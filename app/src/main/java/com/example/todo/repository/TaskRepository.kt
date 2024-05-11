package com.example.todo.repository

import com.example.todo.database.TaskDatabase
import com.example.todo.model.Task

class TaskRepository(private val db:TaskDatabase) {

    suspend fun insertTask(task: Task) = db.getTaskDao().insertTask(task)

    suspend fun updateTask(task: Task) = db.getTaskDao().updateTask(task)

    suspend fun updateOnTask(taskId: Int, title: String, description: String, date: Long, time: Long) = db.getTaskDao().updateOnTask(taskId,title,description,date,time)

    suspend fun updateCompleted(taskId: Int, completed: Boolean) = db.getTaskDao().updateCompleted(taskId,completed)

    suspend fun deleteTask(task: Task) = db.getTaskDao().deleteTask(task)

    fun getAllTasks() = db.getTaskDao().getAllTasks()

    fun searchTask(query: String?) = db.getTaskDao().searchTask(query)
}