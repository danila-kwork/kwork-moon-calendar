package ru.mooncalendar.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.mooncalendar.data.database.notes.Note
import ru.mooncalendar.data.database.notes.NoteDao
import ru.mooncalendar.data.database.pedometer.Day
import ru.mooncalendar.data.database.pedometer.DayDao
import ru.mooncalendar.data.database.pedometer.Goal
import ru.mooncalendar.data.database.pedometer.GoalDao

@Database(
    entities = [Goal::class, Day::class, Note::class],
    version = 7,
    exportSchema = false
)
abstract class MainDatabase : RoomDatabase() {
    abstract val goalDao: GoalDao
    abstract val dayDao: DayDao
    abstract val noteDao: NoteDao

    companion object {
        @Volatile
        private var INSTANCE: MainDatabase? = null
        fun getInstance(context: Context): MainDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MainDatabase::class.java,
                        "main_database"
                    )
                        .fallbackToDestructiveMigration()
                        .addCallback(CALLBACK)
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }

        private val CALLBACK = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                db.execSQL("INSERT INTO goals_table VALUES (NULL, 'Default Goal', 2000)")
                db.execSQL("INSERT INTO days_table VALUES (NULL, date(), 0, 1, 'Default Goal', 2000)")
            }
        }
    }
}
