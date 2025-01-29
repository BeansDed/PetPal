package com.example.petpal

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.blue
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

fun loginAccs() {
    val login_username = String
    val login_password = String
}

class Login : AppCompatActivity() {

    private lateinit var backBtn: ImageView
    private lateinit var username_input: EditText
    private lateinit var password_input: EditText
    private lateinit var rememberMe: ImageView
    private lateinit var rememberBtn: TextView
    private lateinit var forgotBtn: TextView
    private lateinit var loginBtn: ImageView
    private lateinit var signup_suggestion: TextView

    private lateinit var username_container: com.google.android.material.textfield.TextInputLayout
    private lateinit var password_container: com.google.android.material.textfield.TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val login_username = String
        val login_password = String

        backBtn = findViewById(R.id.backBtn)
        username_input = findViewById(R.id.username_input)
        password_input = findViewById(R.id.password_input)
        rememberMe = findViewById(R.id.rememberMe)
        rememberBtn = findViewById(R.id.rememberBtn)
        forgotBtn = findViewById(R.id.forgotBtn)
        loginBtn = findViewById(R.id.loginBtn)
        signup_suggestion = findViewById(R.id.signup_suggestion)

        username_container = findViewById(R.id.username_container)
        password_container = findViewById(R.id.password_container)

        signup_suggestion.paintFlags =
            signup_suggestion.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
        signup_suggestion.setTextColor(getColor(R.color.blue))

        backBtn.setOnClickListener {
            val intent = Intent(this, Welcome::class.java)
            startActivity(intent)
        }

        signup_suggestion.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }
}