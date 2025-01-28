package com.example.petpal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.viewbinding.ViewBinding


class ActivityServiceDetailBinding private constructor(
    private val rootView: CoordinatorLayout,
    val detailToolbar: Toolbar,
    val serviceDetailImage: ImageView,
    val serviceDetailTitle: TextView,
    val serviceDetailDescription: TextView
) : ViewBinding {

    override fun getRoot(): View = rootView

    companion object {
        fun inflate(
            inflater: LayoutInflater,
            parent: ViewGroup? = null,
            attachToParent: Boolean = false
        ): ActivityServiceDetailBinding {
            val root = inflater.inflate(
                R.layout.activity_service_detail,
                parent,
                attachToParent
            )

            return bind(root)
        }

        fun bind(view: View): ActivityServiceDetailBinding {
            return ActivityServiceDetailBinding(
                rootView = view as CoordinatorLayout,
                detailToolbar = view.findViewById(R.id.detailToolbar),
                serviceDetailImage = view.findViewById(R.id.serviceDetailImage),
                serviceDetailTitle = view.findViewById(R.id.serviceDetailTitle),
                serviceDetailDescription = view.findViewById(R.id.serviceDetailDescription)
            )
        }
    }
}

