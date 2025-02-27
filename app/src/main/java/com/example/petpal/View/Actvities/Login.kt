package com.example.petpal

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class Login : AppCompatActivity() {

    private lateinit var usernameInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginBtn: ImageView
    private lateinit var signupSuggestion: TextView
    private lateinit var rememberMe: CheckBox
    private lateinit var sharedPreferences: SharedPreferences
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("PetPalPrefs", MODE_PRIVATE)

        // Auto-login if user is already logged in
        if (sharedPreferences.contains("user_id")) {
            Log.d("Login", "User is already logged in, redirecting...")
            startCatalogActivity()
            finish()
            return
        }

        // Initialize views
        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.loginBtn)
        signupSuggestion = findViewById(R.id.signup_suggestion)
        rememberMe = findViewById(R.id.rememberMe)
        val backBtn: ImageView = findViewById(R.id.backBtn)

        // Set back button to navigate to WelcomeActivity (activity_welcome.xml)
        backBtn.setOnClickListener {
            startActivity(Intent(this, Welcome::class.java))
            finish()
        }

        // Populate credentials if they were remembered
        val rememberedUsername = sharedPreferences.getString("remembered_username", "")
        val rememberedPassword = sharedPreferences.getString("remembered_password", "")
        if (!rememberedUsername.isNullOrEmpty() && !rememberedPassword.isNullOrEmpty()) {
            usernameInput.setText(rememberedUsername)
            passwordInput.setText(rememberedPassword)
            rememberMe.isChecked = true
        }

        // Login button click listener
        loginBtn.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            performLogin(username, password)
        }

        // Navigate to SignUp activity on suggestion click
        signupSuggestion.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
        }
    }

    private fun performLogin(username: String, password: String) {
        val jsonObject = JSONObject().apply {
            put("username", username)
            put("password", password)
        }

        val requestBody = jsonObject
            .toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

        // Replace with your actual backend login URL
        val request = Request.Builder()
            .url("http://192.168.1.12/backend/mobile_login.php")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Login", "Login failed", e)
                runOnUiThread {
                    Toast.makeText(this@Login, "Login failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                val jsonResponse = JSONObject(responseData ?: "{}")
                runOnUiThread {
                    if (jsonResponse.optBoolean("success", false)) {
                        // Save basic user data in SharedPreferences
                        sharedPreferences.edit().apply {
                            putInt("user_id", jsonResponse.getInt("user_id"))
                            putString("username", jsonResponse.getString("username"))
                            apply()
                        }
                        // Remember credentials if checkbox is checked
                        if (rememberMe.isChecked) {
                            sharedPreferences.edit().apply {
                                putString("remembered_username", username)
                                putString("remembered_password", password)
                                apply()
                            }
                        } else {
                            sharedPreferences.edit().remove("remembered_username")
                                .remove("remembered_password")
                                .apply()
                        }
                        startCatalogActivity()
                        finish()
                    } else {
                        val message = jsonResponse.optString("message", "Login failed")
                        Toast.makeText(this@Login, message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun startCatalogActivity() {
        startActivity(Intent(this, CatalogActivity::class.java))
    }
}
