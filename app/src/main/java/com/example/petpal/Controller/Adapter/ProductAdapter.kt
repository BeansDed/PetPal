package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.petpal.CatalogItem
import com.example.petpal.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class ProductAdapter(private val context: Context, private var products: MutableList<CatalogItem>)
    : android.widget.BaseAdapter() {

    override fun getCount(): Int = products.size
    override fun getItem(position: Int): CatalogItem = products[position]
    override fun getItemId(position: Int): Long = products[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // Inflate 'order_summary.xml' instead of 'product_item.xml'
        val view: View = convertView
            ?: LayoutInflater.from(context).inflate(R.layout.order_summary, parent, false)

        val product = getItem(position)

        // Existing IDs in order_summary.xml that we can reference:
        val itemImage: ImageView? = view.findViewById(R.id.product_image)
        val itemQuantity: TextView? = view.findViewById(R.id.product_quantity)
        val subtractBtn: ImageView? = view.findViewById(R.id.subtract_btn)
        val addBtn: ImageView? = view.findViewById(R.id.add_product)

        // 1) Load product image (if available)
        if (itemImage != null) {
            Glide.with(context).load(product.imageUrl).into(itemImage)
        }

        // 2) Display quantity
        itemQuantity?.text = product.quantity.toString()

        // 3) If product.quantity == 0, you might disable buttons, etc.
        if (product.quantity <= 0) {
            subtractBtn?.isEnabled = false
            addBtn?.isEnabled = false
            // or do some "out of stock" logic
        } else {
            subtractBtn?.isEnabled = true
            addBtn?.isEnabled = true
        }

        // 4) Subtract button logic
        subtractBtn?.setOnClickListener {
            if (product.quantity > 1) {
                updateCartQuantity(product, product.quantity - 1)
            } else {
                Toast.makeText(context, "Cannot go below 1", Toast.LENGTH_SHORT).show()
            }
        }

        // 5) Add button logic
        addBtn?.setOnClickListener {
            // Increase quantity by 1 (or do "Add to cart" logic if that's desired)
            updateCartQuantity(product, product.quantity + 1)
        }

        return view
    }

    /**
     * Example function that might update the product quantity in local list
     * and optionally make a network request to update cart.
     */
    private fun updateCartQuantity(product: CatalogItem, newQuantity: Int) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val mobileUserId = sharedPreferences.getInt("user_id", -1)

        if (mobileUserId == -1) {
            Toast.makeText(context, "❌ Please login to modify cart", Toast.LENGTH_SHORT).show()
            return
        }

        // Example network request to update cart quantity
        val jsonObject = JSONObject().apply {
            put("mobile_user_id", mobileUserId)
            put("product_id", product.id)
            put("quantity", newQuantity)
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonObject.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("http://192.168.1.12/backend/update_cart.php")
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("UpdateCart", "❌ Network request failed: ${e.message}")
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(context, "Failed to connect to server", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Handler(Looper.getMainLooper()).post {
                    if (responseBody != null) {
                        val jsonResponse = JSONObject(responseBody)
                        val success = jsonResponse.optBoolean("success", false)

                        if (success) {
                            Toast.makeText(context, "✅ Updated quantity!", Toast.LENGTH_SHORT).show()
                            product.quantity = newQuantity
                            notifyDataSetChanged()
                        } else {
                            Toast.makeText(
                                context,
                                "❌ Error: ${jsonResponse.optString("message")}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        })
    }

    fun updateProducts(newProducts: List<CatalogItem>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }

    fun getProducts(): List<CatalogItem> {
        return products
    }
}
