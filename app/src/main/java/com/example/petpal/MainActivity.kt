    package com.example.petpal

    import android.content.Intent
    import android.os.Bundle
    import android.widget.EditText
    import android.widget.ImageView
    import android.widget.TextView
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatActivity

    class MainActivity : AppCompatActivity() {

        private lateinit var loginBtn : ImageView
        private lateinit var signupBtn : ImageView
        private lateinit var skipBtn : TextView

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_welcome) // Ensure the layout filename matches here


            loginBtn = findViewById(R.id.login_btn)
            signupBtn = findViewById(R.id.signup_btn)
            skipBtn = findViewById(R.id.skip_btn)

            loginBtn.setOnClickListener {
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
            }

            signupBtn.setOnClickListener {
                val intent = Intent(this, SignUp::class.java)
                startActivity(intent)
            }

            skipBtn.setOnClickListener{
                val intent = Intent(this, CatalogActivity::class.java)
                startActivity(intent)
            }

            /*
            // Initialize views from the layout
            val backBtn = findViewById<ImageView>(R.id.backBtn)
            val usernameInput = findViewById<EditText>(R.id.username_input)
            val passwordInput = findViewById<EditText>(R.id.password_input)
            val rememberMeBtn = findViewById<ImageView>(R.id.rememberMe)
            val forgotBtn = findViewById<TextView>(R.id.forgotBtn)
            val loginBtn = findViewById<ImageView>(R.id.loginBtn)
            val signUpSuggestion = findViewById<TextView>(R.id.signup_suggestion)

            // Back button click listener
            backBtn.setOnClickListener {
                Toast.makeText(this, "Back button clicked", Toast.LENGTH_SHORT).show()
                // Handle back button logic (e.g., finish activity or go to a previous screen)
                finish()
            }

            // Remember me button click listener
            rememberMeBtn.setOnClickListener {
                Toast.makeText(this, "Remember Me clicked", Toast.LENGTH_SHORT).show()
                // Handle "Remember Me" functionality
            }

            // Forgot button click listener
            forgotBtn.setOnClickListener {
                Toast.makeText(this, "Forgot Password clicked", Toast.LENGTH_SHORT).show()
                // Navigate to a "Forgot Password" screen or functionality
            }

            // Login button click listener
            loginBtn.setOnClickListener {
                val username = usernameInput.text.toString()
                val password = passwordInput.text.toString()

                if (username.isBlank() || password.isBlank()) {
                    Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Logging in with $username", Toast.LENGTH_SHORT).show()

                    // Navigate to CatalogActivity
                    val intent = Intent(this, CatalogActivity::class.java)
                    startActivity(intent)
                }
            }



             */
        }
    }
