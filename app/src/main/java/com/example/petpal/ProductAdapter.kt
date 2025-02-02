package com.example.petpal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(private val productList: List<CatalogItem>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productDescription: TextView = itemView.findViewById(R.id.productDescription)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val thumbsUpButton: ImageButton = itemView.findViewById(R.id.thumbsUpButton)
        val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
        val addToCartButton: ImageButton = itemView.findViewById(R.id.addToCartButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        // Load product image (use an image loading library like Glide or Picasso)
        // Example with Glide (uncomment if Glide is added to your project):
        // Glide.with(holder.itemView.context).load(product.imageUrl).into(holder.productImage)

        // Bind product data to views
        holder.productName.text = product.name
        holder.productDescription.text = product.description
        holder.productPrice.text = "₱${product.price}"

        // Set up listeners for buttons
        holder.thumbsUpButton.setOnClickListener {
            // Handle Thumbs Up logic
        }
        holder.favoriteButton.setOnClickListener {
            // Handle Favorite logic
        }
        holder.addToCartButton.setOnClickListener {
            // Handle Add to Cart logic
        }
    }

    override fun getItemCount(): Int = productList.size
}
