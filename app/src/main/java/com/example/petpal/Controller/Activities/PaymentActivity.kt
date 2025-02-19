package com.example.petpal

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.petpal.R

class PaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.order_summary)

        // If 'backBtn' is an ImageView in the layout:
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn?.setOnClickListener {
            finish()
        }

        // Retrieve Intent extras
        val productId = intent.getIntExtra("product_id", -1)
        val productName = intent.getStringExtra("product_name") ?: "No Name"
        val productPrice = intent.getStringExtra("product_price") ?: "0.00"
        val productImageUrl = intent.getStringExtra("product_imageUrl") ?: ""
        val productQuantity = intent.getIntExtra("product_quantity", 1)

        // Match IDs to the exact ones in order_summary.xml
        val summaryImage = findViewById<ImageView>(R.id.product_image)
        val summaryName = findViewById<TextView>(R.id.summaryProductName)
        val summaryPrice = findViewById<TextView>(R.id.summaryProductPrice)
        val summaryQuantity = findViewById<TextView>(R.id.product_quantity) // <-- corrected ID
        // 'confirmPaymentButton' is an ImageView in your snippet:
        val confirmPaymentButton = findViewById<ImageView>(R.id.confirmPaymentButton) // <-- corrected type

        // Display product data
        Glide.with(this).load(productImageUrl).into(summaryImage)
        summaryName.text = productName
        summaryPrice.text = "₱$productPrice"
        summaryQuantity.text = "Quantity: $productQuantity"

        // Payment logic
        confirmPaymentButton.setOnClickListener {
            Toast.makeText(this, "Payment confirmed!", Toast.LENGTH_SHORT).show()
        }
    }
}
