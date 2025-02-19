package com.example.petpal.Controller.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.petpal.CartActivity
import com.example.petpal.R
import com.example.petpal.data.CartItem

class CartAdapter(
    private val context: Context,
    private val cartItems: MutableList<CartItem>
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.cartItemName)
        val itemPrice: TextView = itemView.findViewById(R.id.cartItemPrice)
        val itemQuantity: TextView = itemView.findViewById(R.id.cartItemQuantity)
        val itemImage: ImageView = itemView.findViewById(R.id.cartItemImage)
        val removeButton: Button = itemView.findViewById(R.id.removeItemButton)
        val btnIncrease: Button = itemView.findViewById(R.id.btnIncrease)
        val btnDecrease: Button = itemView.findViewById(R.id.btnDecrease)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.cart_choices, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]

        holder.itemName.text = item.name
        holder.itemPrice.text = "₱${item.price}"
        holder.itemQuantity.text = item.quantity.toString()

        // Load product image
        Glide.with(context)
            .load(item.imageUrl)
            .into(holder.itemImage)

        // Remove item
        holder.removeButton.setOnClickListener {
            (context as? CartActivity)?.removeItemFromCart(item.id)
        }
        // Increase quantity
        holder.btnIncrease.setOnClickListener {
            (context as? CartActivity)?.updateCartQuantity(item.id, item.quantity + 1)
        }
        // Decrease quantity (don't go below 1)
        holder.btnDecrease.setOnClickListener {
            if (item.quantity > 1) {
                (context as? CartActivity)?.updateCartQuantity(item.id, item.quantity - 1)
            }
        }
    }

    override fun getItemCount(): Int = cartItems.size
}
