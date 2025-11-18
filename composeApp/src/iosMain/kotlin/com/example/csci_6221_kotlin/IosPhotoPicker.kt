package com.example.csci_6221_kotlin

/**
 * iOS implementation of PhotoPicker.
 *
 * Note: Due to iOS/UIKit restrictions in Kotlin Multiplatform,
 * the actual photo picker UI (PHPickerViewController) should be implemented
 * in the native iOS Swift code. This class provides the data model conversion.
 *
 * To use in your iOS app:
 * 1. In your native Swift code, implement PHPickerViewControllerDelegate
 * 2. When user selects photos, get the PHAssets
 * 3. Call photoFromAsset() for each asset to convert to Photo objects
 */
class IosPhotoPicker : PhotoPicker {

    override suspend fun pickPhotos(): List<Photo> {
        // The actual UI presentation must be handled in Swift/UIKit
        // This method is a placeholder
        return emptyList()
    }

    /**
     * Converts asset metadata to a Photo object.
     * This should be called from native Swift code after PHPickerViewController returns.
     *
     * Parameters would be passed from Swift:
     * - assetId: PHAsset.localIdentifier
     * - creationDate: ISO 8601 formatted date string (e.g., "2025-11-14")
     * - albumName: Album or collection name
     */
    fun photoFromAsset(
        assetId: String,
        creationDate: String?,
        albumName: String?
    ): Photo {
        return Photo(
            id = assetId,
            uri = assetId, // iOS uses localIdentifier as the persistent URI
            takenAt = creationDate,
            album = albumName
        )
    }

    /**
     * Helper to extract album name from the standard iOS photo collections.
     * In Swift, you would call:
     * let collections = PHAssetCollection.fetchAssetCollections(
     *     with: .album,
     *     subtype: .any,
     *     options: nil
     * )
     */
    companion object {
        /**
         * Common iOS album names that can be used as defaults.
         */
        object StandardAlbums {
            const val CAMERA_ROLL = "Camera Roll"
            const val PHOTOS = "Photos"
            const val SCREENSHOTS = "Screenshots"
            const val RECENTLY_DELETED = "Recently Deleted"
        }
    }
}
