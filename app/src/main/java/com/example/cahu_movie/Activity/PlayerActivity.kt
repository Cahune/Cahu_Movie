package com.example.cahu_movie.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.cahu_movie.R
import com.example.cahu_movie.utils.K
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlayerActivity : BaseActivity() {

    private lateinit var rootContainer:
            FrameLayout
    private lateinit var playerContainer:
            FrameLayout

    private var webView: WebView? = null
    private var progressBar: ProgressBar? = null
    private var errorTextView: TextView? = null
    private var backButton: ImageButton? = null
    private var settingsButton: ImageButton? = null
    private var settingsPanel: View? = null
    private var episodeListContainer: LinearLayout? = null
    private var playerHeaderTextView: TextView? = null
    private var currentEpisodeKey: String = ""
    private var currentEpisodeTitle: String = ""
    private var episodes: List<PlayerEpisode> = emptyList()

    private var fullscreenView: View? = null

    private var fullscreenContainer:
            FrameLayout? = null

    private var fullscreenCallback:
            WebChromeClient.CustomViewCallback? = null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        window.addFlags(
            WindowManager.LayoutParams
                .FLAG_KEEP_SCREEN_ON
        )

        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )

        requestedOrientation =
            ActivityInfo
                .SCREEN_ORIENTATION_PORTRAIT

        val embedUrl = intent
            .getStringExtra(EXTRA_EMBED_URL)
            .orEmpty()
            .trim()

        val episodeName = intent
            .getStringExtra(EXTRA_EPISODE_NAME)
            .orEmpty()
            .trim()
        currentEpisodeTitle = episodeName
        currentEpisodeKey = intent
            .getStringExtra(EXTRA_EPISODE_KEY)
            .orEmpty()
            .trim()
        episodes = parseEpisodes(
            intent.getStringExtra(EXTRA_EPISODES_JSON)
        )

        if (episodeName.isNotBlank()) {
            title = episodeName
        }

        if (embedUrl.isBlank()) {
            finish()
            return
        }

        rootContainer =
            FrameLayout(this).apply {
                setBackgroundColor(Color.BLACK)
            }

        setContentView(rootContainer)
        createPlayerLayout()

        hideSystemBars()
        createEmbedPlayer(embedUrl)
        renderEpisodeList()
        createBackToDetailButton()
        createSettingsButton()
        createSettingsPanel()
        configureBackButton()
    }

    private fun createPlayerLayout() {
        val contentContainer =
            LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setBackgroundColor(Color.BLACK)
            }

        val headerView =
            TextView(this).apply {
                text = currentEpisodeTitle.ifBlank {
                    "Đang phát phim"
                }
                setTextColor(Color.WHITE)
                textSize = 18f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                maxLines = 1
                ellipsize = android.text.TextUtils.TruncateAt.END
                setBackgroundColor(Color.rgb(17, 17, 26))
                setPadding(
                    72.dpToPx(),
                    72.dpToPx(),
                    72.dpToPx(),
                    14.dpToPx()
                )
            }

        playerHeaderTextView = headerView

        playerContainer =
            FrameLayout(this).apply {
                setBackgroundColor(Color.BLACK)
            }

        val episodeScrollView =
            ScrollView(this).apply {
                setBackgroundColor(Color.rgb(11, 11, 18))
            }

        val listContainer =
            LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(
                    14.dpToPx(),
                    16.dpToPx(),
                    14.dpToPx(),
                    24.dpToPx()
                )
            }

        episodeListContainer = listContainer
        episodeScrollView.addView(
            listContainer,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        contentContainer.addView(
            headerView,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        contentContainer.addView(
            playerContainer,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                260.dpToPx()
            )
        )
        contentContainer.addView(
            episodeScrollView,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        rootContainer.addView(
            contentContainer,
            matchParentLayoutParams()
        )
    }

    private fun configureBackButton() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {
                    when {
                        settingsPanel?.visibility == View.VISIBLE -> {
                            hideSettingsPanel()
                        }

                        fullscreenView != null -> {
                            exitFullscreen()
                        }

                        webView?.canGoBack() == true -> {
                            webView?.goBack()
                        }

                        else -> {
                            finish()
                        }
                    }
                }
            }
        )
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun createEmbedPlayer(
        embedUrl: String
    ) {
        val loadingView =
            ProgressBar(this).apply {
                visibility = View.VISIBLE
            }

        progressBar = loadingView

        val errorView =
            createErrorTextView()

        errorTextView = errorView

        val browserView =
            WebView(this).apply {
                setBackgroundColor(Color.BLACK)

                setLayerType(
                    View.LAYER_TYPE_HARDWARE,
                    null
                )

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    databaseEnabled = true

                    mediaPlaybackRequiresUserGesture =
                        false

                    javaScriptCanOpenWindowsAutomatically =
                        true

                    mixedContentMode =
                        WebSettings
                            .MIXED_CONTENT_ALWAYS_ALLOW

                    useWideViewPort = true
                    loadWithOverviewMode = true
                    loadsImagesAutomatically = true

                    allowFileAccess = false
                    allowContentAccess = true

                    builtInZoomControls = false
                    displayZoomControls = false

                    setSupportZoom(false)

                    setSupportMultipleWindows(
                        false
                    )

                    userAgentString =
                        userAgentString
                            .replace(
                                "; wv",
                                ""
                            )
                            .replace(
                                "Version/4.0 ",
                                ""
                            )
                }
            }

        webView = browserView

        CookieManager
            .getInstance()
            .apply {
                setAcceptCookie(true)

                setAcceptThirdPartyCookies(
                    browserView,
                    true
                )
            }

        browserView.webViewClient =
            object : WebViewClient() {

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    return false
                }

                override fun onPageStarted(
                    view: WebView?,
                    url: String?,
                    favicon: Bitmap?
                ) {
                    super.onPageStarted(
                        view,
                        url,
                        favicon
                    )

                    progressBar?.visibility =
                        View.VISIBLE

                    errorTextView?.visibility =
                        View.GONE
                }

                override fun onPageFinished(
                    view: WebView?,
                    url: String?
                ) {
                    super.onPageFinished(
                        view,
                        url
                    )

                    progressBar?.visibility =
                        View.GONE
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(
                        view,
                        request,
                        error
                    )

                    if (
                        request?.isForMainFrame == true
                    ) {
                        progressBar?.visibility =
                            View.GONE

                        showError(
                            error
                                ?.description
                                ?.toString()
                                ?.takeIf {
                                    it.isNotBlank()
                                }
                                ?: "Không thể tải trình phát phim"
                        )
                    }
                }

                override fun onReceivedHttpError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    errorResponse:
                    WebResourceResponse?
                ) {
                    super.onReceivedHttpError(
                        view,
                        request,
                        errorResponse
                    )

                    if (
                        request?.isForMainFrame == true
                    ) {
                        progressBar?.visibility =
                            View.GONE

                        val statusCode =
                            errorResponse
                                ?.statusCode

                        showError(
                            if (statusCode != null) {
                                "Máy chủ phim trả về lỗi HTTP $statusCode"
                            } else {
                                "Máy chủ phim không phản hồi"
                            }
                        )
                    }
                }
            }

        browserView.webChromeClient =
            object : WebChromeClient() {

                override fun onProgressChanged(
                    view: WebView?,
                    newProgress: Int
                ) {
                    super.onProgressChanged(
                        view,
                        newProgress
                    )

                    progressBar?.visibility =
                        if (newProgress < 100) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                }

                override fun onShowCustomView(
                    view: View?,
                    callback:
                    CustomViewCallback?
                ) {
                    if (
                        view == null ||
                        callback == null
                    ) {
                        return
                    }

                    showFullscreenView(
                        view = view,
                        callback = callback
                    )
                }

                override fun onHideCustomView() {
                    exitFullscreen()
                }
            }

        playerContainer.addView(
            browserView,
            matchParentLayoutParams()
        )

        playerContainer.addView(
            loadingView,
            centeredLayoutParams()
        )

        playerContainer.addView(
            errorView,
            centeredLayoutParams()
        )

        browserView.loadUrl(
            embedUrl,
            mapOf(
                "Referer" to K.BASE_URL
            )
        )
    }

    private fun renderEpisodeList() {
        val container = episodeListContainer ?: return
        container.removeAllViews()

        if (episodes.isEmpty()) {
            return
        }

        container.addView(
            TextView(this).apply {
                text = "Danh sách tập"
                setTextColor(Color.WHITE)
                textSize = 18f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setPadding(
                    0,
                    0,
                    0,
                    12.dpToPx()
                )
            }
        )

        episodes.groupBy { episode ->
            episode.serverName
                ?.trim()
                ?.takeIf { it.isNotBlank() }
                ?: "Máy chủ"
        }.forEach { (serverName, serverEpisodes) ->
            container.addView(
                TextView(this).apply {
                    text = serverName
                    setTextColor(Color.rgb(255, 64, 129))
                    textSize = 15f
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    setPadding(
                        0,
                        12.dpToPx(),
                        0,
                        8.dpToPx()
                    )
                }
            )

            serverEpisodes.chunked(3).forEach { episodeRow ->
                val row =
                    LinearLayout(this).apply {
                        orientation = LinearLayout.HORIZONTAL
                        gravity = Gravity.CENTER
                    }

                episodeRow.forEach { episode ->
                    row.addView(
                        episodeButton(episode),
                        LinearLayout.LayoutParams(
                            0,
                            44.dpToPx(),
                            1f
                        ).apply {
                            setMargins(
                                4.dpToPx(),
                                4.dpToPx(),
                                4.dpToPx(),
                                4.dpToPx()
                            )
                        }
                    )
                }

                repeat(3 - episodeRow.size) {
                    row.addView(
                        View(this),
                        LinearLayout.LayoutParams(
                            0,
                            44.dpToPx(),
                            1f
                        ).apply {
                            setMargins(
                                4.dpToPx(),
                                4.dpToPx(),
                                4.dpToPx(),
                                4.dpToPx()
                            )
                        }
                    )
                }

                container.addView(row)
            }
        }
    }

    private fun episodeButton(
        episode: PlayerEpisode
    ): TextView {
        val selected =
            episode.key() == currentEpisodeKey

        return TextView(this).apply {
            text = episodeButtonName(episode)
            setTextColor(Color.WHITE)
            textSize = 13f
            gravity = Gravity.CENTER
            maxLines = 1
            background = GradientDrawable().apply {
                cornerRadius = 10.dpToPx().toFloat()
                setColor(
                    if (selected) {
                        Color.rgb(229, 9, 20)
                    } else {
                        Color.rgb(34, 29, 44)
                    }
                )
            }
            isClickable = true
            isFocusable = true
            setOnClickListener {
                loadEpisode(episode)
            }
        }
    }

    private fun loadEpisode(
        episode: PlayerEpisode
    ) {
        val embedUrl = episode.embed
            ?.trim()
            .orEmpty()

        if (embedUrl.isBlank()) {
            Toast.makeText(
                this,
                "Tập này chưa có đường dẫn phát",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        currentEpisodeKey = episode.key()
        currentEpisodeTitle = episodeDisplayName(episode)
        title = currentEpisodeTitle
        playerHeaderTextView?.text = currentEpisodeTitle
        webView?.loadUrl(
            embedUrl,
            mapOf(
                "Referer" to K.BASE_URL
            )
        )
        renderEpisodeList()
    }

    private fun showFullscreenView(
        view: View,
        callback:
        WebChromeClient.CustomViewCallback
    ) {
        if (fullscreenView != null) {
            callback.onCustomViewHidden()
            return
        }

        fullscreenView = view
        fullscreenCallback = callback

        webView?.visibility = View.GONE

        val container =
            FrameLayout(this).apply {
                setBackgroundColor(Color.BLACK)

                addView(
                    view,
                    matchParentLayoutParams()
                )
            }

        fullscreenContainer = container

        rootContainer.addView(
            container,
            matchParentLayoutParams()
        )

        backButton?.visibility = View.VISIBLE
        settingsButton?.visibility = View.VISIBLE
        bringPlayerControlsToFront()

        requestedOrientation =
            ActivityInfo
                .SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        hideSystemBars()
    }

    private fun exitFullscreen() {
        val currentFullscreenView =
            fullscreenView
                ?: return

        fullscreenContainer
            ?.removeView(
                currentFullscreenView
            )

        fullscreenContainer
            ?.let { container ->
                rootContainer.removeView(
                    container
                )
            }

        fullscreenView = null
        fullscreenContainer = null

        webView?.visibility = View.VISIBLE
        backButton?.visibility = View.VISIBLE
        settingsButton?.visibility = View.VISIBLE
        bringPlayerControlsToFront()

        val callback = fullscreenCallback
        fullscreenCallback = null

        callback?.onCustomViewHidden()

        requestedOrientation =
            ActivityInfo
                .SCREEN_ORIENTATION_PORTRAIT

        hideSystemBars()
    }

    private fun createBackToDetailButton() {
        val button =
            ImageButton(this).apply {
                setImageResource(
                    R.drawable.ic_arrow_back_24
                )
                setColorFilter(Color.WHITE)
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(Color.argb(170, 0, 0, 0))
                }
                scaleType = ImageView.ScaleType.CENTER
                isClickable = true
                isFocusable = true
                contentDescription = "Quay lại"
                setOnClickListener {
                    finish()
                }
            }

        backButton = button

        rootContainer.addView(
            button,
            backButtonLayoutParams()
        )

        button.bringToFront()
    }

    private fun createSettingsButton() {
        val button =
            ImageButton(this).apply {
                setImageResource(
                    R.drawable.ic_settings_24
                )
                setColorFilter(Color.WHITE)
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(Color.argb(170, 0, 0, 0))
                }
                scaleType = ImageView.ScaleType.CENTER
                isClickable = true
                isFocusable = true
                contentDescription = "Cài đặt"
                setOnClickListener {
                    toggleSettingsPanel()
                }
            }

        settingsButton = button

        rootContainer.addView(
            button,
            settingsButtonLayoutParams()
        )

        button.bringToFront()
    }

    private fun createSettingsPanel() {
        val panel =
            LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                visibility = View.GONE
                setPadding(
                    24.dpToPx(),
                    18.dpToPx(),
                    24.dpToPx(),
                    24.dpToPx()
                )
                background = GradientDrawable().apply {
                    setColor(Color.argb(238, 17, 17, 26))
                    cornerRadii = floatArrayOf(
                        20.dpToPx().toFloat(),
                        20.dpToPx().toFloat(),
                        20.dpToPx().toFloat(),
                        20.dpToPx().toFloat(),
                        0f,
                        0f,
                        0f,
                        0f
                    )
                }

                addView(
                    settingsText(
                        text = "Cài đặt phát phim",
                        textSize = 18f,
                        bold = true
                    )
                )
                addView(
                    settingsText(
                        text = "Chất lượng: Tự động",
                        textSize = 15f
                    )
                )
                addView(
                    settingsText(
                        text = "Tốc độ phát: Mặc định",
                        textSize = 15f
                    )
                )
            }

        settingsPanel = panel

        rootContainer.addView(
            panel,
            settingsPanelLayoutParams()
        )

        panel.post {
            panel.translationY = panel.height.toFloat()
        }
    }

    private fun settingsText(
        text: String,
        textSize: Float,
        bold: Boolean = false
    ): TextView {
        return TextView(this).apply {
            this.text = text
            setTextColor(Color.WHITE)
            this.textSize = textSize
            if (bold) {
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
            setPadding(
                0,
                0,
                0,
                12.dpToPx()
            )
        }
    }

    private fun toggleSettingsPanel() {
        val panel = settingsPanel ?: return

        if (panel.visibility == View.VISIBLE) {
            hideSettingsPanel()
        } else {
            showSettingsPanel()
        }
    }

    private fun showSettingsPanel() {
        settingsPanel?.apply {
            visibility = View.VISIBLE
            bringToFront()
            translationY = height.toFloat()
            animate()
                .translationY(0f)
                .setDuration(220L)
                .start()
        }

        bringPlayerControlsToFront()
    }

    private fun hideSettingsPanel() {
        settingsPanel?.apply {
            animate()
                .translationY(height.toFloat())
                .setDuration(180L)
                .withEndAction {
                    visibility = View.GONE
                }
                .start()
        }
    }

    private fun bringPlayerControlsToFront() {
        settingsPanel
            ?.takeIf { panel ->
                panel.visibility == View.VISIBLE
            }
            ?.bringToFront()
        backButton?.bringToFront()
        settingsButton?.bringToFront()
    }

    private fun createErrorTextView():
            TextView {
        return TextView(this).apply {
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.BLACK)

            textSize = 15f
            gravity = Gravity.CENTER

            setPadding(
                48,
                24,
                48,
                24
            )

            visibility = View.GONE
        }
    }

    private fun showError(
        message: String
    ) {
        errorTextView?.apply {
            text = message
            visibility = View.VISIBLE
        }
    }

    private fun hideSystemBars() {
        val controller =
            WindowCompat.getInsetsController(
                window,
                window.decorView
            )

        controller.systemBarsBehavior =
            WindowInsetsControllerCompat
                .BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        controller.hide(
            WindowInsetsCompat.Type.systemBars()
        )
    }

    private fun matchParentLayoutParams():
            FrameLayout.LayoutParams {
        return FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    private fun centeredLayoutParams():
            FrameLayout.LayoutParams {
        return FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.CENTER
        )
    }

    private fun backButtonLayoutParams():
            FrameLayout.LayoutParams {
        return FrameLayout.LayoutParams(
            56.dpToPx(),
            56.dpToPx(),
            Gravity.TOP or Gravity.START
        ).apply {
            setMargins(
                32,
                72,
                0,
                0
            )
        }
    }

    private fun settingsButtonLayoutParams():
            FrameLayout.LayoutParams {
        return FrameLayout.LayoutParams(
            56.dpToPx(),
            56.dpToPx(),
            Gravity.TOP or Gravity.END
        ).apply {
            setMargins(
                0,
                72,
                32,
                0
            )
        }
    }

    private fun settingsPanelLayoutParams():
            FrameLayout.LayoutParams {
        return FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.BOTTOM
        )
    }

    private fun parseEpisodes(
        episodesJson: String?
    ): List<PlayerEpisode> {
        if (episodesJson.isNullOrBlank()) {
            return emptyList()
        }

        return runCatching {
            val type = object :
                TypeToken<List<PlayerEpisode>>() {}.type
            Gson().fromJson<List<PlayerEpisode>>(
                episodesJson,
                type
            )
        }.getOrDefault(emptyList())
    }

    private fun episodeButtonName(
        episode: PlayerEpisode
    ): String {
        val episodeName = episode.name
            ?.trim()
            .orEmpty()

        return if (episodeName.isBlank()) {
            "Xem tập"
        } else {
            "Tập $episodeName"
        }
    }

    private fun episodeDisplayName(
        episode: PlayerEpisode
    ): String {
        val episodeName = episode.name
            ?.trim()
            .orEmpty()

        return if (episodeName.isBlank()) {
            "Đang phát phim"
        } else {
            "Tập $episodeName"
        }
    }

    private fun PlayerEpisode.key(): String {
        return listOf(
            serverName.orEmpty(),
            slug.orEmpty(),
            name.orEmpty(),
            embed.orEmpty()
        ).joinToString("|")
    }

    private fun Int.dpToPx(): Int {
        return (
                this * resources.displayMetrics.density
                ).toInt()
    }

    override fun onResume() {
        super.onResume()

        webView?.onResume()
        webView?.resumeTimers()

        hideSystemBars()
    }

    override fun onPause() {
        webView?.onPause()

        super.onPause()
    }

    override fun onWindowFocusChanged(
        hasFocus: Boolean
    ) {
        super.onWindowFocusChanged(
            hasFocus
        )

        if (hasFocus) {
            hideSystemBars()
        }
    }

    override fun onConfigurationChanged(
        newConfig: Configuration
    ) {
        super.onConfigurationChanged(
            newConfig
        )

        hideSystemBars()
    }

    override fun onDestroy() {
        if (fullscreenView != null) {
            exitFullscreen()
        }

        webView?.apply {
            onPause()
            stopLoading()
            loadUrl("about:blank")
            clearHistory()
            clearCache(false)
            removeAllViews()
            destroy()
        }

        webView = null
        progressBar = null
        errorTextView = null
        playerHeaderTextView = null
        settingsButton = null
        settingsPanel = null

        super.onDestroy()
    }

    companion object {

        private const val EXTRA_EMBED_URL =
            "extra_embed_url"

        private const val EXTRA_EPISODE_NAME =
            "extra_episode_name"

        private const val EXTRA_EPISODE_KEY =
            "extra_episode_key"

        private const val EXTRA_EPISODES_JSON =
            "extra_episodes_json"

        fun newIntent(
            context: Context,
            embedUrl: String,
            episodeName: String,
            episodeKey: String = "",
            episodesJson: String = ""
        ): Intent {
            return Intent(
                context,
                PlayerActivity::class.java
            ).apply {
                putExtra(
                    EXTRA_EMBED_URL,
                    embedUrl
                )

                putExtra(
                    EXTRA_EPISODE_NAME,
                    episodeName
                )

                putExtra(
                    EXTRA_EPISODE_KEY,
                    episodeKey
                )

                putExtra(
                    EXTRA_EPISODES_JSON,
                    episodesJson
                )
            }
        }
    }
}
