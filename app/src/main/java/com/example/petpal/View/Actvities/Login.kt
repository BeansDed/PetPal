package com.example.petpal

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class Login : AppCompatActivity() {

    private lateinit var backBtn: ImageView
    private lateinit var usernameContainer: TextInputLayout
    private lateinit var passwordContainer: TextInputLayout
    private lateinit var usernameInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginBtn: ImageView
    private lateinit var signupSuggestion: TextView
    private lateinit var rememberMe: CheckBox
    private lateinit var forgotBtn: TextView
    private lateinit var sharedPreferences: SharedPreferences

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences("PetPalPrefs", MODE_PRIVATE)

        // Add debug log to check shared preferences state
        Log.d("LoginActivity", "User ID present: ${sharedPreferences.contains("user_id")}")

        // Check if user is already logged in
        if (sharedPreferences.contains("user_id")) {
            Log.d("LoginActivity", "User is already logged in, redirecting...")
            startCatalogActivity()
            finish()
            return
        }

        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        backBtn = findViewById(R.id.backBtn)
        usernameContainer = findViewById(R.id.username_container)
        passwordContainer = findViewById(R.id.password_container)
        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.loginBtn)
        signupSuggestion = findViewById(R.id.signup_suggestion)
        rememberMe = findViewById(R.id.rememberMe)
        forgotBtn = findViewById(R.id.forgotBtn)

        forgotBtn.paintFlags = forgotBtn.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
        signupSuggestion.paintFlags = signupSuggestion.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
    }

    private fun setupListeners() {
        loginBtn.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                usernameContainer.error = "Username is required"
                passwordContainer.error = "Password is required"
            } else {
                usernameContainer.error = null
                passwordContainer.error = null
                performLogin(username, password)
            }
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

        val requestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("http://192.168.1.12/backend/mobile_login.php")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@Login, "Login failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                val jsonResponse = JSONObject(responseData ?: "{}")
                runOnUiThread {
                    if (jsonResponse.optBoolean("success", false)) {
                        sharedPreferences.edit().apply {
                            putInt("user_id", jsonResponse.getInt("user_id"))
                            putString("username", jsonResponse.getString("username"))
                            apply()
                        }
                        startCatalogActivity()
                    } else {
                        Toast.makeText(this@Login, "Login failed: ${jsonResponse.optString("message")}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun startCatalogActivity() {
        val intent = Intent(this, CatalogActivity::class.java)
        startActivity(intent)
        finish()
    }
}
