package com.example.petpal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val addToCartButton: ImageButton = itemView.findViewById(R.id.addToCartButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        // Bind your data here
        holder.itemView.setOnClickListener {
            // Handle item click
        }

        holder.favoriteButton.setOnClickListener {
            // Handle favorite button click
        }

        holder.addToCartButton.setOnClickListener {
            // Handle add to cart button click
        }
    }

    override fun getItemCount(): Int {
        // Return your data size
        return 0
    }
}