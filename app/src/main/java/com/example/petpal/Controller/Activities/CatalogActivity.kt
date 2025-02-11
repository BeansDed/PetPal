package com.example.petpal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petpal.Controller.Activities.CartActivity
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class CatalogActivity : AppCompatActivity() {

    // Views
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var cartButton: ImageView
    private lateinit var searchIcon: ImageView
    private lateinit var bottomTabLayout: TabLayout

    // Data
    private val client = OkHttpClient()
    private val productList = mutableListOf<CatalogItem>()
    private var currentUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.catalog_activity)

        initializeViews()
        setupNavigationDrawer()
        checkUserAndFetchProducts()
    }

    private fun initializeViews() {
        try {
            // Initialize views with their respective IDs
            drawerLayout = findViewById(R.id.drawer_layout)
            navigationView = findViewById(R.id.nav_view)
            productsRecyclerView = findViewById(R.id.productsRecyclerView)
            productsRecyclerView.layoutManager = GridLayoutManager(this, 2)
            productsRecyclerView.adapter = CatalogAdapter(this, productList)

            // Ensure this ID is an ImageView, not an EditText or other type
            val menuIcon: ImageView = findViewById(R.id.menuIcon)
            menuIcon.setOnClickListener {
                drawerLayout.openDrawer(navigationView)
            }

            cartButton = findViewById(R.id.cartIcon)
            cartButton.setOnClickListener {
                startActivity(Intent(this, CartActivity::class.java))
            }

            searchIcon = findViewById(R.id.search_bar)
            searchIcon.setOnClickListener {
                Toast.makeText(this, "Search feature not implemented yet", Toast.LENGTH_SHORT).show()
            }

            bottomTabLayout = findViewById(R.id.bottomNavigation)
            setupBottomTabs()

            Log.d("CatalogActivity", "Views initialized successfully")
        } catch (e: ClassCastException) {
            Log.e("CatalogActivity", "View casting error", e)
            Toast.makeText(this, "Error initializing views: ${e.message}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("CatalogActivity", "Error initializing views", e)
            Toast.makeText(this, "Error initializing views: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }



    private fun setupNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_profile -> {
                    Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_profile -> {
                    Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_settings -> {
                    Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_logout -> {
                    logout()
                }
                else -> false
            }
            drawerLayout.closeDrawer(navigationView)
            true
        }
    }

    private fun setupBottomTabs() {
        bottomTabLayout.addTab(bottomTabLayout.newTab().setText("Products"))
        bottomTabLayout.addTab(bottomTabLayout.newTab().setText("Services"))

        bottomTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> fetchProducts()
                    1 -> Toast.makeText(this@CatalogActivity, "Services feature not implemented", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
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
                Log.e("CatalogActivity", "Network error: ${e.message}", e)
                runOnUiThread {
                    Toast.makeText(this@CatalogActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@CatalogActivity, "Failed to fetch products", Toast.LENGTH_SHORT).show()
                    }
                    return
                }

                try {
                    val responseData = response.body?.string()
                    if (responseData.isNullOrEmpty()) throw Exception("Empty response from server")

                    val jsonResponse = JSONObject(responseData)
                    if (!jsonResponse.optBoolean("success")) {
                        throw Exception("Failed to fetch products from server")
                    }

                    val productsArray = jsonResponse.optJSONArray("products") ?: return
                    productList.clear()

                    for (i in 0 until productsArray.length()) {
                        val productJson = productsArray.getJSONObject(i)
                        productList.add(
                            CatalogItem(
                                id = productJson.optInt("id", -1),
                                name = productJson.optString("name", "Unnamed Product"),
                                price = productJson.optString("price", "0.00"),
                                description = productJson.optString("description", "No description"),
                                quantity = productJson.optInt("quantity", 0),
                                imageUrl = productJson.optString("image", "")
                            )
                        )
                    }

                    runOnUiThread {
                        productsRecyclerView.adapter?.notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    Log.e("CatalogActivity", "Error parsing response", e)
                    runOnUiThread {
                        Toast.makeText(this@CatalogActivity, "Parsing error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun logout() {
        getSharedPreferences("PetPalPrefs", MODE_PRIVATE).edit().clear().apply()
        val intent = Intent(this, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
