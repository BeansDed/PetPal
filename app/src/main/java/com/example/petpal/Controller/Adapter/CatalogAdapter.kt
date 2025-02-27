package com.example.petpal

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CatalogAdapter(
    private val context: Context,
    private val catalogItems: MutableList<CatalogItem>
) : RecyclerView.Adapter<CatalogAdapter.ViewHolder>() {

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

        Glide.with(context)
            .load(item.imageUrl)
            .into(holder.productImage)

        holder.productName.text = item.name
        holder.productDescription.text = item.description
        holder.productPrice.text = "₱${item.price}"

        var isThumbsUp = false
        holder.thumbsUpButton.setOnClickListener {
            isThumbsUp = !isThumbsUp
            holder.thumbsUpButton.setImageResource(
                if (isThumbsUp) R.drawable.baseline_thumb_up_24 else R.drawable.thumbs_off
            )
            Toast.makeText(context, if (isThumbsUp) "Thumbs up!" else "Thumbs removed!", Toast.LENGTH_SHORT).show()
        }

        var isFavorite = false
        holder.favoriteButton.setOnClickListener {
            isFavorite = !isFavorite
            holder.favoriteButton.setImageResource(
                if (isFavorite) R.drawable.heart_logo else R.drawable.fill_heart
            )
            Toast.makeText(context, if (isFavorite) "Added to favorites!" else "Removed from favorites!", Toast.LENGTH_SHORT).show()
        }

        holder.addToCartButton.setOnClickListener {
            Toast.makeText(context, "Added ${item.name} to cart", Toast.LENGTH_SHORT).show()
        }

        // Make the entire card clickable to open ProductViewActivity
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProductViewActivity::class.java).apply {
                putExtra("product_id", item.id)
                putExtra("product_name", item.name)
                putExtra("product_price", item.price)
                putExtra("product_description", item.description)
                putExtra("product_image", item.imageUrl)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = catalogItems.size
}
