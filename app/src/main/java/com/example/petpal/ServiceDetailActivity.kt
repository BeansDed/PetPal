package com.example.petpal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.petpal.databinding.ActivityServiceDetailBinding  // Import correct binding class

class ServiceDetailActivity : AppCompatActivity() {

    private var _binding: ActivityServiceDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityServiceDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        displayServiceDetails()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupToolbar() {
        val toolbar = binding.detailToolbar as Toolbar  // Explicit cast
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun displayServiceDetails() {
        val service = intent.getParcelableExtra<Service>("service")

        service?.let {
            binding.apply {
                serviceDetailTitle.text = it.title
                serviceDetailDescription.text = it.description
                // Load image using Glide or other image loading library
            }
        }
    }
}
