package com.example.csci_6221_kotlin

/**
 * Represents a photo with metadata.
 *
 * @param id Unique identifier for the photo (e.g., UUID or system ID)
 * @param uri Photo file URI or path accessible by the app
 * @param takenAt ISO 8601 date string (e.g., "2025-11-14") or null if unknown
 * @param album Album or category name, null if uncategorized
 * @param latitude GPS latitude coordinate, null if not available
 * @param longitude GPS longitude coordinate, null if not available
 * @param locationName Human-readable location name (e.g., "San Francisco, CA"), null if not available
 */
data class Photo(
    val id: String,
    val uri: String,
    val takenAt: String? = null,
    val album: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationName: String? = null,
) {
    /**
     * Returns a human-readable location string.
     * Prefers locationName if available, otherwise constructs from coordinates.
     */
    fun getLocationDisplay(): String? = when {
        !locationName.isNullOrBlank() -> locationName
        latitude != null && longitude != null -> "$latitude, $longitude"
        else -> null
    }
}

enum class GroupingMode {
    BY_DATE,
    BY_ALBUM,
    BY_LOCATION,
}

