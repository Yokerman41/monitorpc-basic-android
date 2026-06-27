package com.example

import android.app.Application
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.core.app.ApplicationProvider
import com.example.ui.screens.*
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
class FullAppScreenshotsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val application = ApplicationProvider.getApplicationContext<Application>()
    private val viewModel = MonitorViewModel(application)

    @Test
    fun capture_login_screen() {
        composeTestRule.setContent {
            MyApplicationTheme {
                LoginScreen(viewModel = viewModel)
            }
        }
        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/login.png")
    }

    @Test
    fun capture_dashboard_screen() {
        composeTestRule.setContent {
            MyApplicationTheme {
                DashboardScreen(viewModel = viewModel)
            }
        }
        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/dashboard_full.png")
    }

    @Test
    fun capture_storage_screen() {
        composeTestRule.setContent {
            MyApplicationTheme {
                StorageScreen(viewModel = viewModel)
            }
        }
        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/storage.png")
    }

    @Test
    fun capture_settings_screen() {
        composeTestRule.setContent {
            MyApplicationTheme {
                SettingsScreen(viewModel = viewModel)
            }
        }
        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/settings.png")
    }
}
