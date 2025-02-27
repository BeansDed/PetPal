package com.example.petpal

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProductViewActivity : AppCompatActivity() {

    private lateinit var productImage: ImageView
    private lateinit var productPrice: TextView
    private lateinit var productSoldHistory: TextView
    private lateinit var productName: TextView
    private lateinit var productDescription: TextView
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_view)

        // Bind views
        val backBtn = findViewById<ImageView>(R.id.backBtn)    // Back button in toolbar
        productImage = findViewById(R.id.productImageView)
        productPrice = findViewById(R.id.product_price)
        productSoldHistory = findViewById(R.id.product_soldHistory)
        productName = findViewById(R.id.productName)
        productDescription = findViewById(R.id.productDescription)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        // Back button -> finish() returns to CatalogActivity
        backBtn.setOnClickListener {
            finish()
        }

        // Get product info from intent extras
        val prodId = intent.getIntExtra("product_id", -1)
        val prodName = intent.getStringExtra("product_name") ?: "Unknown"
        val prodPriceVal = intent.getStringExtra("product_price") ?: "0.00"
        val prodDesc = intent.getStringExtra("product_description") ?: ""
        val prodImageUrl = intent.getStringExtra("product_image") ?: ""

        // Populate UI
        productName.text = prodName
        productPrice.text = "₱$prodPriceVal"
        productSoldHistory.text = "100 Sold"  // Example static text
        productDescription.text = prodDesc

        // Load product image with Glide
        Glide.with(this)
            .load(prodImageUrl)
            .into(productImage)

        // Handle bottom nav clicks
        bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_add_to_cart -> {
                    // Example: go to CartActivity
                    startActivity(Intent(this, CartActivity::class.java))
                    true
                }
                R.id.menu_buy_now -> {
                    // 1) Pass product data to OrderSummaryActivity
                    val intentOrder = Intent(this, OrderSummaryActivity::class.java).apply {
                        putExtra("product_id", prodId)
                        putExtra("product_name", prodName)
                        putExtra("product_price", prodPriceVal)
                        putExtra("product_description", prodDesc)
                        putExtra("product_image", prodImageUrl)
                    }
                    startActivity(intentOrder)
                    true
                }
                else -> false
            }
        }
    }
}
