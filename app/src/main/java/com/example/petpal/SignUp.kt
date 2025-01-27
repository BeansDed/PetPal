package com.example.petpal

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SignUp : AppCompatActivity() {

    private lateinit var backBtn : ImageView
    private lateinit var username_input : EditText
    private lateinit var email_input : EditText
    private lateinit var password_input : EditText
    private lateinit var signin_btn : ImageView
    private lateinit var rememberBtn : ImageView
    private lateinit var rememberMe : TextView
    private lateinit var forgotBtn : TextView
    private lateinit var googleBtn : ImageView
    private lateinit var login_sugg : TextView

    private lateinit var username_container : com.google.android.material.textfield.TextInputLayout
    private lateinit var email_container : com.google.android.material.textfield.TextInputLayout
    private lateinit var password_container : com.google.android.material.textfield.TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        backBtn = findViewById(R.id.backBtn)
        username_input = findViewById(R.id.username_input)
        email_input = findViewById(R.id.email_input)
        password_input = findViewById(R.id.password_input)
        signin_btn = findViewById(R.id.signin_btn)
        rememberBtn = findViewById(R.id.rememberBtn)
        rememberMe = findViewById(R.id.rememberMe)
        forgotBtn = findViewById(R.id.forgotBtn)
        googleBtn = findViewById(R.id.googleBtn)
        login_sugg = findViewById(R.id.login_sugg)

        username_container = findViewById(R.id.username_container)
        email_container = findViewById(R.id.email_container)
        password_container = findViewById(R.id.password_container)

        login_sugg.paintFlags = login_sugg.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
        login_sugg.setTextColor(getColor(R.color.smth_black))

        backBtn.setOnClickListener {
            val intent = Intent(this, Welcome::class.java)
            startActivity(intent)
        }

        login_sugg.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        fun setupFocusListeners() {
            username_input.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) validateUsername()
            }

            email_input.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) validateEmail()
            }

            password_input.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) validatePassword()
            }
        }

        setupFocusListeners()
    }

    fun validateUsername() {
        val username = username_input.text.toString().trim()
        if (username.isEmpty()) {
            username_container.error = "Username is required"
        } else {
            username_container.error = null // Clear error
        }
    }

    fun validateEmail() {
        val email = email_input.text.toString().trim()
        if (email.isEmpty()) {
            email_container.error = "Email is required"
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_container.error = "Enter a valid email"
        } else {
            email_container.error = null // Clear error
        }
    }

    fun validatePassword() {
        val password = password_input.text.toString().trim()
        if (password.isEmpty()) {
            password_container.error = "Password is required"
        } else if (password.length < 16) {
            password_container.error = "Password must not exceed at least 16 characters"
        } else {
            password_container.error = null // Clear error
        }
    }
}