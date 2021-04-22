package me.profiluefter.profinote.data.geocoding

interface GeocodingService {
    suspend fun reverse(latitude: Double, longitude: Double): String
}