package com.example.app_covoiturage.data.remote.nominatim

import com.google.gson.annotations.SerializedName

data class NominatimResponse(
    @SerializedName("display_name") val displayName: String?,
    val address: NominatimAddress?
)

data class NominatimAddress(
    val city: String?,
    val town: String?,
    val village: String?,
    val county: String?,
    val state: String?
)