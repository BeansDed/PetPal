package com.example.petpal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout

class SignUp : AppCompatActivity() {

    private lateinit var backBtn: ImageView
    private lateinit var username_input: EditText
    private lateinit var email_input: EditText
    private lateinit var password_input: EditText
    private lateinit var signin_btn: ImageView
    private lateinit var rememberBtn: ImageView
    private lateinit var rememberMe: TextView
    private lateinit var forgotBtn: TextView
    private lateinit var googleBtn: ImageView
    private lateinit var login_sugg: TextView

    private lateinit var username_container: TextInputLayout
    private lateinit var email_container: TextInputLayout
    private lateinit var password_container: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupButtonListeners()
        setupFocusListeners()
    }

    private fun initViews() {
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
    }

    private fun setupButtonListeners() {
        backBtn.setOnClickListener {
            finish()  // Go back to the previous activity
        }
        signin_btn.setOnClickListener {
            if (validateInputs()) {
                val intent = Intent(this, CatalogActivity::class.java)
                startActivity(intent)
            }
        }
        login_sugg.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }

    private fun setupFocusListeners() {
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

    private fun validateUsername(): Boolean {
        val username = username_input.text.toString().trim()
        return if (username.isEmpty()) {
            username_container.error = "Username is required"
            false
        } else {
            username_container.error = null
            true
        }
    }

    private fun validateEmail(): Boolean {
        val email = email_input.text.toString().trim()
        Log.d("SignUpActivity", "Validating email: $email") // Debugging output
        return if (email.isEmpty()) {
            email_container.error = "Email is required"
            false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_container.error = "Enter a valid email"
            false
        } else {
            email_container.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val password = password_input.text.toString().trim()
        return if (password.isEmpty()) {
            password_container.error = "Password is required"
            false
        } else if (password.length < 8) {  // Assuming a minimum of 8 characters for the password
            password_container.error = "Password must be at least 8 characters"
            false
        } else {
            password_container.error = null
            true
        }
    }

    private fun validateInputs(): Boolean {
        val isUsernameValid = validateUsername()
        val isEmailValid = validateEmail()
        val isPasswordValid = validatePassword()
        return isUsernameValid && isEmailValid && isPasswordValid
    }
}
