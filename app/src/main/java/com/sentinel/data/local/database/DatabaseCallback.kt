package com.sentinel.data.local.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sentinel.data.local.entity.ProfileEntity
import com.sentinel.domain.model.*

/**
 * Seeds default emergency profiles and sample decoy content on first launch.
 */
class DatabaseCallback(
    private val scope: CoroutineScope
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        // Room entity seeding happens via repository on first app launch
    }

    companion object {
        fun defaultProfiles(): List<ProfileEntity> = listOf(
            ProfileEntity(
                name = "Normal Mode",
                type = ProfileType.NORMAL,
                isDefault = true,
                isActive = true,
                color = "#00E5FF",
                icon = "shield"
            ),
            ProfileEntity(
                name = "Travel Mode",
                type = ProfileType.TRAVEL,
                isDefault = true,
                notificationBehavior = NotificationBehavior.SILENT,
                color = "#FFB300",
                icon = "flight"
            ),
            ProfileEntity(
                name = "Border Crossing Mode",
                type = ProfileType.BORDER_CROSSING,
                isDefault = true,
                notificationBehavior = NotificationBehavior.SUPPRESSED,
                decoySettings = DecoySettings(enabled = true),
                color = "#FF5722",
                icon = "flag"
            ),
            ProfileEntity(
                name = "Emergency Mode",
                type = ProfileType.EMERGENCY,
                isDefault = true,
                notificationBehavior = NotificationBehavior.SUPPRESSED,
                decoySettings = DecoySettings(enabled = true),
                color = "#F44336",
                icon = "emergency"
            )
        )

        data class DecoySeed(val category: DecoyCategory, val title: String, val content: String)

        fun defaultDecoyContent(): List<DecoySeed> = listOf(
            DecoySeed(DecoyCategory.CONTACTS, "John Smith", """{"name":"John Smith","phone":"+1 555-0100","email":"john@email.com"}"""),
            DecoySeed(DecoyCategory.CONTACTS, "Jane Doe", """{"name":"Jane Doe","phone":"+1 555-0101","email":"jane@email.com"}"""),
            DecoySeed(DecoyCategory.NOTES, "Shopping List", """{"title":"Shopping List","body":"Milk, Bread, Eggs"}"""),
            DecoySeed(DecoyCategory.NOTES, "Meeting Notes", """{"title":"Meeting Notes","body":"Discuss project timeline"}"""),
            DecoySeed(DecoyCategory.GALLERY, "Vacation Photo", """{"title":"Vacation Photo","description":"Beach sunset"}"""),
            DecoySeed(DecoyCategory.FILES, "Document.pdf", """{"name":"Document.pdf","size":"2.4 MB","type":"pdf"}"""),
            DecoySeed(DecoyCategory.RECENT_ACTIVITY, "Photos", """{"appName":"Photos","action":"Viewed album"}""")
        )
    }
}
