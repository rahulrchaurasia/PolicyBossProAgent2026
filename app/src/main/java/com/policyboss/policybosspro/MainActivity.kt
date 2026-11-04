package com.policyboss.policybosspro

import android.os.Bundle
import androidx.activity.enableEdgeToEdge

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.policyboss.policybosspro.core.LocalNavController
import com.policyboss.policybosspro.navigation.AppRoot // Update import path if placed in navigation
import com.policyboss.policybosspro.root.RootViewModel
import com.policyboss.policybosspro.ui.theme.PolicyBossTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = androidx.activity.SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = androidx.activity.SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )

        setContent {
            val rootViewModel: RootViewModel = hiltViewModel()

            val uiState by rootViewModel.uiState.collectAsStateWithLifecycle()

            splashScreen.setKeepOnScreenCondition {
                uiState.isLoading
            }

            uiState.startDestination?.let { startDestination ->
                PolicyBossTheme {
                    val navController = rememberNavController()
                    val destinationOverride = intent.getStringExtra(EXTRA_START_DESTINATION)

                    CompositionLocalProvider(
                        LocalNavController provides navController
                    ) {
                        AppRoot(
                            navController = navController,
                            startDestination = startDestination,
                            initialDestinationOverride = destinationOverride
                        )
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_START_DESTINATION = "extra_start_destination"
        const val DEST_SYNC_CONTACT = "dest_sync_contact"
    }
}

/*
LocalNavController provides navController means:

"Store this navController in the Compose tree so that any child composable
can access it without passing it as a parameter."
 */



//class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_main)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//    }
//}