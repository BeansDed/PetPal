package com.example.petpal

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Register : AppCompatActivity() {

    lateinit var back_btn : ImageView
    lateinit var username_input : EditText
    lateinit var email_input : EditText
    lateinit var password_input : EditText
    lateinit var login_btn : Button
    lateinit var remember_acc : ImageView
    lateinit var remember_me : TextView
    lateinit var forgot_pass : TextView
    lateinit var fb_acc : ImageView
    lateinit var google_acc : ImageView
    lateinit var already_acc : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        back_btn = findViewById(R.id.back_btn)
        username_input = findViewById(R.id.username_input)
        email_input = findViewById(R.id.email_input)
        password_input = findViewById(R.id.password_input)
        login_btn = findViewById(R.id.login_btn)
        remember_acc = findViewById(R.id.remember_acc)
        remember_me = findViewById(R.id.remember_me)
        forgot_pass = findViewById(R.id.forgot_pass)
        fb_acc = findViewById(R.id.fb_acc)
        google_acc = findViewById(R.id.google_acc)
        already_acc = findViewById(R.id.already_acc)


    }
}