package com.example.petpal

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class OrderSummaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.order_summary)

        // 1) Bind your views
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        val confirmPaymentButton = findViewById<ImageView>(R.id.confirmPaymentButton)

        // The main product image in your layout
        val productImage = findViewById<ImageView>(R.id.product_image)

        // Product name & price in the upper summary
        val summaryProductName = findViewById<TextView>(R.id.summaryProductName)
        val summaryProductPrice = findViewById<TextView>(R.id.summaryProductPrice)

        // The total price at the bottom
        val bottomTotalPrice = findViewById<TextView>(R.id.bottomTotalPrice)

        // 2) Handle back button
        backBtn.setOnClickListener {
            finish()
        }

        // 3) Handle confirm payment
        confirmPaymentButton.setOnClickListener {
            // e.g. place order logic, show a toast, etc.
            // Toast.makeText(this, "Order Confirmed!", Toast.LENGTH_SHORT).show()
        }

        // 4) Retrieve product data from the Intent
        val productId = intent.getIntExtra("product_id", -1)
        val productName = intent.getStringExtra("product_name") ?: "No name"
        val productPrice = intent.getStringExtra("product_price") ?: "0.00"
        val productDesc = intent.getStringExtra("product_description") ?: ""
        val productImageUrl = intent.getStringExtra("product_image") ?: ""

        // 5) Populate the UI
        summaryProductName.text = productName
        summaryProductPrice.text = "$ $productPrice" // or "₱$productPrice"
        bottomTotalPrice.text = "$ $productPrice"

        // If you want to show productDesc or other data, add more TextViews or logic

        // 6) Load the product image with Glide
        Glide.with(this)
            .load(productImageUrl)
            .into(productImage)
    }
}
