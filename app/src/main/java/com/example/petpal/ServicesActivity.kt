package com.example.petpal

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.example.petpal.databinding.ActivityServicesBinding

class ServicesActivity : AppCompatActivity() {

    private var _binding: ActivityServicesBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityServicesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupBottomNavigation()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.menu_bar) // Ensure this drawable exists in your resources
        }

        binding.toolbar.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START) // Open drawer when menu icon is clicked
        }
    }

    private fun setupRecyclerView() {
        binding.servicesRecyclerView.apply {
            layoutManager = GridLayoutManager(this@ServicesActivity, 2)
            adapter = ServicesAdapter(getServicesList()) { service ->
                onServiceClicked(service)
            }
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_service -> {
                    // Already in ServicesActivity
                    true
                }
                R.id.menu_category -> {
                    startActivity(Intent(this, CatalogActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun getServicesList(): List<Service> {
        return listOf(
            Service("Service 1", "Description 1"),
            Service("Service 2", "Description 2"),
            Service("Service 3", "Description 3")
        )
    }

    private fun onServiceClicked(service: Service) {
        val intent = Intent(this, ServiceDetailActivity::class.java)
        intent.putExtra("menu_service", service.title) //
        startActivity(intent)
    }
}
