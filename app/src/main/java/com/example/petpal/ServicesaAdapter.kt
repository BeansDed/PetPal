package com.example.petpal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class ServicesAdapter(
    private val services: List<Service>,
    private val onServiceClick: (Service) -> Unit
) : RecyclerView.Adapter<ServicesAdapter.ServiceViewHolder>() {

    class ServiceViewHolder(
        private val binding: ItemServiceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(service: Service, onServiceClick: (Service) -> Unit) {
            binding.apply {
                serviceTitle.text = service.title
                serviceDescription.text = service.description

                // Load image using Glide
                Glide.with(serviceImage.context)
                    .load(service.imageUrl)
                    .centerCrop()
                    .into(serviceImage)

                root.setOnClickListener {
                    onServiceClick(service)
                }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ServiceViewHolder {
                val binding = ItemServiceBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ServiceViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        return ServiceViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(services[position], onServiceClick)
    }

    override fun getItemCount() = services.size
}



