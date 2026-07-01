package com.example.permissionmanagerpro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.permissionmanagerpro.model.AppInfo
import com.example.permissionmanagerpro.util.PermissionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _apps = MutableLiveData<List<AppInfo>>()
    val apps: LiveData<List<AppInfo>> = _apps

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadInstalledApps(includeSystemApps: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = withContext(Dispatchers.IO) {
                PermissionUtils.getInstalledAppsWithPermissions(
                    getApplication(),
                    includeSystemApps
                ).sortedByDescending { it.hasDangerousGranted }
            }
            _apps.value = result
            _isLoading.value = false
        }
    }
}
