package com.example.petpal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
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

    // Top navigation
    private lateinit var backBtn: ImageView

    // TextField Containers
    private lateinit var usernameContainer: TextInputLayout
    private lateinit var emailContainer: TextInputLayout
    private lateinit var locationContainer: TextInputLayout
    private lateinit var ageContainer: TextInputLayout
    private lateinit var contactContainer: TextInputLayout
    private lateinit var passwordContainer: TextInputLayout
    private lateinit var confirmpassContainer: TextInputLayout

    // TextField Inputs
    private lateinit var usernameInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var locationInput: TextInputEditText
    private lateinit var ageInput: TextInputEditText
    private lateinit var contactInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var confirmpassInput: TextInputEditText

    // Terms and Conditions
    private lateinit var acceptTerms: CheckBox
    private lateinit var terms: TextView
    private lateinit var privacyPolicy: TextView

    // Register Button and Login Suggestion
    private lateinit var signupBtn: Button
    private lateinit var loginSugg: TextView

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        // Top design/back button
        backBtn = findViewById(R.id.backBtn)

        // Containers
        usernameContainer = findViewById(R.id.username_container)
        emailContainer = findViewById(R.id.email_container)
        locationContainer = findViewById(R.id.location_container)
        ageContainer = findViewById(R.id.age_container)
        contactContainer = findViewById(R.id.contact_container)
        passwordContainer = findViewById(R.id.password_container)
        confirmpassContainer = findViewById(R.id.confirmpass_container)

        // Input fields
        usernameInput = findViewById(R.id.username_input)
        emailInput = findViewById(R.id.email_input)
        locationInput = findViewById(R.id.location_input)
        ageInput = findViewById(R.id.age_input)
        contactInput = findViewById(R.id.contact_input)
        passwordInput = findViewById(R.id.password_input)
        confirmpassInput = findViewById(R.id.confirmpass_input)

        // Terms & Conditions and policy
        acceptTerms = findViewById(R.id.acceptTerms)
        terms = findViewById(R.id.terms)
        privacyPolicy = findViewById(R.id.privacyPolicy)

        // Sign Up button and login suggestion
        signupBtn = findViewById(R.id.signin_btn)
        loginSugg = findViewById(R.id.login_sugg)

        // Underline texts for clickable items
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
        var isValid = true

        // Validate username
        val username = usernameInput.text.toString().trim()
        if (username.isEmpty()) {
            usernameContainer.error = "Username is required"
            isValid = false
        } else {
            usernameContainer.error = null
        }

        // Validate email
        val email = emailInput.text.toString().trim()
        if (email.isEmpty()) {
            emailContainer.error = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailContainer.error = "Enter a valid email"
            isValid = false
        } else {
            emailContainer.error = null
        }

        // Validate location
        val location = locationInput.text.toString().trim()
        if (location.isEmpty()) {
            locationContainer.error = "Location is required"
            isValid = false
        } else {
            locationContainer.error = null
        }

        // Validate age
        val age = ageInput.text.toString().trim()
        if (age.isEmpty()) {
            ageContainer.error = "Age is required"
            isValid = false
        } else {
            ageContainer.error = null
        }

        // Validate contact number
        val contact = contactInput.text.toString().trim()
        if (contact.isEmpty()) {
            contactContainer.error = "Contact number is required"
            isValid = false
        } else {
            contactContainer.error = null
        }

        // Validate password and confirm password
        val password = passwordInput.text.toString().trim()
        val confirmPassword = confirmpassInput.text.toString().trim()
        if (password.isEmpty()) {
            passwordContainer.error = "Password is required"
            isValid = false
        } else if (password.length < 8) {
            passwordContainer.error = "Password must be at least 8 characters"
            isValid = false
        } else {
            passwordContainer.error = null
        }
        if (confirmPassword.isEmpty()) {
            confirmpassContainer.error = "Confirm your password"
            isValid = false
        } else if (password != confirmPassword) {
            confirmpassContainer.error = "Passwords do not match"
            isValid = false
        } else {
            confirmpassContainer.error = null
        }

        // Validate Terms and Conditions
        if (!acceptTerms.isChecked) {
            Toast.makeText(this, "You must accept the terms and conditions.", Toast.LENGTH_LONG).show()
            isValid = false
        }

        return isValid
    }

    private fun performRegistration() {
        val username = usernameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val location = locationInput.text.toString().trim()
        val age = ageInput.text.toString().trim()
        val contact = contactInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        val jsonObject = JSONObject().apply {
            put("username", username)
            put("email", email)
            put("location", location)
            put("age", age)
            put("contact_number", contact)
            put("password", password)
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonObject.toString().toRequestBody(mediaType)

        // Update the URL to match your backend endpoint.
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
