package ru.mooncalendar.data.database.notes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = false) val date: String,
    val description: String
)