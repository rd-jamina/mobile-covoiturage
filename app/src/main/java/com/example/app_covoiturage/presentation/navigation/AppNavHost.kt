package com.example.app_covoiturage.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.app_covoiturage.domain.model.Role
import com.example.app_covoiturage.presentation.auth.login.LoginScreen
import com.example.app_covoiturage.presentation.auth.register.RegisterScreen
import com.example.app_covoiturage.presentation.common.notifications.NotificationsScreen
import com.example.app_covoiturage.presentation.driver.dashboard.DriverDashboardScreen
import com.example.app_covoiturage.presentation.driver.profile.DriverProfileScreen
import com.example.app_covoiturage.presentation.driver.profile.edit.EditPersonalInfoScreen
import com.example.app_covoiturage.presentation.driver.profile.edit.EditVehicleScreen
import com.example.app_covoiturage.presentation.driver.reservations.DriverReservationsScreen
import com.example.app_covoiturage.presentation.driver.reservations.ReservationDetailScreen
import com.example.app_covoiturage.presentation.driver.setup.DriverDocumentScreen
import com.example.app_covoiturage.presentation.driver.setup.DriverPersonalInfoScreen
import com.example.app_covoiturage.presentation.driver.setup.DriverVehicleInfoScreen
import com.example.app_covoiturage.presentation.driver.trip.history.TripHistoryScreen
import com.example.app_covoiturage.presentation.driver.trip.publish.PublishTripScreen
import com.example.app_covoiturage.presentation.driver.vehicle.VehicleFormScreen
import com.example.app_covoiturage.presentation.driver.vehicle.VehicleListScreen
import com.example.app_covoiturage.presentation.onboarding.OnboardingScreen
import com.example.app_covoiturage.presentation.passenger.dashboard.PassengerDashboardScreen
import com.example.app_covoiturage.presentation.passenger.history.PassengerHistoryScreen
import com.example.app_covoiturage.presentation.passenger.payment.PaymentScreen
import com.example.app_covoiturage.presentation.passenger.preferences.PreferencesScreen
import com.example.app_covoiturage.presentation.passenger.profile.PassengerProfileScreen
import com.example.app_covoiturage.presentation.passenger.profile.edit.EditPassengerInfoScreen
import com.example.app_covoiturage.presentation.passenger.setup.PassengerPersonalInfoScreen
import com.example.app_covoiturage.presentation.passenger.trip.detail.TripDetailScreen
import com.example.app_covoiturage.presentation.passenger.trip.results.SearchResultsScreen
import com.example.app_covoiturage.presentation.passenger.trip.search.SearchTripScreen
import com.example.app_covoiturage.presentation.map.TripMapScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.ONBOARDING
    ) {

        // ---------- Onboarding / Auth ----------
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { user ->
                    val destination = when (user.activeRole) {
                        Role.DRIVER -> Routes.DRIVER_DASHBOARD
                        else -> Routes.PASSENGER_DASHBOARD
                    }
                    navController.navigate(destination) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = { user ->
                    val destination = when (user.activeRole) {
                        Role.DRIVER -> Routes.DRIVER_PERSONAL_INFO
                        else -> Routes.PASSENGER_PERSONAL_INFO
                    }
                    navController.navigate(destination) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // ---------- Passager : setup ----------
        composable(Routes.PASSENGER_PERSONAL_INFO) {
            PassengerPersonalInfoScreen(
                onNext = { navController.navigate(Routes.PASSENGER_DASHBOARD) }
            )
        }

        // ---------- Chauffeur : setup ----------
        composable(Routes.DRIVER_PERSONAL_INFO) {
            DriverPersonalInfoScreen(
                onNext = { navController.navigate(Routes.DRIVER_VEHICLE_INFO) }
            )
        }
        composable(Routes.DRIVER_VEHICLE_INFO) {
            DriverVehicleInfoScreen(
                onNext = { navController.navigate(Routes.DRIVER_DOCUMENTS) }
            )
        }
        composable(Routes.DRIVER_DOCUMENTS) {
            DriverDocumentScreen(
                onFinished = {
                    navController.navigate(Routes.DRIVER_DASHBOARD) {
                        popUpTo(Routes.DRIVER_PERSONAL_INFO) { inclusive = true }
                    }
                }
            )
        }

        // ---------- Chauffeur : dashboard + navigation ----------
        composable(Routes.DRIVER_DASHBOARD) {
            DriverDashboardScreen(
                onPublishTrip = { navController.navigate(Routes.DRIVER_PUBLISH_TRIP) },
                onReservationsReceived = { navController.navigate(Routes.DRIVER_RESERVATIONS) },
                onTripHistory = { navController.navigate(Routes.DRIVER_TRIP_HISTORY) },
                onNotifications = { navController.navigate(Routes.DRIVER_NOTIFICATIONS) },
                onProfile = { navController.navigate(Routes.DRIVER_PROFILE) },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ---------- Chauffeur : Publier un trajet (saisie manuelle) ----------
        composable(Routes.DRIVER_PUBLISH_TRIP) {
            PublishTripScreen(
                onPublished = { navController.popBackStack() }
            )
        }

        // ---------- Chauffeur : réservations ----------
        composable(Routes.DRIVER_RESERVATIONS) {
            DriverReservationsScreen(
                onReservationClick = { id ->
                    navController.navigate("driver_reservation_detail/$id")
                }
            )
        }

        composable(
            route = Routes.DRIVER_RESERVATION_DETAIL,
            arguments = listOf(navArgument("reservationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val reservationId = backStackEntry.arguments?.getString("reservationId") ?: ""
            ReservationDetailScreen(
                reservationId = reservationId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.DRIVER_TRIP_HISTORY) {
            TripHistoryScreen(
                onViewMap = { trip ->
                    navController.navigate(
                        "driver_trip_map/${trip.originLat}/${trip.originLng}/${trip.destinationLat}/${trip.destinationLng}"
                    )
                }
            )
        }

        composable(
            route = Routes.DRIVER_TRIP_MAP,
            arguments = listOf(
                navArgument("originLat") { type = NavType.StringType },
                navArgument("originLng") { type = NavType.StringType },
                navArgument("destLat") { type = NavType.StringType },
                navArgument("destLng") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val originLat = backStackEntry.arguments?.getString("originLat")?.toDoubleOrNull() ?: 0.0
            val originLng = backStackEntry.arguments?.getString("originLng")?.toDoubleOrNull() ?: 0.0
            val destLat = backStackEntry.arguments?.getString("destLat")?.toDoubleOrNull() ?: 0.0
            val destLng = backStackEntry.arguments?.getString("destLng")?.toDoubleOrNull() ?: 0.0

            TripMapScreen(
                originLat = originLat,
                originLng = originLng,
                destinationLat = destLat,
                destinationLng = destLng
            )
        }

        composable(Routes.DRIVER_NOTIFICATIONS) {
            NotificationsScreen()
        }

        composable(Routes.DRIVER_PROFILE) {
            DriverProfileScreen(
                onEditPersonalInfo = { navController.navigate(Routes.DRIVER_EDIT_PERSONAL_INFO) },
                onEditVehicle = { navController.navigate(Routes.DRIVER_VEHICLE_LIST) },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.DRIVER_EDIT_PERSONAL_INFO) {
            EditPersonalInfoScreen(
                onSaved = { navController.popBackStack() }
            )
        }
        composable(Routes.DRIVER_EDIT_VEHICLE) {
            EditVehicleScreen(
                onSaved = { navController.popBackStack() }
            )
        }

        // ---------- Chauffeur : véhicules (multi-véhicules) ----------
        composable(Routes.DRIVER_VEHICLE_LIST) {
            VehicleListScreen(
                onAddVehicle = { navController.navigate("driver_vehicle_form/new") },
                onEditVehicle = { id -> navController.navigate("driver_vehicle_form/$id") }
            )
        }
        composable(
            "driver_vehicle_form/{vehicleId}",
            arguments = listOf(navArgument("vehicleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("vehicleId")
            VehicleFormScreen(
                vehicleId = if (id == "new") null else id,
                onSaved = { navController.popBackStack() }
            )
        }

        // ---------- Passager : dashboard ----------
        composable(Routes.PASSENGER_DASHBOARD) {
            PassengerDashboardScreen(
                onSearchTrip = { navController.navigate(Routes.PASSENGER_SEARCH_TRIP) },
                onHistory = { navController.navigate(Routes.PASSENGER_HISTORY) },
                onNotifications = { navController.navigate(Routes.PASSENGER_NOTIFICATIONS) },
                onProfile = { navController.navigate(Routes.PASSENGER_PROFILE) },
                onPreferences = { navController.navigate(Routes.PASSENGER_PREFERENCES) },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ---------- Passager : recherche / résultats / détail ----------
        composable(Routes.PASSENGER_SEARCH_TRIP) {
            SearchTripScreen(
                onBack = { navController.popBackStack() },
                onSearch = { origin, destination, date ->
                    navController.navigate("passenger_search_results/$origin/$destination/$date")
                }
            )
        }

        composable(
            route = Routes.PASSENGER_SEARCH_RESULTS,
            arguments = listOf(
                navArgument("origin") { type = NavType.StringType },
                navArgument("destination") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType }
            )
        ) {
            SearchResultsScreen(
                onBack = { navController.popBackStack() },
                onTripClick = { tripId -> navController.navigate("passenger_trip_detail/$tripId") }
            )
        }

        composable(
            route = Routes.PASSENGER_TRIP_DETAIL,
            arguments = listOf(navArgument("tripId") { type = NavType.StringType })
        ) {
            TripDetailScreen(
                onBack = { navController.popBackStack() },
                onBooked = { reservationId, totalPrice ->
                    navController.navigate("passenger_payment/$reservationId/$totalPrice")
                }
            )
        }

        composable(
            route = Routes.PASSENGER_PAYMENT,
            arguments = listOf(
                navArgument("reservationId") { type = NavType.StringType },
                navArgument("totalPrice") { type = NavType.StringType }
            )
        ) {
            PaymentScreen(
                onBack = { navController.popBackStack() },
                onPaymentSuccess = {
                    navController.navigate(Routes.PASSENGER_DASHBOARD) {
                        popUpTo(Routes.PASSENGER_SEARCH_TRIP) { inclusive = true }
                    }
                }
            )
        }

        // ---------- Passager : historique / notifications / profil ----------
        composable(Routes.PASSENGER_HISTORY) {
            PassengerHistoryScreen()
        }

        composable(Routes.PASSENGER_NOTIFICATIONS) {
            NotificationsScreen()
        }

        composable(Routes.PASSENGER_PROFILE) {
            PassengerProfileScreen(
                onEditPersonalInfo = { navController.navigate(Routes.PASSENGER_EDIT_PERSONAL_INFO) },
                onPreferences = { navController.navigate(Routes.PASSENGER_PREFERENCES) },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.PASSENGER_PREFERENCES) {
            PreferencesScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PASSENGER_EDIT_PERSONAL_INFO) {
            EditPassengerInfoScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}