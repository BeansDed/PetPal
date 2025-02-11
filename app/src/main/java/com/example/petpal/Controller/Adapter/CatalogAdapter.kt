package com.example.petpal

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class CatalogAdapter(private val context: Context, private var catalogItems: MutableList<CatalogItem>) :
    RecyclerView.Adapter<CatalogAdapter.ViewHolder>() {

    private val client = OkHttpClient()

    // ViewHolder class
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productDescription: TextView = itemView.findViewById(R.id.productDescription)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val addToCartButton: ImageButton = itemView.findViewById(R.id.addToCartButton)
        val thumbsUpButton: ImageButton = itemView.findViewById(R.id.thumbsUpButton)
        val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = catalogItems[position]

        // Load product image using Glide
        Glide.with(context)
            .load(item.imageUrl)
            .into(holder.productImage)

        // Set product details
        holder.productName.text = item.name
        holder.productDescription.text = item.description
        holder.productPrice.text = "₱${item.price}"

        // Handle thumbs up functionality
        var isThumbsUp = false
        holder.thumbsUpButton.setOnClickListener {
            isThumbsUp = !isThumbsUp
            holder.thumbsUpButton.setImageResource(
                if (isThumbsUp) R.drawable.baseline_thumb_up_24 else R.drawable.thumbs_off
            )
            Toast.makeText(context, if (isThumbsUp) "Thumbs up!" else "Thumbs removed!", Toast.LENGTH_SHORT).show()
        }

        // Handle favorite functionality
        var isFavorite = false
        holder.favoriteButton.setOnClickListener {
            isFavorite = !isFavorite
            holder.favoriteButton.setImageResource(
                if (isFavorite) R.drawable.heart_logo else R.drawable.heart_logo
            )
            Toast.makeText(context, if (isFavorite) "Added to favorites!" else "Removed from favorites!", Toast.LENGTH_SHORT).show()
        }

        // Configure Add to Cart button
        holder.addToCartButton.setOnClickListener {
            addToCart(item, holder)
        }
    }

    override fun getItemCount(): Int = catalogItems.size

    private fun addToCart(product: CatalogItem, holder: ViewHolder) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val mobileUserId = sharedPreferences.getInt("user_id", -1)

        if (mobileUserId == -1) {
            Toast.makeText(context, "❌ Please login to add to cart", Toast.LENGTH_SHORT).show()
            return
        }

        val jsonObject = JSONObject().apply {
            put("mobile_user_id", mobileUserId)
            put("product_id", product.id)
            put("quantity", 1)
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonObject.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("http://192.168.1.12/backend/add_to_cart.php")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("AddToCart", "❌ Network request failed: ${e.message}")
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
                            Toast.makeText(context, "✅ Added to Cart!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "❌ Error: ${jsonResponse.optString("message")}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }

    // Method to update the product list
    fun updateProducts(newProducts: List<CatalogItem>) {
        catalogItems.clear()
        catalogItems.addAll(newProducts)
        notifyDataSetChanged()
    }
}
