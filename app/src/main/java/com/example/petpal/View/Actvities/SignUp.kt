package com.example.petpal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class SignUp : AppCompatActivity() {

    private lateinit var backBtn: ImageView

    // TextField Containers
    private lateinit var usernameContainer: TextInputLayout
    private lateinit var emailContainer: TextInputLayout
    private lateinit var passwordContainer: TextInputLayout
    private lateinit var confirmPasswordContainer: TextInputLayout

    // TextField Inputs
    private lateinit var usernameInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var confirmPasswordInput: TextInputEditText

    // Terms and Conditions
    private lateinit var acceptTerms: CheckBox
    private lateinit var terms: TextView
    private lateinit var privacyPolicy: TextView

    // Sign Up Button and Login Suggestion
    private lateinit var signupBtn: ImageView
    private lateinit var loginSugg: TextView

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        backBtn = findViewById(R.id.backBtn)

        usernameContainer = findViewById(R.id.username_container)
        emailContainer = findViewById(R.id.email_container)
        passwordContainer = findViewById(R.id.password_container)
        confirmPasswordContainer = findViewById(R.id.confirmpass_container)

        usernameInput = findViewById(R.id.username_input)
        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        confirmPasswordInput = findViewById(R.id.confirmpass_input)

        acceptTerms = findViewById(R.id.acceptTerms)
        terms = findViewById(R.id.terms)
        privacyPolicy = findViewById(R.id.privacyPolicy)

        signupBtn = findViewById(R.id.signin_btn)
        loginSugg = findViewById(R.id.login_sugg)

        // Underline texts for Terms, Privacy, and Login Suggestion
        terms.paintFlags = terms.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
        privacyPolicy.paintFlags = privacyPolicy.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
        loginSugg.paintFlags = loginSugg.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
    }

    private fun setupListeners() {
        backBtn.setOnClickListener {
            finish()
        }

        signupBtn.setOnClickListener {
            if (validateInputs()) {
                performRegistration()
            }
        }

        loginSugg.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }
    }

    private fun validateInputs(): Boolean {
        val usernameValid = validateUsername()
        val emailValid = validateEmail()
        val passwordValid = validatePassword()
        val termsAccepted = validateTerms()

        return usernameValid && emailValid && passwordValid && termsAccepted
    }

    private fun validateUsername(): Boolean {
        val username = usernameInput.text.toString().trim()
        return if (username.isEmpty()) {
            usernameContainer.error = "Username is required"
            false
        } else {
            usernameContainer.error = null
            true
        }
    }

    private fun validateEmail(): Boolean {
        val email = emailInput.text.toString().trim()
        return if (email.isEmpty()) {
            emailContainer.error = "Email is required"
            false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailContainer.error = "Enter a valid email"
            false
        } else {
            emailContainer.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val password = passwordInput.text.toString().trim()
        val confirmPassword = confirmPasswordInput.text.toString().trim()

        return if (password.isEmpty()) {
            passwordContainer.error = "Password is required"
            false
        } else if (password.length < 8) {
            passwordContainer.error = "Password must be at least 8 characters"
            false
        } else if (password != confirmPassword) {
            confirmPasswordContainer.error = "Passwords do not match"
            false
        } else {
            passwordContainer.error = null
            confirmPasswordContainer.error = null
            true
        }
    }

    private fun validateTerms(): Boolean {
        return if (!acceptTerms.isChecked) {
            Toast.makeText(this, "You must accept the terms and conditions.", Toast.LENGTH_LONG).show()
            false
        } else {
            true
        }
    }

    private fun performRegistration() {
        val username = usernameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        val jsonObject = JSONObject().apply {
            put("username", username)
            put("email", email)
            put("password", password)
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonObject.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("http://192.168.1.12/backend/mobile_register.php")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("SignUp", "Registration failed", e)
                runOnUiThread {
                    Toast.makeText(this@SignUp, "Registration failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseData = response.body?.string()
                    val jsonResponse = JSONObject(responseData)

                    runOnUiThread {
                        if (jsonResponse.optBoolean("success", false)) {
                            Toast.makeText(this@SignUp, "Registration successful! Please log in.", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this@SignUp, Login::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@SignUp, jsonResponse.optString("message", "Registration failed"), Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("SignUp", "Error processing response", e)
                    runOnUiThread {
                        Toast.makeText(this@SignUp, "Error processing response: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }
}
