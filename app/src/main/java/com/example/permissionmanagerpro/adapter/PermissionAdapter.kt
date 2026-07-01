package com.example.permissionmanagerpro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.permissionmanagerpro.R
import com.example.permissionmanagerpro.databinding.ItemPermissionBinding

data class PermissionRow(val name: String, val granted: Boolean)

class PermissionAdapter(
    private val items: List<PermissionRow>
) : RecyclerView.Adapter<PermissionAdapter.PermissionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PermissionViewHolder {
        val binding = ItemPermissionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PermissionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PermissionViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class PermissionViewHolder(private val binding: ItemPermissionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(row: PermissionRow) {
            val simplifiedName = row.name.substringAfterLast('.')
            binding.tvPermissionName.text = simplifiedName

            val context = binding.root.context
            if (row.granted) {
                binding.tvPermissionStatus.text = context.getString(R.string.label_granted)
                binding.tvPermissionStatus.setTextColor(
                    context.getColor(R.color.success)
                )
            } else {
                binding.tvPermissionStatus.text = context.getString(R.string.label_denied)
                binding.tvPermissionStatus.setTextColor(
                    context.getColor(R.color.danger)
                )
            }
        }
    }
}
