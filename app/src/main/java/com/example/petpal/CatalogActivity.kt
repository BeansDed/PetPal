package com.example.petpal

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout

class CatalogActivity : AppCompatActivity() {

    class CatalogActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.catalog_activity)

            // Initialize views
            val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
            val navigationView = findViewById<NavigationView>(R.id.nav_view)
            val categoryAll = findViewById<MaterialButton>(R.id.categoryAll)
            val categoryFood = findViewById<MaterialButton>(R.id.categoryFood)
            val categoryToys = findViewById<MaterialButton>(R.id.categoryToys)
            val categoryAccessories = findViewById<MaterialButton>(R.id.categoryAccessories)
            val categoryGrooming = findViewById<MaterialButton>(R.id.categoryGrooming)
            val categoryHealth = findViewById<MaterialButton>(R.id.categoryHealth)
            val productsRecyclerView = findViewById<RecyclerView>(R.id.productsRecyclerView)
            val tabLayout = findViewById<TabLayout>(R.id.dotIndicator)

            // Setup RecyclerView for products
            productsRecyclerView.layoutManager = GridLayoutManager(this, 2) // Grid with 2 columns
            productsRecyclerView.adapter = CatalogAdapter(getCatalogItems())

            // Handle category button clicks
            categoryAll.setOnClickListener {
                Toast.makeText(this, "All category selected", Toast.LENGTH_SHORT).show()
                // Filter products or perform actions for "All"
            }

            categoryFood.setOnClickListener {
                Toast.makeText(this, "Food category selected", Toast.LENGTH_SHORT).show()
                // Filter products or perform actions for "Food"
            }

            categoryToys.setOnClickListener {
                Toast.makeText(this, "Toys category selected", Toast.LENGTH_SHORT).show()
                // Filter products or perform actions for "Toys"
            }

            categoryAccessories.setOnClickListener {
                Toast.makeText(this, "Accessories category selected", Toast.LENGTH_SHORT).show()
                // Filter products or perform actions for "Accessories"
            }

            categoryGrooming.setOnClickListener {
                Toast.makeText(this, "Grooming category selected", Toast.LENGTH_SHORT).show()
                // Filter products or perform actions for "Grooming"
            }

            categoryHealth.setOnClickListener {
                Toast.makeText(this, "Health category selected", Toast.LENGTH_SHORT).show()
                // Filter products or perform actions for "Health"
            }

            // Handle navigation drawer item clicks
            navigationView.setNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.nav_home -> {
                        Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show()
                        true
                    }
                    //R.id.nav_profile -> {
                    //Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
                    //true
                    //}
                    R.id.nav_settings -> {
                        Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
                        true
                    }

                    R.id.nav_logout -> {
                        Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show()
                        true
                    }

                    else -> false
                }
            }
        }

        // Mock data
        private fun getCatalogItems(): List<CatalogItem> {
            return listOf(
                // Add more items...
            )
        }
    }}