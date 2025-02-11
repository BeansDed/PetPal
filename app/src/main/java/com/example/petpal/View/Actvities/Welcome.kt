package com.example.petpal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Welcome : AppCompatActivity() {

    private lateinit var loginBtn: ImageView
    private lateinit var signupBtn: ImageView
    private lateinit var skipBtn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        Log.d("WelcomeActivity", "Activity created")

        initViews()
        setupListeners()
    }

    private fun initViews() {
        try {
            loginBtn = findViewById(R.id.login_btn)
            signupBtn = findViewById(R.id.signup_btn)
            skipBtn = findViewById(R.id.skip_btn)

            Log.d("WelcomeActivity", "Views initialized successfully")

        } catch (e: Exception) {
            Log.e("WelcomeActivity", "Error initializing views", e)
            Toast.makeText(this, "Error initializing views: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupListeners() {
        loginBtn.setOnClickListener {
            Log.d("WelcomeActivity", "Login button clicked")
            try {
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("WelcomeActivity", "Error starting Login activity", e)
                Toast.makeText(this, "Failed to start Login activity: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        signupBtn.setOnClickListener {
            Log.d("WelcomeActivity", "Signup button clicked")
            try {
                val intent = Intent(this, SignUp::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("WelcomeActivity", "Error starting Signup activity", e)
                Toast.makeText(this, "Failed to start Signup activity: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        skipBtn.setOnClickListener {
            Log.d("WelcomeActivity", "Skip button clicked")
            try {
                val intent = Intent(this, CatalogActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("WelcomeActivity", "Error starting Catalog activity", e)
                Toast.makeText(this, "Failed to start Catalog activity: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
