package com.example.permissionmanagerpro.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.permissionmanagerpro.adapter.PermissionAdapter
import com.example.permissionmanagerpro.adapter.PermissionRow
import com.example.permissionmanagerpro.databinding.ActivityAppDetailBinding

class AppDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PACKAGE_NAME = "extra_package_name"
        const val EXTRA_APP_NAME = "extra_app_name"
        const val EXTRA_GRANTED = "extra_granted"
        const val EXTRA_DENIED = "extra_denied"
    }

    private lateinit var binding: ActivityAppDetailBinding
    private var packageName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME)
        val appName = intent.getStringExtra(EXTRA_APP_NAME).orEmpty()
        val granted = intent.getStringArrayListExtra(EXTRA_GRANTED).orEmpty()
        val denied = intent.getStringArrayListExtra(EXTRA_DENIED).orEmpty()

        binding.toolbar.title = appName
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.tvDetailAppName.text = appName
        binding.tvDetailPackageName.text = packageName

        val rows = granted.map { PermissionRow(it, true) } +
                denied.map { PermissionRow(it, false) }

        binding.recyclerPermissions.layoutManager = LinearLayoutManager(this)
        binding.recyclerPermissions.adapter = PermissionAdapter(rows)

        binding.btnOpenSettings.setOnClickListener { openAppSettings() }
    }

    private fun openAppSettings() {
        val pkg = packageName ?: return
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", pkg, null)
        }
        startActivity(intent)
    }
}
