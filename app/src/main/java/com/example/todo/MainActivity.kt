package com.example.todo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.todo.database.TaskDatabase
import com.example.todo.repository.TaskRepository
import com.example.todo.viewmodel.TaskViewModel
import com.example.todo.viewmodel.TaskViewModelFactory

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 100
    private val permissionsToRequest = mutableListOf<String>()

    lateinit var taskViewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Check if permissions were previously granted
        if (!arePermissionsGranted()) {
            checkPermissions()
        }

        setupViewModel()
    }

    // Check if the app has permissions, if not, request them
    private fun checkPermissions() {
        permissionsToRequest.clear()

        // Check if SCHEDULE_EXACT_ALARM permission is granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SCHEDULE_EXACT_ALARM
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.SCHEDULE_EXACT_ALARM)
        }

        // Check if POST_NOTIFICATIONS permission is granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        // If there are permissions to request, request them
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PERMISSIONS_REQUEST_CODE
            )
        }
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            // Check if all permissions were granted
            val allPermissionsGranted = permissionsToRequest.all { permission ->
                grantResults.any { it == PackageManager.PERMISSION_GRANTED }
            }
            if (allPermissionsGranted) {
                // Permissions granted, save the status
                savePermissionsStatus(true)
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                // Permissions not granted, save the status
                savePermissionsStatus(false)
                Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to save the permissions status
    private fun savePermissionsStatus(granted: Boolean) {
        val sharedPreferences = getSharedPreferences("permissions", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("permissions_granted", granted)
            apply()
        }
    }

    // Function to check if permissions were previously granted
    private fun arePermissionsGranted(): Boolean {
        val sharedPreferences = getSharedPreferences("permissions", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("permissions_granted", false)
    }

    private fun setupViewModel() {
        val taskRepository = TaskRepository(TaskDatabase(this))
        val viewModelProviderFactory = TaskViewModelFactory(application, taskRepository)
        taskViewModel = ViewModelProvider(this, viewModelProviderFactory)[TaskViewModel::class.java]
    }
}