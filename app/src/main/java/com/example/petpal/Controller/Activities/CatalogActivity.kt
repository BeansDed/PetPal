package com.example.petpal

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.commit
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class CatalogActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbarTitle: TextView
    private lateinit var browseText: TextView
    private lateinit var recommendTxt: TextView
    private lateinit var categoryScrollView: HorizontalScrollView
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var fragmentContainer: View
    private lateinit var cartButton: ImageView
    private lateinit var menuIcon: ImageView
    private lateinit var searchBar: EditText
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var categoryAll: MaterialButton
    private lateinit var categoryFood: MaterialButton
    private lateinit var categoryToys: MaterialButton
    private lateinit var categoryAccessories: MaterialButton
    private lateinit var categoryGrooming: MaterialButton
    private lateinit var categoryHealth: MaterialButton

    private val client = OkHttpClient()
    private val productList = mutableListOf<CatalogItem>()
    private var currentUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.catalog_activity)  // Assume you have a layout named catalog_activity

        initializeViews()
        setupNavigationDrawer()
        setupBottomNavigation()
        setupCategoryButtons()
        checkUserAndFetchProducts()
    }

    private fun initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        toolbarTitle = findViewById(R.id.toolbarTitle)
        browseText = findViewById(R.id.browseText)
        recommendTxt = findViewById(R.id.recommendTxt)
        categoryScrollView = findViewById(R.id.categoryScrollView)
        productsRecyclerView = findViewById(R.id.productsRecyclerView)
        productsRecyclerView.layoutManager = GridLayoutManager(this, 2)
        productsRecyclerView.adapter = CatalogAdapter(this, productList) // Set the adapter

        fragmentContainer = findViewById(R.id.fragment_container)

        menuIcon = findViewById(R.id.menuIcon)
        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(navigationView)
        }

        cartButton = findViewById(R.id.cartIcon)
        cartButton.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        searchBar = findViewById(R.id.search_bar)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        categoryAll = findViewById(R.id.categoryAll)
        categoryFood = findViewById(R.id.categoryFood)
        categoryToys = findViewById(R.id.categoryToys)
        categoryAccessories = findViewById(R.id.categoryAccessories)
        categoryGrooming = findViewById(R.id.categoryGrooming)
        categoryHealth = findViewById(R.id.categoryHealth)
    }

    private fun setupNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_profile -> Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
                R.id.nav_settings -> Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
                R.id.nav_logout -> logout()
            }
            drawerLayout.closeDrawer(navigationView)
            true
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_category -> {
                    toolbarTitle.text = "Catalog"
                    showCatalogUI()
                    fetchProducts()
                    true
                }
                R.id.menu_service -> {
                    toolbarTitle.text = "Services"
                    hideCatalogUI()
                    showServiceFragment()
                    true
                }
                else -> false
            }
        }
    }

    private fun showCatalogUI() {
        browseText.visibility = View.VISIBLE
        recommendTxt.visibility = View.VISIBLE
        categoryScrollView.visibility = View.VISIBLE
        productsRecyclerView.visibility = View.VISIBLE
        fragmentContainer.visibility = View.GONE
    }

    private fun hideCatalogUI() {
        browseText.visibility = View.GONE
        recommendTxt.visibility = View.GONE
        categoryScrollView.visibility = View.GONE
        productsRecyclerView.visibility = View.GONE
        fragmentContainer.visibility = View.VISIBLE
    }

    private fun showServiceFragment() {
        supportFragmentManager.commit {
            // Replace with your actual fragment
            // replace(R.id.fragment_container, ServiceFragment())
        }
    }

    private fun setupCategoryButtons() {
        categoryAll.setOnClickListener { fetchProductsByCategory("all") }
        categoryFood.setOnClickListener { fetchProductsByCategory("dog") }
        categoryToys.setOnClickListener { fetchProductsByCategory("cat") }
        categoryAccessories.setOnClickListener { fetchProductsByCategory("accessories") }
        categoryGrooming.setOnClickListener { fetchProductsByCategory("grooming") }
        categoryHealth.setOnClickListener { fetchProductsByCategory("health") }
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
            .url("http://192.168.1.12/backend/fetch_product.php") // or http://10.0.2.2 if emulator
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
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
                val responseData = response.body?.string()
                if (responseData.isNullOrEmpty()) {
                    runOnUiThread {
                        Toast.makeText(this@CatalogActivity, "Empty response from server", Toast.LENGTH_SHORT).show()
                    }
                    return
                }
                try {
                    val jsonResponse = JSONObject(responseData)
                    if (!jsonResponse.optBoolean("success", false)) {
                        runOnUiThread {
                            Toast.makeText(this@CatalogActivity, "Failed to fetch products from server", Toast.LENGTH_SHORT).show()
                        }
                        return
                    }
                    val productsArray = jsonResponse.optJSONArray("products") ?: return
                    productList.clear()

                    val baseImageUrl = "http://192.168.1.12/backend/images/" // or 10.0.2.2
                    for (i in 0 until productsArray.length()) {
                        val productJson = productsArray.getJSONObject(i)
                        val rawImage = productJson.optString("image", "")
                        val fullImageUrl = if (rawImage.startsWith("http")) rawImage else baseImageUrl + rawImage

                        productList.add(
                            CatalogItem(
                                id = productJson.optInt("id", -1),
                                name = productJson.optString("name", "Unnamed Product"),
                                price = productJson.optString("price", "0.00"),
                                description = productJson.optString("description", "No description"),
                                quantity = productJson.optInt("quantity", 0),
                                imageUrl = fullImageUrl
                            )
                        )
                    }
                    runOnUiThread {
                        productsRecyclerView.adapter?.notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@CatalogActivity, "Parsing error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun fetchProductsByCategory(category: String) {
        val url = "http://192.168.1.12/backend/fetch_product.php?category=$category"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@CatalogActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                if (responseData.isNullOrEmpty()) {
                    runOnUiThread {
                        Toast.makeText(this@CatalogActivity, "No products found for $category", Toast.LENGTH_SHORT).show()
                    }
                    return
                }
                try {
                    val jsonResponse = JSONObject(responseData)
                    if (!jsonResponse.optBoolean("success", false)) {
                        runOnUiThread {
                            Toast.makeText(this@CatalogActivity, "Failed to fetch products for $category", Toast.LENGTH_SHORT).show()
                        }
                        return
                    }
                    val productsArray = jsonResponse.optJSONArray("products") ?: return
                    productList.clear()

                    val baseImageUrl = "http://192.168.1.12/backend/images/"
                    for (i in 0 until productsArray.length()) {
                        val productJson = productsArray.getJSONObject(i)
                        val rawImage = productJson.optString("image", "")
                        val fullImageUrl = if (rawImage.startsWith("http")) rawImage else baseImageUrl + rawImage

                        productList.add(
                            CatalogItem(
                                id = productJson.optInt("id", -1),
                                name = productJson.optString("name", "Unnamed Product"),
                                price = productJson.optString("price", "0.00"),
                                description = productJson.optString("description", "No description"),
                                quantity = productJson.optInt("quantity", 0),
                                imageUrl = fullImageUrl
                            )
                        )
                    }
                    runOnUiThread {
                        productsRecyclerView.adapter?.notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@CatalogActivity, "Parsing error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun logout() {
        getSharedPreferences("PetPalPrefs", MODE_PRIVATE).edit().clear().apply()
        // Navigate to your Login activity, e.g.:
        // val intent = Intent(this, Login::class.java)
        // startActivity(intent)
        finish()
    }
}
