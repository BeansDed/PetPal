package com.example.petpal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petpal.Controller.Adapter.CartAdapter
import com.example.petpal.R
import com.example.petpal.data.CartItem
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class CartActivity : AppCompatActivity() {

    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var backBtn: ImageView
    private lateinit var checkoutBtn: ImageView

    private val cartItems = mutableListOf<CartItem>()
    private lateinit var cartAdapter: CartAdapter
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cart)

        backBtn = findViewById(R.id.backBtn)
        backBtn.setOnClickListener { finish() }

        checkoutBtn = findViewById(R.id.checkoutBtn)
        checkoutBtn.setOnClickListener {
            // Launch PaymentActivity, or do any logic you need
            val intent = Intent(this, PaymentActivity::class.java)
            startActivity(intent)
        }

        cartRecyclerView = findViewById(R.id.cartRecyclerView)
        cartRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize adapter with empty list (cartItems)
        cartAdapter = CartAdapter(this, cartItems)
        cartRecyclerView.adapter = cartAdapter

        // Fetch cart items from your backend
        fetchCartItems()
    }

    /**
     * Fetch cart items from the server for the logged-in user.
     */
    private fun fetchCartItems() {
        val sharedPreferences = getSharedPreferences("PetPalPrefs", Context.MODE_PRIVATE)
        val mobileUserId = sharedPreferences.getInt("user_id", -1)
        Log.d("CartActivity", "mobileUserId = $mobileUserId")

        if (mobileUserId == -1) {
            Toast.makeText(this, "❌ Please login to view cart", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val url = "http://192.168.1.12/backend/fetch_cart.php?mobile_user_id=$mobileUserId"
        Log.d("CartActivity", "Fetching cart from: $url")

        val request = Request.Builder().url(url).get().build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@CartActivity, "❌ Network Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (responseBody.isNullOrEmpty()) {
                    runOnUiThread {
                        Toast.makeText(this@CartActivity, "❌ No cart items found.", Toast.LENGTH_SHORT).show()
                    }
                    return
                }
                try {
                    val json = JSONObject(responseBody)
                    if (!json.optBoolean("success", false)) {
                        runOnUiThread {
                            Toast.makeText(this@CartActivity, "❌ Error loading cart.", Toast.LENGTH_SHORT).show()
                        }
                        return
                    }
                    val cartArray = json.optJSONArray("cart") ?: JSONArray()

                    // Clear old items
                    cartItems.clear()

                    // Loop through each item in the JSON array
                    for (i in 0 until cartArray.length()) {
                        val item = cartArray.getJSONObject(i)
                        val cartId = item.optString("cart_id", "-1").toIntOrNull() ?: -1
                        val priceDouble = item.optString("price", "0").toDoubleOrNull() ?: 0.0
                        val quantityInt = item.optString("quantity", "1").toIntOrNull() ?: 1

                        // Get manufacturer if available in your JSON
                        val manufacturer = item.optString("manufacturer", "")

                        // If your JSON has a "checked" or "selected" key, parse it:
                        // val isChecked = item.optInt("checked", 0) == 1 // or some logic

                        // Create the CartItem
                        cartItems.add(
                            CartItem(
                                id = cartId,
                                name = item.optString("name", "Unnamed"),
                                price = priceDouble,
                                quantity = quantityInt,
                                imageUrl = item.optString("image", ""),
                                manufacturer = manufacturer,    // new
                                isChecked = false               // or from your JSON
                            )
                        )
                    }

                    // Update RecyclerView on the main thread
                    runOnUiThread {
                        cartAdapter.notifyDataSetChanged()
                    }

                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@CartActivity, "❌ JSON parsing error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                    Log.e("CartActivity", "JSON parsing error", e)
                }
            }
        })
    }

    /**
     * Removes an item from the cart by cartId.
     */
    fun removeItemFromCart(cartId: Int) {
        val url = "http://192.168.1.12/backend/remove_from_cart.php"
        val json = JSONObject().apply { put("cart_id", cartId) }
        val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder().url(url).post(requestBody).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@CartActivity, "❌ Failed to remove item", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    Toast.makeText(this@CartActivity, "✅ Item removed", Toast.LENGTH_SHORT).show()
                    fetchCartItems()
                }
            }
        })
    }

    /**
     * Updates the quantity of a cart item.
     */
    fun updateCartQuantity(cartId: Int, newQuantity: Int) {
        val url = "http://192.168.1.12/backend/update_cart.php"
        val json = JSONObject().apply {
            put("cart_id", cartId)
            put("quantity", newQuantity)
        }
        val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder().url(url).post(requestBody).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@CartActivity, "❌ Failed to update quantity", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                runOnUiThread {
                    if (!responseBody.isNullOrEmpty()) {
                        val jsonResponse = JSONObject(responseBody)
                        if (jsonResponse.optBoolean("success", false)) {
                            Toast.makeText(this@CartActivity, "✅ Quantity updated", Toast.LENGTH_SHORT).show()
                            fetchCartItems()
                        } else {
                            Toast.makeText(this@CartActivity, "❌ ${jsonResponse.optString("message")}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }

    /**
     * Handle checkbox changes from the adapter.
     * This is called whenever a user checks/unchecks the item in the list.
     */
    fun onCheckoutCheckboxChanged(cartId: Int, isChecked: Boolean) {
        // 1) Update local cartItems so the UI is consistent
        val cartItem = cartItems.find { it.id == cartId }
        cartItem?.isChecked = isChecked

        // 2) Optionally, call an API to update the item’s “selected” status in the DB
        //    or handle it however you like. For example:
        /*
        val url = "http://192.168.1.12/backend/update_cart_checkbox.php"
        val json = JSONObject().apply {
            put("cart_id", cartId)
            put("checked", if (isChecked) 1 else 0)
        }
        val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder().url(url).post(requestBody).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@CartActivity, "❌ Failed to update checkbox", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onResponse(call: Call, response: Response) {
                // You can refresh or handle the response
            }
        })
        */
    }
}
