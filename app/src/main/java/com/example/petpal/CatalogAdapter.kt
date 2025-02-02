package com.example.petpal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CatalogAdapter(private val catalogItems: List<CatalogItem>) : RecyclerView.Adapter<CatalogAdapter.ViewHolder>() {

    // ViewHolder class
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productDescription: TextView = itemView.findViewById(R.id.productDescription)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val addToCartButton: ImageButton = itemView.findViewById(R.id.addToCartButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = catalogItems[position]

        // Load product image using Glide
        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            //.placeholder(R.drawable.placeholder_image)  // Optional: placeholder while loading
            //.error(R.drawable.error_image)             // Optional: error image if loading fails
            .into(holder.productImage)

        // Set product details
        holder.productName.text = item.name
        holder.productDescription.text = item.description
        holder.productPrice.text = "₱${item.price}"

        // Add click listener for the "Add to Cart" button
        holder.addToCartButton.setOnClickListener {
            Toast.makeText(holder.itemView.context, "${item.name} added to cart", Toast.LENGTH_SHORT).show()
            // TODO: Implement actual cart functionality here
        }
    }

    override fun getItemCount(): Int = catalogItems.size
}
