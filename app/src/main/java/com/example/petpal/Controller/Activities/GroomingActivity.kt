package com.example.petpal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.petpal.Controller.Activities.ServiceAdapter
import com.example.petpal.Controller.Activities.ServiceAvailActivity
import com.example.petpal.Controller.Activities.ServiceHistoryActivity
import com.example.petpal.R
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException

class GroomingActivity : AppCompatActivity() {

    private lateinit var servicesListView: ListView
    private lateinit var historyButton: Button
    private val client = OkHttpClient()
    private var services = mutableListOf<Service>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // "grooming_page" is your layout from the question
        setContentView(R.layout.grooming_page)

        servicesListView = findViewById(R.id.servicesListView)
        historyButton = findViewById(R.id.historyButton)

        // Adapter: When user selects "Avail", we launch ServiceAvailActivity
        val adapter = ServiceAdapter(this, services) { service, selectedDate ->
            val intent = Intent(this, ServiceAvailActivity::class.java)
            intent.putExtra("SERVICE_ID", service.id)
            intent.putExtra("SELECTED_DATE", selectedDate)
            startActivity(intent)
        }
        servicesListView.adapter = adapter

        // History button -> open ServiceHistoryActivity
        historyButton.setOnClickListener {
            startActivity(Intent(this, ServiceHistoryActivity::class.java))
        }

        // Fetch only "grooming" services
        fetchServices("grooming")
    }

    private fun fetchServices(typeFilter: String) {
        val url = "http://192.168.1.65/backend/fetch_services.php"
        val request = Request.Builder().url(url).get().build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        this@GroomingActivity,
                        "Error fetching services: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (responseBody.isNullOrEmpty()) {
                    runOnUiThread {
                        Toast.makeText(
                            this@GroomingActivity,
                            "No services found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return
                }
                try {
                    val jsonArray = JSONArray(responseBody)
                    services.clear()
                    for (i in 0 until jsonArray.length()) {
                        val serviceJson = jsonArray.getJSONObject(i)
                        // Filter for grooming services
                        val serviceType = serviceJson.optString("type", "").lowercase()
                        if (serviceType == typeFilter) {
                            val service = Service(
                                id = serviceJson.getInt("id"),
                                serviceName = serviceJson.getString("service_name"),
                                description = serviceJson.getString("description"),
                                status = serviceJson.getString("status")
                            )
                            services.add(service)
                        }
                    }
                    runOnUiThread {
                        (servicesListView.adapter as BaseAdapter).notifyDataSetChanged()
                    }
                } catch (e: JSONException) {
                    Log.e("GroomingActivity", "JSON error: ${e.message}")
                }
            }
        })
    }
}
