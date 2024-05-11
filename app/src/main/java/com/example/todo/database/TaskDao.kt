package com.example.todo.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todo.model.Task


@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Query("UPDATE tasks SET completed = :completed WHERE id = :taskId")
    suspend fun updateCompleted(taskId: Int, completed: Boolean)

    @Query("UPDATE tasks SET title = :title, description=:description, date=:date, time=:time WHERE id = :taskId")
    suspend fun updateOnTask(taskId: Int, title: String, description: String, date: Long, time: Long)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE title LIKE :query OR description LIKE :query")
    fun searchTask(query: String?): LiveData<List<Task>>

}