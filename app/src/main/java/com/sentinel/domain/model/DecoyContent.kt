package com.sentinel.domain.model

/**
 * Decoy content models (Module 4).
 */
data class DecoyContent(
    val id: Long = 0,
    val category: DecoyCategory,
    val title: String,
    val content: String,
    val metadata: Map<String, String> = emptyMap(),
    val createdAt: Long = System.currentTimeMillis()
)

enum class DecoyCategory {
    GALLERY,
    CONTACTS,
    NOTES,
    FILES,
    RECENT_ACTIVITY
}

data class DecoyContact(
    val name: String,
    val phone: String,
    val email: String = "",
    val avatarColor: String = "#607D8B"
)

data class DecoyNote(
    val title: String,
    val body: String,
    val modifiedAt: Long = System.currentTimeMillis()
)

data class DecoyGalleryItem(
    val title: String,
    val description: String = "",
    val colorHex: String = "#37474F"
)

data class DecoyFile(
    val name: String,
    val size: String,
    val type: String = "document",
    val modifiedAt: Long = System.currentTimeMillis()
)

data class DecoyRecentActivity(
    val appName: String,
    val action: String,
    val timestamp: Long = System.currentTimeMillis()
)
