package com.example.app_covoiturage.presentation.navigation

object Routes {
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val DRIVER_TRIP_MAP = "driver_trip_map/{originLat}/{originLng}/{destLat}/{destLng}"
    const val REGISTER = "register"

    const val DRIVER_PERSONAL_INFO = "driver_personal_info"
    const val DRIVER_VEHICLE_INFO = "driver_vehicle_info"
    const val DRIVER_DOCUMENTS = "driver_documents"
    const val DRIVER_DASHBOARD = "driver_dashboard"
    const val DRIVER_PUBLISH_TRIP = "driver_publish_trip"
    const val DRIVER_RESERVATIONS = "driver_reservations"
    const val DRIVER_RESERVATION_DETAIL = "driver_reservation_detail/{reservationId}"
    const val DRIVER_TRIP_HISTORY = "driver_trip_history"
    const val DRIVER_NOTIFICATIONS = "driver_notifications"
    const val DRIVER_PROFILE = "driver_profile"
    const val DRIVER_EDIT_PERSONAL_INFO = "driver_edit_personal_info"
    const val DRIVER_EDIT_VEHICLE = "driver_edit_vehicle"
    const val DRIVER_VEHICLE_LIST = "driver_vehicle_list"

    const val PASSENGER_PERSONAL_INFO = "passenger_personal_info"
    const val PASSENGER_DASHBOARD = "passenger_dashboard"
    const val PASSENGER_SEARCH_TRIP = "passenger_search_trip"
    const val PASSENGER_SEARCH_RESULTS = "passenger_search_results/{origin}/{destination}/{date}"
    const val PASSENGER_TRIP_DETAIL = "passenger_trip_detail/{tripId}"
    const val PASSENGER_PAYMENT = "passenger_payment/{reservationId}/{totalPrice}"
    const val PASSENGER_HISTORY = "passenger_history"
    const val PASSENGER_NOTIFICATIONS = "passenger_notifications"
    const val PASSENGER_PROFILE = "passenger_profile"
    const val PASSENGER_EDIT_PERSONAL_INFO = "passenger_edit_personal_info"
    const val PASSENGER_PREFERENCES = "passenger_preferences"

    const val LOCATION_PICKER_ORIGIN = "location_picker_origin"
    const val LOCATION_PICKER_DESTINATION = "location_picker_destination"
}