package ru.mooncalendar.data.database.pedometer

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface GoalDao {
    @Insert
    fun insert(goal: Goal)
    @Update
    fun update(goal: Goal)
    @Query("UPDATE goals_table SET steps = :steps")
    suspend fun updateAllSteps(steps: Int)
    @Delete
    fun delete(goal: Goal)
    @Query("SELECT * FROM goals_table WHERE id = :key")
    fun get(key: Long): Goal?
    @Query("SELECT * FROM goals_table ORDER BY id DESC LIMIT 1")
    fun getLast(): LiveData<Goal>
    @Query("SELECT * FROM goals_table WHERE name = :name")
    fun get(name: String): Goal?
    @Query("SELECT * FROM goals_table")
    fun getAllObservable(): LiveData<List<Goal>>
}
