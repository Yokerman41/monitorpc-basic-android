package com.example

import android.app.Application
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.core.app.ApplicationProvider
import com.example.ui.screens.DashboardScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MonitorViewModel
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class SettingsAlertsUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dashboard_alerts_screenshot() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = MonitorViewModel(app)
        
        // Force alert state by lowering thresholds
        viewModel.updateCpuTempLimit(40)
        viewModel.updateCpuLoadLimit(10)
        
        composeTestRule.setContent {
            MyApplicationTheme {
                DashboardScreen(viewModel = viewModel)
            }
        }

        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/dashboard_alerts.png")
    }
}
