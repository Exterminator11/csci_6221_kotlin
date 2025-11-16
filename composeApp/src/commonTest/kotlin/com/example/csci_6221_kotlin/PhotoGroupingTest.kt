package com.example.csci_6221_kotlin

import kotlin.test.Test
import kotlin.test.assertEquals

class PhotoGroupingTest {

    @Test
    fun `group by date groups photos with same date together`() {
        val photos = listOf(
            Photo(id = "1", uri = "uri1", takenAt = "2025-11-14"),
            Photo(id = "2", uri = "uri2", takenAt = "2025-11-14"),
            Photo(id = "3", uri = "uri3", takenAt = "2025-11-13")
        )

        val grouped = groupPhotos(photos, GroupingMode.BY_DATE)

        assertEquals(2, grouped["2025-11-14"]?.size)
        assertEquals(1, grouped["2025-11-13"]?.size)
    }

    @Test
    fun `group by album falls back to Uncategorized when null`() {
        val photos = listOf(
            Photo(id = "1", uri = "uri1", album = "Camera"),
            Photo(id = "2", uri = "uri2", album = null)
        )

        val grouped = groupPhotos(photos, GroupingMode.BY_ALBUM)

        assertEquals(listOf(photos[0]), grouped["Camera"])
        assertEquals(listOf(photos[1]), grouped["Uncategorized"])
    }

    @Test
    fun `group by location groups photos by location name when available`() {
        val photos = listOf(
            Photo(id = "1", uri = "uri1", locationName = "San Francisco, CA"),
            Photo(id = "2", uri = "uri2", locationName = "San Francisco, CA"),
            Photo(id = "3", uri = "uri3", locationName = "New York, NY")
        )

        val grouped = groupPhotos(photos, GroupingMode.BY_LOCATION)

        assertEquals(2, grouped["San Francisco, CA"]?.size)
        assertEquals(1, grouped["New York, NY"]?.size)
    }

    @Test
    fun `group by location falls back to coordinates when location name is null`() {
        val photos = listOf(
            Photo(id = "1", uri = "uri1", latitude = 37.7749, longitude = -122.4194),
            Photo(id = "2", uri = "uri2", latitude = 37.7749, longitude = -122.4194),
            Photo(id = "3", uri = "uri3", latitude = null, longitude = null)
        )

        val grouped = groupPhotos(photos, GroupingMode.BY_LOCATION)

        assertEquals(2, grouped["37.7749, -122.4194"]?.size)
        assertEquals(1, grouped["Unknown location"]?.size)
    }

    @Test
    fun `getLocationDisplay prefers location name over coordinates`() {
        val photo = Photo(
            id = "1",
            uri = "uri1",
            locationName = "San Francisco, CA",
            latitude = 37.7749,
            longitude = -122.4194
        )

        assertEquals("San Francisco, CA", photo.getLocationDisplay())
    }

    @Test
    fun `getLocationDisplay returns coordinates when location name is null`() {
        val photo = Photo(
            id = "1",
            uri = "uri1",
            latitude = 37.7749,
            longitude = -122.4194
        )

        assertEquals("37.7749, -122.4194", photo.getLocationDisplay())
    }

    @Test
    fun `getLocationDisplay returns null when no location data available`() {
        val photo = Photo(id = "1", uri = "uri1")

        assertEquals(null, photo.getLocationDisplay())
    }
}
