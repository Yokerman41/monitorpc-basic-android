package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.viewmodel.MonitorViewModel
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AlertsValidationTest {

    @Test
    fun testAlertThresholdsDefaultAndUpdates() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = MonitorViewModel(app)
        
        // Assert initial default limits in uiState
        assertEquals(80, viewModel.uiState.value.cpuTempLimit)
        assertEquals(90, viewModel.uiState.value.cpuLoadLimit)
        assertEquals(80, viewModel.uiState.value.gpuTempLimit)
        assertEquals(90, viewModel.uiState.value.gpuLoadLimit)
        
        // Update limits
        viewModel.updateCpuTempLimit(85)
        viewModel.updateCpuLoadLimit(95)
        viewModel.updateGpuTempLimit(75)
        viewModel.updateGpuLoadLimit(88)
        
        // Assert updated limits
        assertEquals(85, viewModel.uiState.value.cpuTempLimit)
        assertEquals(95, viewModel.uiState.value.cpuLoadLimit)
        assertEquals(75, viewModel.uiState.value.gpuTempLimit)
        assertEquals(88, viewModel.uiState.value.gpuLoadLimit)
    }
}
