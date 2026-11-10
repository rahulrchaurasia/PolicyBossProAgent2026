package com.policyboss.policybosspro.navigation.graphs

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.policyboss.policybosspro.feature.login.ui.CreateAccountScreen
import com.policyboss.policybosspro.anim.NavigationAnimations
import com.policyboss.policybosspro.feature.login.ui.LoginScreen
import com.policyboss.policybosspro.feature.login.ui.VerifyAccountScreen
import com.policyboss.policybosspro.feature.login.viewmodel.LoginViewModel
import com.policyboss.policybosspro.feature.register.ui.RegisterScreen
import com.policyboss.policybosspro.feature.syncContact.ui.SyncContactsRoute
import com.policyboss.policybosspro.feature.syncContact.ui.SyncProcessingRoute
import com.policyboss.policybosspro.feature.syncContact.ui.welcome.WelcomeSyncContactRoute

import com.policyboss.policybosspro.feature.onboarding.ui.OnboardingPagerScreen
import com.policyboss.policybosspro.navigation.AppNavigator
import com.policyboss.policybosspro.navigation.Dest

/*
Current Navigation Architecture
1. Root Navigation (Global)
kotlinRootNavGraph -> NavHost with globalActions
├── Splash
├── Auth (Welcome, Login, Register)
└── MainHome (Contains bottom navigation)
2. Bottom Navigation (Local)
kotlinMainApp -> Manual tab switching with selectedIndex
├── Home
├── WishList
├── Cart
└── Profile
 */


fun NavGraphBuilder.authGraph(
    navigator: AppNavigator,
    startDestination: Any = Dest.Welcome
) {

    navigation<Dest.AuthGraph>(
        startDestination = startDestination
    ) {
        // Welcome Screen
        composable<Dest.Welcome>(
            enterTransition = { NavigationAnimations.slideInRight },
            exitTransition = { NavigationAnimations.slideOutLeft },
            popEnterTransition = { NavigationAnimations.slideInLeft },
            popExitTransition = { NavigationAnimations.slideOutRight }
        ) {

            OnboardingPagerScreen(
                onFinish = {
                    navigator.navigateToLogin()
                }
            )
        }

        // ✅ ADD THIS - Login Screen (This was missing!)
        composable<Dest.Login>(
            enterTransition = { NavigationAnimations.slideInRight },
            exitTransition = { NavigationAnimations.slideOutLeft },
            popEnterTransition = { NavigationAnimations.slideInLeft },
            popExitTransition = { NavigationAnimations.slideOutRight }
        ) {


            LoginScreen(

                onNavigateToRegister = {

                    navigator.navigateToRegister()
                },

                onNavigateToVerifyOtp = {
                        fullName,
                        mobileNumber,
                        loginInput,
                        ssId,
                        source ->

                    navigator.navigateTo(

                        Dest.VerifyAccount(

                            fullName = fullName,

                            mobileNumber = mobileNumber,

                            source = source,

                            loginInput = loginInput,

                            ssId = ssId
                        )
                    )
                }
            )
        }


        composable<Dest.Register>(
            enterTransition = { NavigationAnimations.slideInRight },
            exitTransition = { NavigationAnimations.slideOutLeft },
            popEnterTransition = { NavigationAnimations.slideInLeft },
            popExitTransition = { NavigationAnimations.slideOutRight }
        ) {
            RegisterScreen(
                onBackClick = {
                    navigator.navigateBack()
                },
                onRegistrationSuccess = { _ ->
                    // Registration Flow: Login -> RegisterScreen -> WelcomeSyncScreen -> SyncContactsScreen -> SyncProcessingScreen -> Home
                    navigator.navigateTo(Dest.WelcomeSyncContact) {
                        popUpTo<Dest.Login> {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<Dest.VerifyAccount>(
            enterTransition = { NavigationAnimations.slideInRight },
            exitTransition = { NavigationAnimations.slideOutLeft },
            popEnterTransition = { NavigationAnimations.slideInLeft },
            popExitTransition = { NavigationAnimations.slideOutRight }
        ) {


            val args =
                it.toRoute<Dest.VerifyAccount>()

            VerifyAccountScreen(

                navigator = navigator,

                fullName = args.fullName,

                mobileNumber = args.mobileNumber,

                source = args.source,

                loginInput = args.loginInput.ifBlank { args.mobileNumber },

                ssId = args.ssId
            )
        }

        composable<Dest.WelcomeSyncContact>(
            enterTransition = { NavigationAnimations.slideInRight },
            exitTransition = { NavigationAnimations.slideOutLeft },
            popEnterTransition = { NavigationAnimations.slideInLeft },
            popExitTransition = { NavigationAnimations.slideOutRight }
        ) {
            WelcomeSyncContactRoute(
                onNavigateToMainSyncContact = {
                    navigator.navigateTo(Dest.SyncContact)
                },
                onBackClick = {
                    navigator.navigateBack()
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
                    navigator.navigateBack()
                },
                onNavigateToProcessing = {
                    navigator.navigateTo(Dest.SyncContactProcess)
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
                    navigator.navigateToHome()
                }
            )
        }
    }
}