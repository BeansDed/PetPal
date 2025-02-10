package com.example.petpal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
    private lateinit var confirmpass_container: TextInputLayout

    // TextField Inputs
    private lateinit var usernameInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var confirmpass_input: TextInputEditText

    // Terms and Conditions CheckBox and TextButtons
    private lateinit var acceptTerms : CheckBox
    private lateinit var terms : TextView
    private lateinit var privacyPolicy : TextView

    // Sign Up Button and Login Suggestion
    private lateinit var signupBtn: ImageView
    private lateinit var loginSugg: TextView

    private val client = OkHttpClient()

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

        // TextField Containers / Text Helpers
        usernameContainer = findViewById(R.id.username_container)
        emailContainer = findViewById(R.id.email_container)
        passwordContainer = findViewById(R.id.password_container)
        confirmpass_container = findViewById(R.id.confirmpass_container)

        // TextField Inputs
        usernameInput = findViewById(R.id.username_input)
        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        confirmpass_input = findViewById(R.id.confirmpass_input)

        // Terms and Conditions CheckBox and TextButtons
        acceptTerms = findViewById(R.id.acceptTerms)
        terms = findViewById(R.id.terms)
        privacyPolicy = findViewById(R.id.privacyPolicy)

        // Sign Up Button and Login Suggestion
        signupBtn = findViewById(R.id.signin_btn)
        loginSugg = findViewById(R.id.login_sugg)


        // Terms Underline Text
        terms.paintFlags = terms.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
        terms.setTextColor(getColor(R.color.smth_orange))

        // Privacy Policy Underline Text
        privacyPolicy.paintFlags = privacyPolicy.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
        privacyPolicy.setTextColor(getColor(R.color.smth_orange))

        // Login Suggestion Underline Text
        loginSugg.paintFlags = loginSugg.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
        loginSugg.setTextColor(getColor(R.color.smth_orange))
    }

    private fun setupButtonListeners() {
        backBtn.setOnClickListener {
            finish()  // Go back to the previous activity
        }
        signupBtn.setOnClickListener {
            if (validateInputs()) {
                performRegistration()
            }
        }
        loginSugg.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }

    private fun setupFocusListeners() {
        usernameInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateUsername()
        }
        emailInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateEmail()
        }
        passwordInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validatePassword()
        }
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
        return if (password.isEmpty()) {
            passwordContainer.error = "Password is required"
            false
        } else if (password.length < 8) {
            passwordContainer.error = "Password must be at least 8 characters"
            false
        } else {
            passwordContainer.error = null
            true
        }
    }

    private fun validateInputs(): Boolean {
        val isUsernameValid = validateUsername()
        val isEmailValid = validateEmail()
        val isPasswordValid = validatePassword()
        return isUsernameValid && isEmailValid && isPasswordValid
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
            .url("http://192.168.1.12/backend/mobile_register.php")  // Change this to your actual endpoint
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
