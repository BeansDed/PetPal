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
        // Set the layout to cart.xml (make sure it contains a RecyclerView with id cartRecyclerView,
        // a back button with id backBtn, and a checkout button with id confirmPaymentButton)
        setContentView(R.layout.cart)

        // Initialize back button and set its click listener.
        backBtn = findViewById(R.id.backBtn)
        backBtn.setOnClickListener { finish() }

        // Initialize checkout button.
        checkoutBtn = findViewById(R.id.checkoutBtn)
        checkoutBtn.setOnClickListener {
            // For demonstration, we simply launch PaymentActivity.
            val intent = Intent(this, PaymentActivity::class.java)
            // Optionally pass extras if needed.
            startActivity(intent)
        }

        // Initialize RecyclerView.
        cartRecyclerView = findViewById(R.id.cartRecyclerView)
        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        cartAdapter = CartAdapter(this, cartItems)
        cartRecyclerView.adapter = cartAdapter

        // Fetch cart items from the server.
        fetchCartItems()
    }

    /**
     * Fetch cart items from the server for the logged-in user.
     */
    private fun fetchCartItems() {
        // Use the same SharedPreferences name/key as your login activity.
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
                    cartItems.clear()
                    for (i in 0 until cartArray.length()) {
                        val item = cartArray.getJSONObject(i)
                        val cartId = item.optString("cart_id", "-1").toIntOrNull() ?: -1
                        val priceDouble = item.optString("price", "0").toDoubleOrNull() ?: 0.0
                        val quantityInt = item.optString("quantity", "1").toIntOrNull() ?: 1
                        cartItems.add(
                            CartItem(
                                id = cartId,
                                name = item.optString("name", "Unnamed"),
                                price = priceDouble,
                                quantity = quantityInt,
                                imageUrl = item.optString("image", "")
                            )
                        )
                    }
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
}
