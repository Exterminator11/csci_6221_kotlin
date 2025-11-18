package com.example.csci_6221_kotlin

/**
 * Interface for platform-specific photo picking functionality.
 */
interface PhotoPicker {
    /**
     * Opens the system photo picker and returns the selected photos.
     * @return List of Photo objects, empty if user cancelled
     */
    suspend fun pickPhotos(): List<Photo>
}
