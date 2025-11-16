package com.example.csci_6221_kotlin

/**
 * Groups photos by the specified mode.
 *
 * @param photos List of photos to group
 * @param mode Grouping mode (BY_DATE, BY_ALBUM, or BY_LOCATION)
 * @return A map where keys are group labels and values are lists of photos in that group
 */
fun groupPhotos(
    photos: List<Photo>,
    mode: GroupingMode,
): Map<String, List<Photo>> {
    return when (mode) {
        GroupingMode.BY_DATE ->
            photos.groupBy { photo ->
                photo.takenAt ?: "Unknown date"
            }

        GroupingMode.BY_ALBUM ->
            photos.groupBy { photo ->
                photo.album ?: "Uncategorized"
            }

        GroupingMode.BY_LOCATION ->
            photos.groupBy { photo ->
                photo.getLocationDisplay() ?: "Unknown location"
            }
    }
}