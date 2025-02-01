package com.example.petpal

import android.content.Intent
import android.os.Bundle
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
import com.google.android.material.tabs.TabLayout

class CatalogActivity : AppCompatActivity() {

    private lateinit var categoryAll: MaterialButton
    private lateinit var categoryFood: MaterialButton
    private lateinit var categoryToys: MaterialButton
    private lateinit var categoryAccessories: MaterialButton
    private lateinit var categoryGrooming: MaterialButton
    private lateinit var categoryHealth: MaterialButton
    private lateinit var searchIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.catalog_activity)

        initializeViews()
        setupCategoryButtons()
        setupNavigationDrawer()
    }

    private fun initializeViews() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val productsRecyclerView = findViewById<RecyclerView>(R.id.productsRecyclerView)
        val menuIcon = findViewById<ImageView>(R.id.menuIcon)

        categoryAll = findViewById(R.id.categoryAll)
        categoryFood = findViewById(R.id.categoryFood)
        categoryToys = findViewById(R.id.categoryToys)
        categoryAccessories = findViewById(R.id.categoryAccessories)
        categoryGrooming = findViewById(R.id.categoryGrooming)
        categoryHealth = findViewById(R.id.categoryHealth)

        productsRecyclerView.layoutManager = GridLayoutManager(this, 2)
        productsRecyclerView.adapter = CatalogAdapter(getCatalogItems())

        menuIcon.setOnClickListener { drawerLayout.openDrawer(Gravity.LEFT) }

        searchIcon = findViewById(R.id.searchIcon)
        searchIcon.setOnClickListener { goToSearchActivity() }
    }

    private fun goToSearchActivity() {
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
    }

    private fun setupCategoryButtons() {
        val buttons = listOf(categoryAll, categoryFood, categoryToys, categoryAccessories, categoryGrooming, categoryHealth)
        buttons.forEach { button ->
            button.setOnClickListener {
                setActiveButton(it as MaterialButton)
            }
        }
    }

    private fun setActiveButton(activeButton: MaterialButton) {
        val buttons = listOf(categoryAll, categoryFood, categoryToys, categoryAccessories, categoryGrooming, categoryHealth)
        buttons.forEach {
            it.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        }
        activeButton.setBackgroundColor(ContextCompat.getColor(this, R.color.orange))
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
                    logout()  // Call the logout function here
                    true
                }
                else -> false
            }
        }
    }

    private fun logout() {
        // Clear user data (SharedPreferences or any other storage)
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        // Redirect to the LoginActivity
        val intent = Intent(this, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun getCatalogItems(): List<CatalogItem> {
        return listOf(
            // Populate with actual catalog items
        )
    }
}
