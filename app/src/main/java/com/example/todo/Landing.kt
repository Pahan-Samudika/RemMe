package com.example.todo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.todo.viewmodel.TaskViewModel
import androidx.lifecycle.Observer
import com.example.todo.repository.TaskRepository
import com.example.todo.viewmodel.TaskViewModelFactory
import com.example.todo.database.TaskDatabase


class Landing : AppCompatActivity() {

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var totalCountTextView: TextView
    private lateinit var completedCountTextView: TextView
    private lateinit var seeTasks: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        totalCountTextView = findViewById(R.id.totalCount)
        completedCountTextView = findViewById(R.id.completedCount)

        val db = TaskDatabase(applicationContext)

        val taskRepository = TaskRepository(db) // Pass the database instance to TaskRepository constructor
        val taskViewModelFactory = TaskViewModelFactory(application, taskRepository)
        taskViewModel = ViewModelProvider(this, taskViewModelFactory).get(TaskViewModel::class.java)

        taskViewModel.getTaskCount()
        taskViewModel.getCompletedTasks()

        observeTaskCounts()

        seeTasks = findViewById(R.id.seeTasks)

        seeTasks.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

    }

    private fun observeTaskCounts() {
        taskViewModel.totalTaskCount.observe(this, Observer { totalCount ->
            totalCountTextView.text = totalCount.toString()
        })

        taskViewModel.completedTaskCount.observe(this, Observer { completedCount ->
            completedCountTextView.text = completedCount.toString()
        })
    }
}
