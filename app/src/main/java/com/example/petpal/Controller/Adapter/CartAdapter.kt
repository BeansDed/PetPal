package com.example.petpal.Controller.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
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
        val addToCheckoutCheckBox: CheckBox = itemView.findViewById(R.id.addtoCheck_btn)
        val productImage: ImageView = itemView.findViewById(R.id.product_image)
        val productName: TextView = itemView.findViewById(R.id.product_name)
        val productManufacturer: TextView = itemView.findViewById(R.id.product_manufacturer)
        val productPrice: TextView = itemView.findViewById(R.id.product_price)
        val subtractButton: ImageView = itemView.findViewById(R.id.subtract_btn)
        val productQuantity: TextView = itemView.findViewById(R.id.product_quantity)
        val addProductButton: ImageView = itemView.findViewById(R.id.add_product)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        // Inflate your cart_choices.xml
        val view = LayoutInflater.from(context).inflate(R.layout.cart_choices, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]

        // Assign data to views
        holder.productName.text = item.name
        holder.productManufacturer.text = item.manufacturer    // uses the new field in CartItem
        holder.productPrice.text = "$ ${item.price}"
        holder.productQuantity.text = item.quantity.toString()

        // Load image
        Glide.with(context)
            .load(item.imageUrl)
            .into(holder.productImage)

        // Checkbox state
        holder.addToCheckoutCheckBox.isChecked = item.isChecked
        holder.addToCheckoutCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (context is CartActivity) {
                context.onCheckoutCheckboxChanged(item.id, isChecked)
            }
        }

        // Increase quantity
        holder.addProductButton.setOnClickListener {
            if (context is CartActivity) {
                context.updateCartQuantity(item.id, item.quantity + 1)
            }
        }

        // Decrease quantity (if > 1)
        holder.subtractButton.setOnClickListener {
            if (item.quantity > 1 && context is CartActivity) {
                context.updateCartQuantity(item.id, item.quantity - 1)
            }
        }
    }

    override fun getItemCount(): Int = cartItems.size
}
