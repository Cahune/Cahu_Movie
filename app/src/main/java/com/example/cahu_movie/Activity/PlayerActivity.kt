package com.example.cahu_movie.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Color
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
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.cahu_movie.utils.K

class PlayerActivity : BaseActivity() {

    private lateinit var rootContainer:
            FrameLayout

    private var webView: WebView? = null
    private var progressBar: ProgressBar? = null
    private var errorTextView: TextView? = null
    private var backButton: TextView? = null

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

        hideSystemBars()
        createEmbedPlayer(embedUrl)
        createBackToDetailButton()
        configureBackButton()
    }

    private fun configureBackButton() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {
                    when {
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

        rootContainer.addView(
            browserView,
            matchParentLayoutParams()
        )

        rootContainer.addView(
            loadingView,
            centeredLayoutParams()
        )

        rootContainer.addView(
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
        backButton?.visibility = View.GONE

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
            TextView(this).apply {
                text = "Quay lai"
                setTextColor(Color.WHITE)
                textSize = 15f
                gravity = Gravity.CENTER
                setBackgroundColor(Color.argb(170, 0, 0, 0))
                setPadding(
                    28,
                    14,
                    28,
                    14
                )
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    finish()
                }
            }

        backButton = button

        rootContainer.addView(
            button,
            backButtonLayoutParams()
        )
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
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
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

        super.onDestroy()
    }

    companion object {

        private const val EXTRA_EMBED_URL =
            "extra_embed_url"

        private const val EXTRA_EPISODE_NAME =
            "extra_episode_name"

        fun newIntent(
            context: Context,
            embedUrl: String,
            episodeName: String
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
            }
        }
    }
}
