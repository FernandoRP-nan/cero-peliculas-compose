package com.example.cero.data.device

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import com.example.cero.domain.model.UiPerformanceMode
import com.example.cero.domain.repository.DeviceProfileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class AndroidDeviceProfileRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : DeviceProfileRepository {

    override suspend fun detectUiPerformanceMode(): UiPerformanceMode = withContext(Dispatchers.Default) {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryClass = activityManager.memoryClass
        val isLowRam = activityManager.isLowRamDevice
        val cores = Runtime.getRuntime().availableProcessors()
        val sdk = Build.VERSION.SDK_INT

        when {
            isLowRam || memoryClass <= 192 || cores <= 4 || sdk <= Build.VERSION_CODES.Q -> {
                UiPerformanceMode.LOW
            }

            memoryClass >= 320 && cores >= 8 && sdk >= Build.VERSION_CODES.TIRAMISU -> {
                UiPerformanceMode.HIGH
            }

            else -> UiPerformanceMode.BALANCED
        }
    }
}
