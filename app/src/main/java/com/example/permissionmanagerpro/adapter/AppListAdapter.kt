package com.example.permissionmanagerpro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.permissionmanagerpro.R
import com.example.permissionmanagerpro.databinding.ItemAppBinding
import com.example.permissionmanagerpro.model.AppInfo

class AppListAdapter(
    private val onItemClick: (AppInfo) -> Unit
) : ListAdapter<AppInfo, AppListAdapter.AppViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemAppBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AppViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AppViewHolder(private val binding: ItemAppBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(appInfo: AppInfo) {
            binding.tvAppName.text = appInfo.appName
            binding.ivAppIcon.setImageDrawable(appInfo.icon)

            val grantedCount = appInfo.grantedPermissions.size
            binding.tvPermissionCount.text = binding.root.context.getString(
                R.string.label_granted
            ) + ": $grantedCount / ${appInfo.totalPermissions}"

            binding.statusDot.setBackgroundResource(
                if (appInfo.hasDangerousGranted) R.drawable.dot_danger
                else R.drawable.dot_success
            )

            binding.root.setOnClickListener { onItemClick(appInfo) }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<AppInfo>() {
        override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean =
            oldItem.packageName == newItem.packageName

        override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean =
            oldItem == newItem
    }
}
