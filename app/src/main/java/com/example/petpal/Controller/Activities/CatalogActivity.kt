package com.example.petpal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class CatalogActivity : AppCompatActivity() {

    private lateinit var categoryAll: MaterialButton
    private lateinit var categoryFood: MaterialButton
    private lateinit var categoryToys: MaterialButton
    private lateinit var categoryAccessories: MaterialButton
    private lateinit var categoryGrooming: MaterialButton
    private lateinit var categoryHealth: MaterialButton
    private lateinit var searchIcon: ImageView
    private lateinit var productsRecyclerView: RecyclerView
    private val client = OkHttpClient()
    private var currentUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.catalog_activity)

        initializeViews()
        setupCategoryButtons()
        setupNavigationDrawer()
        checkUserAndFetchProducts()
    }

    private fun initializeViews() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val menuIcon = findViewById<ImageView>(R.id.menuIcon)

        productsRecyclerView = findViewById(R.id.productsRecyclerView)
        productsRecyclerView.layoutManager = GridLayoutManager(this, 2)

        categoryAll = findViewById(R.id.categoryAll)
        categoryFood = findViewById(R.id.categoryFood)
        categoryToys = findViewById(R.id.categoryToys)
        categoryAccessories = findViewById(R.id.categoryAccessories)
        categoryGrooming = findViewById(R.id.categoryGrooming)
        categoryHealth = findViewById(R.id.categoryHealth)

        menuIcon.setOnClickListener { drawerLayout.openDrawer(Gravity.LEFT) }

        searchIcon = findViewById(R.id.searchIcon)
        searchIcon.setOnClickListener { goToSearchActivity() }
    }

    private fun goToSearchActivity() {
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
    }

    private fun setupCategoryButtons() {
        val buttons = listOf(
            categoryAll,
            categoryFood,
            categoryToys,
            categoryAccessories,
            categoryGrooming,
            categoryHealth
        )
        buttons.forEach { button ->
            button.setOnClickListener {
                setActiveButton(it as MaterialButton)
            }
        }
    }

    private fun setActiveButton(activeButton: MaterialButton) {
        val buttons = listOf(
            categoryAll,
            categoryFood,
            categoryToys,
            categoryAccessories,
            categoryGrooming,
            categoryHealth
        )
        buttons.forEach {
            it.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        }
        activeButton.setBackgroundColor(ContextCompat.getColor(this, R.color.smth_orange))
    }

    private fun setupNavigationDrawer() {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.nav_settings -> {
                    Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.nav_logout -> {
                    logout()
                    true
                }

                else -> false
            }
        }
    }

    private fun logout() {
        getSharedPreferences("PetPalPrefs", MODE_PRIVATE).edit().clear().apply()
        val intent = Intent(this, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun checkUserAndFetchProducts() {
        val sharedPreferences = getSharedPreferences("PetPalPrefs", MODE_PRIVATE)
        currentUserId = sharedPreferences.getInt("user_id", -1)

        if (currentUserId == -1) {
            Toast.makeText(this, "Please log in to view products", Toast.LENGTH_SHORT).show()
            logout()
        } else {
            fetchProducts()
        }
    }

    private fun fetchProducts() {
        val request = Request.Builder()
            .url("http://192.168.1.12/backend/fetch_product.php")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        this@CatalogActivity,
                        "Network error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(
                            this@CatalogActivity,
                            "Failed to fetch products",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return
                }

                try {
                    val responseData = response.body?.string() ?: throw Exception("Empty response")
                    Log.d("CatalogActivity", "Response Data: $responseData")
                    val jsonArray = JSONArray(responseData)
                    val productList = mutableListOf<CatalogItem>()

                    for (i in 0 until jsonArray.length()) {
                        val productJson = jsonArray.getJSONObject(i)

                        // Safely retrieve fields with default values if missing
                        val id = productJson.optInt("id", -1)
                        val name = productJson.optString("name", "Unnamed Product")
                        val price = productJson.optString("price", "0.00")
                        val description = productJson.optString("description", "No description")
                        val quantity = productJson.optInt("quantity", 0)
                        val imageUrl = productJson.optString("image", "")

                        if (id == -1) {
                            Log.e("CatalogActivity", "Invalid product data: missing id")
                            continue  // Skip invalid product entry
                        }

                        productList.add(
                            CatalogItem(
                                id = id,
                                name = name,
                                price = price,
                                description = description,
                                quantity = quantity,
                                imageUrl = imageUrl
                            )
                        )
                    }

                    runOnUiThread {
                        productsRecyclerView.adapter = CatalogAdapter(productList)
                    }
                } catch (e: Exception) {
                    Log.e("CatalogActivity", "Error parsing response", e)
                    runOnUiThread {
                        Toast.makeText(
                            this@CatalogActivity,
                            "Parsing error: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }
}