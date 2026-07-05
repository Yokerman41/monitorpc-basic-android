package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.WindowInsetsCompat
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.StorageScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ComputersScreen
import com.example.ui.screens.ProcessesScreen
import com.example.ui.screens.ScriptsScreen
import com.example.ui.components.TutorialDialog
import com.example.ui.theme.*
import com.example.viewmodel.MonitorViewModel
import com.example.ui.utils.tr

class MainActivity : ComponentActivity() {
  private val viewModel: MonitorViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Configure sticky immersive mode (transient status & navigation bars)
    val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
    windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

    setContent {
      val state by viewModel.uiState.collectAsState()
      MyApplicationTheme(themeJson = state.customThemeColors) {
        var activeTab by rememberSaveable { mutableIntStateOf(0) }

        Scaffold(
          modifier = Modifier.fillMaxSize(),
          bottomBar = {
            CustomBottomBar(
              activeIndex = activeTab,
              onTabClick = { activeTab = it }
            )
          },
          containerColor = Background
        ) { innerPadding ->
          // Animated Crossfade transition between screens
          Crossfade(
            targetState = activeTab,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            label = "screenTransition"
          ) { tab ->
            when (tab) {
              0 -> DashboardScreen(viewModel = viewModel)
              1 -> StorageScreen(viewModel = viewModel)
              2 -> SettingsScreen(viewModel = viewModel)
            }
          }
        }

        if (state.showTutorial) {
          TutorialDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.dismissTutorial() }
          )
        }


        if (state.showComputersScreen) {
          ComputersScreen(viewModel = viewModel)
        }

        if (state.showProcessesScreen) {
          ProcessesScreen(viewModel = viewModel)
        }

        if (state.showScriptsScreen) {
          ScriptsScreen(viewModel = viewModel)
        }

      }
    }
  }
}

@Composable
fun CustomBottomBar(
    activeIndex: Int,
    onTabClick: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        color = SurfaceContainerLowest,
        border = BorderStroke(1.dp, BorderSlate)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .windowInsetsPadding(WindowInsets.navigationBars) // respects screen notch and edge gesture safety caps
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val tabs = listOf(
                    BottomTabItem("Panel".tr(), Icons.Filled.Dashboard, 0),
                    BottomTabItem("Almacenamiento".tr(), Icons.Filled.Storage, 1),
                    BottomTabItem("Ajustes".tr(), Icons.Filled.Settings, 2)
                )

                tabs.forEach { tab ->
                    val isSelected = activeIndex == tab.index
                    val capBgColor = if (isSelected) PrimaryContainerGreen else Color.Transparent
                    val labelColor = if (isSelected) OnPrimaryContainerGreen else OnSurfaceTextVariant

                    Column(
                        modifier = Modifier
                            .width(90.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(capBgColor)
                            .clickable { onTabClick(tab.index) }
                            .padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            tint = labelColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tab.label,
                            color = labelColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

data class BottomTabItem(
    val label: String,
    val icon: ImageVector,
    val index: Int
)
