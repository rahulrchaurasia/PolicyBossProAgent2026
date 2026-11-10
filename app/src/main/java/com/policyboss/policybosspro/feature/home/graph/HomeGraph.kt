package com.policyboss.policybosspro.feature.home.graph

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.policyboss.policybosspro.anim.NavigationAnimations
import com.policyboss.policybosspro.feature.home.ui_home.HomeRoute
import com.policyboss.policybosspro.feature.home.ui_Menu.MenuRoute
import com.policyboss.policybosspro.feature.syncContact.ui.SyncContactsRoute
import com.policyboss.policybosspro.feature.syncContact.ui.SyncProcessingRoute
import com.policyboss.policybosspro.navigation.AppNavigator
import com.policyboss.policybosspro.navigation.Dest
import androidx.navigation.toRoute
import com.policyboss.policybosspro.feature.profile.ui.ProfileRoute


/*
What is toRoute()?

toRoute() is an extension function provided by Navigation Compose's
type-safe navigation.

Its job is:
Read the navigation arguments from the NavBackStackEntry and convert
them back into your destination object.
 */

fun NavGraphBuilder.homeGraph(


    appNavigator: AppNavigator,
    padding: PaddingValues

) {

    navigation<Dest.HomeGraph>(
        startDestination = Dest.Home
    ) {

        composable<Dest.Home> {

            HomeRoute(
                modifier = Modifier,
               // contentPadding = PaddingValues(0.dp),
                contentPadding = padding, // <--- 2. FIX: Pass the scaffold padding here! Not PaddingValues(0.dp)
                // --- PASS THE NAVIGATION CALLBACKS HERE ---
                onNavigateToProfile = {
                    appNavigator.navigateTo(Dest.Profile) // Adjust based on your AppNavigator setup
                },
                onGetQuoteClick = { registrationNumber ->
                    // Handle get quote click
                },
                onNavigateToMenu = {
                    appNavigator.navigateTo(Dest.Menu)
                },
                onNavigateToNewCar = { isNewCar ->
                    appNavigator.navigateTo(Dest.NewCarInsurance(isNewCar = isNewCar))
                },
                onNavigateToSyncContact = {
                    appNavigator.navigateTo(Dest.SyncContact)
                },
                onNavigateToWebView = { url, title ->
                    appNavigator.navigateTo(Dest.CommonWebView(url = url, title = title))
                }
            )
        }

        composable<Dest.Menu>(
            // 1. Moving FORWARD into Menu (Appears from Bottom to Top)
            enterTransition = { NavigationAnimations.slideInBottom },

            // When navigating DEEPER from Menu (Menu hides smoothly)
            exitTransition = { NavigationAnimations.slideOutTop },

            // When returning TO Menu from a deeper screen (Menu reappears smoothly)
            popEnterTransition = { NavigationAnimations.slideInTop },

            // 2. Moving BACKWARD out of Menu (Closes from Top to Bottom)
            popExitTransition = { NavigationAnimations.slideOutBottom }
        ) {
            MenuRoute(
                contentPadding = padding,
                onCloseClick = {
                    // Trigger normal back navigation to close Menu
                    appNavigator.navigateBack()
                },
                onNavigateToProfile = {
                    appNavigator.navigateTo(Dest.Profile)
                },
                onLogoutSuccess = {
                    appNavigator.navigateToLoginAfterLogout()
                }
            )
        }

        composable<Dest.NewCarInsurance>(
            enterTransition = { NavigationAnimations.slideInRight },
            exitTransition = { NavigationAnimations.slideOutLeft },
            popEnterTransition = { NavigationAnimations.slideInLeft },
            popExitTransition = { NavigationAnimations.slideOutRight }
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<Dest.NewCarInsurance>()
            com.policyboss.policybosspro.feature.carInsurance.ui.NewCarRoute(
                isNewCar = route.isNewCar,
                onNavigateBack = {
                    appNavigator.navigateBack()
                },
                onNavigateToQuotes = { quoteId, quoteUrl ->
                    if (!quoteUrl.isNullOrBlank()) {
                        appNavigator.navigateTo(Dest.CommonWebView(url = quoteUrl, title = "Car Insurance Quotes"))
                    } else {
                        appNavigator.navigateBack()
                    }
                }
            )
        }

        composable<Dest.CommonWebView>(
            enterTransition = { NavigationAnimations.slideInRight },
            exitTransition = { NavigationAnimations.slideOutLeft },
            popEnterTransition = { NavigationAnimations.slideInLeft },
            popExitTransition = { NavigationAnimations.slideOutRight }
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<Dest.CommonWebView>()
            com.policyboss.policybosspro.feature.common.CommonWebViewScreen(
                url = route.url,
                title = route.title,
                onNavigateBack = {
                    appNavigator.navigateBack()
                }
            )
        }

        composable<Dest.SyncContact>(
            enterTransition = { NavigationAnimations.slideInRight },
            exitTransition = { NavigationAnimations.slideOutLeft },
            popEnterTransition = { NavigationAnimations.slideInLeft },
            popExitTransition = { NavigationAnimations.slideOutRight }
        ) {
            SyncContactsRoute(
                onBackClick = {
                    appNavigator.navigateBack()
                },
                onNavigateToProcessing = {
                    appNavigator.navigateTo(Dest.SyncContactProcess)
                }
            )
        }

        composable<Dest.SyncContactProcess>(
            enterTransition = { NavigationAnimations.slideInRight },
            exitTransition = { NavigationAnimations.slideOutLeft },
            popEnterTransition = { NavigationAnimations.slideInLeft },
            popExitTransition = { NavigationAnimations.slideOutRight }
        ) {
            SyncProcessingRoute(
                onNavigateToHome = {
                    appNavigator.safePopBackToRoute(
                        destination = Dest.HomeGraph,
                        inclusive = false,
                        fallback = { appNavigator.navigateToHome() }
                    )
                }
            )
        }

        composable<Dest.Profile>(
            enterTransition = { NavigationAnimations.slideInBottom },
            exitTransition = { NavigationAnimations.slideOutTop },
            popEnterTransition = { NavigationAnimations.slideInTop },
            popExitTransition = { NavigationAnimations.slideOutBottom }


        ) {
            ProfileRoute(
                onNavigateBack = {
                    appNavigator.navigateBack()
                },
                onNavigateToEditPersonalInfo = {
                    appNavigator.navigateTo(Dest.EditPersonalInfo)
                }
            )
        }

        composable<Dest.EditPersonalInfo>(
            enterTransition = { NavigationAnimations.slideInRight },
            exitTransition = { NavigationAnimations.slideOutLeft },
            popEnterTransition = { NavigationAnimations.slideInLeft },
            popExitTransition = { NavigationAnimations.slideOutRight }
        ) {
            com.policyboss.policybosspro.feature.profile.ui.EditPersonalInfoRoute(
                onNavigateBack = {
                    appNavigator.navigateBack()
                }
            )
        }
    }
}