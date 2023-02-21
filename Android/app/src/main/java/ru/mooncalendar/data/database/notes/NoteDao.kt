package ru.mooncalendar.data.database.notes

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface NoteDao {

    @Upsert
    suspend fun upsert(note: Note)

    @Query("SELECT * FROM notes WHERE date = :date")
    fun getByDate(date: String): LiveData<Note>
}