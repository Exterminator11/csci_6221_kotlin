package com.example.csci_6221_kotlin

import android.content.Context
import android.net.Uri
import android.provider.MediaStore

/**
 * Android implementation of PhotoPicker using the system photo picker.
 * Extracts metadata (date taken) from MediaStore.
 *
 * Usage: Use with ActivityResultContracts.GetMultipleContents() or
 * ActivityResultContracts.PickMultipleVisualMedia() in your Compose UI
 * to show the photo picker dialog.
 */
class AndroidPhotoPicker(private val context: Context) : PhotoPicker {

    override suspend fun pickPhotos(): List<Photo> {
        // This is implemented via ActivityResultContracts in the UI layer
        return emptyList()
    }

    /**
     * Converts a content URI to a Photo object with metadata extracted from MediaStore.
     * Call this for each URI returned by the photo picker.
     */
    fun photoFromUri(uri: Uri): Photo? = try {
        val (takenAt, album) = extractMetadata(uri)
        Photo(
            id = uri.lastPathSegment ?: uri.toString(),
            uri = uri.toString(),
            takenAt = takenAt,
            album = album
        )
    } catch (e: Exception) {
        // Log or handle error; return null to skip this photo
        null
    }

    /**
     * Extracts date taken and album name from MediaStore for the given URI.
     * Returns a pair of (takenAt, album).
     */
    private fun extractMetadata(uri: Uri): Pair<String?, String?> {
        val projection = arrayOf(
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATE_TAKEN,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME
        )

        var takenAt: String? = null
        var album: String? = null

        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                // Get date taken (milliseconds since epoch)
                val dateTakenIdx = cursor.getColumnIndex(MediaStore.MediaColumns.DATE_TAKEN)
                if (dateTakenIdx >= 0) {
                    val timestamp = cursor.getLong(dateTakenIdx)
                    if (timestamp > 0) {
                        takenAt = formatDateFromTimestamp(timestamp)
                    }
                }

                // Get bucket/album name
                val bucketIdx = cursor.getColumnIndex(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME)
                if (bucketIdx >= 0) {
                    album = cursor.getString(bucketIdx)
                }
            }
        }

        return Pair(takenAt, album)
    }

    /**
     * Converts timestamp (milliseconds) to ISO 8601 date string (YYYY-MM-DD).
     */
    private fun formatDateFromTimestamp(timestampMs: Long): String {
        val date = java.util.Date(timestampMs)
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        return sdf.format(date)
    }
}
