package com.example.cahu_movie.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cahu_movie.R
import androidx.compose.ui.unit.sp

class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen(onLoginClick = {
                startActivity(Intent((this), MainActivity::class.java))
            })
        }
    }
}

@Composable
@Preview
fun LoginScreenPreview(){
    LoginScreen(onLoginClick = {})
}

@Composable
fun LoginScreen(onLoginClick:()-> Unit){
    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = colorResource((R.color.blackBackground)))){
        Image(
            painter = painterResource(id = R.drawable.bg1),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier.matchParentSize()
        )
        Column(modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp, vertical = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(128.dp))
            Text(text = "Log in",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(128.dp))
            GradientTextField(
                hint = "Username",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            GradientTextField(
                hint = "Password",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Forget your password?",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(64.dp))
            GradientButton(
                text = "Login",
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            )
        }
    }
}

@Composable
fun GradientButton(
    text: String,
    onClick:()-> Unit,
    modifier: Modifier= Modifier
){
    Button(onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(60.dp),
        border = BorderStroke(
            width = 4.dp, brush = Brush.linearGradient(
                colors = listOf(colorResource(R.color.pink), colorResource(R.color.green))
            )
        ),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color.Transparent,
            contentColor = Color.White)
    ) {
        Text(text=text,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White)
    }
}

@Composable
fun GradientTextField(
    hint:String,
    modifier: Modifier=Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
    ){
    Box(modifier = modifier
        .height(60.dp)
        .background(
            brush = Brush.linearGradient(
                colors = listOf(colorResource(R.color.pink), colorResource(R.color.green))
            ),
            shape = RoundedCornerShape(50.dp)
        )
        .padding(4.dp)
    ){
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = {
                Text(
                    text = hint,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            singleLine = true,
            textStyle = TextStyle(
                color = Color.White,
                textAlign = TextAlign.Center
            ),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,

                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,

                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            keyboardOptions = keyboardOptions,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = colorResource(R.color.black1),
                    shape = RoundedCornerShape(50.dp)
                )
                .align(Alignment.Center)
        )
    }
    }