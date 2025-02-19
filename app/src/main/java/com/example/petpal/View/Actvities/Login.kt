package com.example.petpal

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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

    // Using TextInputEditText (as defined in XML)
    private lateinit var usernameInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText

    // Using ImageView for the login button, as in your XML
    private lateinit var loginBtn: ImageView

    // Using TextView for the sign-up suggestion
    private lateinit var signupSuggestion: TextView

    private lateinit var sharedPreferences: SharedPreferences
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("PetPalPrefs", MODE_PRIVATE)

        // Check if user is already logged in
        if (sharedPreferences.contains("user_id")) {
            Log.d("Login", "User is already logged in, redirecting...")
            startCatalogActivity()
            finish()
            return
        }

        // Initialize views from XML
        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.loginBtn)
        signupSuggestion = findViewById(R.id.signup_suggestion)

        // Set up click listeners
        loginBtn.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            performLogin(username, password)
        }

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
                    Toast.makeText(
                        this@Login,
                        "Login failed: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                val jsonResponse = JSONObject(responseData ?: "{}")
                runOnUiThread {
                    if (jsonResponse.optBoolean("success", false)) {
                        // Save user data in SharedPreferences
                        sharedPreferences.edit().apply {
                            putInt("user_id", jsonResponse.getInt("user_id"))
                            putString("username", jsonResponse.getString("username"))
                            apply()
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
