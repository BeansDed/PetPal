package com.example.petpal


// Common imports needed for the binding classes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.navigation.NavigationView


class ActivityServicesBinding private constructor(
    private val rootView: DrawerLayout,
    val toolbar: Toolbar,
    val servicesRecyclerView: RecyclerView,
    val bottomNavigation: BottomNavigationView,
    val navigationView: NavigationView,
    val popularText: TextView
) : ViewBinding {

    override fun getRoot(): View = rootView

    companion object {
        fun inflate(
            inflater: LayoutInflater,
            parent: ViewGroup? = null,
            attachToParent: Boolean = false
        ): ActivityServicesBinding {
            val root = inflater.inflate(
                R.layout.activity_services,
                parent,
                attachToParent
            )

            return bind(root)
        }

        fun bind(view: View): ActivityServicesBinding {
            return ActivityServicesBinding(
                rootView = view as DrawerLayout,
                toolbar = view.findViewById(R.id.toolbar),
                servicesRecyclerView = view.findViewById(R.id.servicesRecyclerView),
                bottomNavigation = view.findViewById(R.id.bottomNavigation),
                navigationView = view.findViewById(R.id.navigationView),
                popularText = view.findViewById(R.id.popularText)
            )
        }
    }
}

