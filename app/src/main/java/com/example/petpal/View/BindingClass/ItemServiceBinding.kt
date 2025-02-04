package com.example.petpal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewbinding.ViewBinding
import com.google.android.material.card.MaterialCardView

class ItemServiceBinding private constructor(
    private val rootView: MaterialCardView,
    val serviceImage: ImageView,
    val serviceTitle: TextView,
    val serviceDescription: TextView
) : ViewBinding {

    override fun getRoot(): View = rootView

    companion object {
        @JvmStatic
        fun inflate(
            inflater: LayoutInflater,
            parent: ViewGroup?,
            attachToParent: Boolean = false
        ): ItemServiceBinding {
            val root = inflater.inflate(
                R.layout.item_service,
                parent,
                attachToParent
            )
            return bind(root)
        }

        @JvmStatic
        fun bind(view: View): ItemServiceBinding {
            // Type checks
            if (view !is MaterialCardView) {
                throw IllegalArgumentException("Root view must be MaterialCardView")
            }

            val serviceImage = view.findViewById<ImageView>(R.id.serviceImage)
                ?: throw IllegalArgumentException("Required view 'serviceImage' not found")

            val serviceTitle = view.findViewById<TextView>(R.id.serviceTitle)
                ?: throw IllegalArgumentException("Required view 'serviceTitle' not found")

            val serviceDescription = view.findViewById<TextView>(R.id.serviceDescription)
                ?: throw IllegalArgumentException("Required view 'serviceDescription' not found")

            return ItemServiceBinding(
                rootView = view,
                serviceImage = serviceImage,
                serviceTitle = serviceTitle,
                serviceDescription = serviceDescription
            )
        }
    }
}

