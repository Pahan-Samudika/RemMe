package com.example.todo.repository

import com.example.todo.database.TaskDatabase
import com.example.todo.model.Task

class TaskRepository(private val db:TaskDatabase) {

    suspend fun insertTask(task: Task) = db.getTaskDao().insertTask(task)

    suspend fun updateTask(task: Task) = db.getTaskDao().updateTask(task)

    suspend fun deleteTask(task: Task) = db.getTaskDao().deleteTask(task)

    fun getAllTasks() = db.getTaskDao().getAllTasks()

    fun searchDatabase(searchQuery: String) = db.getTaskDao().searchDatabase(searchQuery)
}