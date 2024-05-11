package com.example.todo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.todo.model.Task
import com.example.todo.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(app: Application, private val taskRepository: TaskRepository):AndroidViewModel(app){

    private val _totalTaskCount = MutableLiveData<Int>()
    val totalTaskCount: LiveData<Int> = _totalTaskCount

    private val _completedTaskCount = MutableLiveData<Int>()
    val completedTaskCount: LiveData<Int> = _completedTaskCount

    fun addTask(task: Task) =
        viewModelScope.launch {
            taskRepository.insertTask(task)
        }

    fun deleteNote(task: Task) =
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }

    fun updateTask(task: Task) =
        viewModelScope.launch {
            taskRepository.updateTask (task)
        }

    fun updateOnTask(taskId: Int, title: String, description: String, date: Long, time: Long) =
        viewModelScope.launch {
            taskRepository.updateOnTask(taskId, title, description, date, time)
        }

    fun updateCompleted(taskId: Int, completed: Boolean) =
        viewModelScope.launch {
            taskRepository.updateCompleted(taskId,completed)
        }

    // Function to retrieve total task count
    fun getTaskCount() {
        viewModelScope.launch {
            val totalCount = taskRepository.getTaskCount()
            _totalTaskCount.postValue(totalCount)
        }
    }

    // Function to retrieve completed task count
    fun getCompletedTasks() {
        viewModelScope.launch {
            val completedCount = taskRepository.getCompletedTasks()
            _completedTaskCount.postValue(completedCount)
        }
    }


    fun getAllTasks() = taskRepository.getAllTasks()

    fun searchTask(query: String?) = taskRepository.searchTask(query)


}