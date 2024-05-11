package com.example.todo.fragments

import android.Manifest
import android.app.AlarmManager
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import com.example.todo.MainActivity
import com.example.todo.R
import com.example.todo.databinding.FragmentAddItemBinding
import com.example.todo.model.Task
import com.example.todo.viewmodel.TaskViewModel
import java.util.Calendar
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.todo.Notification
import com.example.todo.channelID
import com.example.todo.databinding.ActivityMainBinding
import com.example.todo.messageExtra
import com.example.todo.notificationID
import com.example.todo.titleExtra
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AddItemFragment : Fragment(R.layout.fragment_add_item), MenuProvider {

    // Define variables to hold selected date and time
    private var selectedDate: Calendar = Calendar.getInstance()
    private var selectedTime: Calendar = Calendar.getInstance()

    private var addItemBinding: FragmentAddItemBinding? = null
    private val binding get() = addItemBinding!!

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var addTaskView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        addItemBinding = FragmentAddItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        createNotificationChannel()

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        taskViewModel = (activity as MainActivity).taskViewModel
        addTaskView = view

        binding.SaveNoteFab.setOnClickListener{
            saveTask(addTaskView)
            scheduleNotification()
            true
        }

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notif Channel"
            val desc = "Description of the channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelID, name, importance)
            channel.description = desc

            val notificationManager = requireContext().applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun scheduleNotification() {
        val intent = Intent(requireContext(), Notification::class.java)
        val title = binding.addTitle.text.toString()
        val message = binding.addDesc.text.toString()

        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!AlarmManagerCompat.canScheduleExactAlarms(alarmManager)) {
                // Handle case where exact alarms cannot be scheduled
                // You might want to use setExactAndAllowWhileIdle instead
                // or provide alternative handling for the notification.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        time,
                        pendingIntent
                    )
                } else {
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        time,
                        pendingIntent
                    )
                }
                showAlert(time, title, message)
                return
            }
        }

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time,
                pendingIntent
            )
            showAlert(time, title, message)
        } catch (securityException: SecurityException) {
            // Handle security exception gracefully
            Toast.makeText(
                requireContext(),
                "Security exception occurred while scheduling alarm",
                Toast.LENGTH_SHORT
            ).show()
            securityException.printStackTrace()
        }
    }


    private fun showAlert(time: Long, title: Any, message: Any) {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(requireContext())
        val timeFormat = android.text.format.DateFormat.getTimeFormat(requireContext())

        AlertDialog.Builder(requireContext())
            .setTitle("Notification Scheduled!")
            .setMessage(
                "Title : $title\n" +
                        "Message : $message\n" +
                        "At : ${dateFormat.format(date)} ${timeFormat.format(date)}"
            )
            .setPositiveButton("Okay") { _, _ -> }
            .show()
    }

    private fun getTimeOnly(): Long {
        val minute = binding.editTextTime.minute
        val hour = binding.editTextTime.hour

        val calendar = Calendar.getInstance()
        // Set other fields to current values to avoid exceptions
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
        // Set time
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)

        return calendar.timeInMillis
    }



    private fun getDateOnly(): Long {
        val day = binding.editTextDate.dayOfMonth
        val month = binding.editTextDate.month
        val year = binding.editTextDate.year

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)

        return calendar.timeInMillis
    }


    private fun getTime(): Long {
        val minute = binding.editTextTime.minute
        val hour = binding.editTextTime.hour

        val day = binding.editTextDate.dayOfMonth
        val month = binding.editTextDate.month
        val year = binding.editTextDate.year

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute)

        return calendar.timeInMillis
    }

    private fun saveTask(view: View){
        val title = binding.addTitle.text.toString().trim()
        val description = binding.addDesc.text.toString().trim()
        val date = getDateOnly()
        val time = getTimeOnly()

        if (title.isNotEmpty()){
            val task = Task(0, title, description, false, System.currentTimeMillis(), date, time)
            taskViewModel.addTask(task)

            Toast.makeText(addTaskView.context, "Task added successfully", Toast.LENGTH_SHORT).show()
            view.findNavController().popBackStack(R.id.homeFragment, false)
        }else{
            Toast.makeText(addTaskView.context, "Please enter a title", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_add_item, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId){
            R.id.saveMenu -> {
                saveTask(addTaskView)
                true
            }
            else -> false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        addItemBinding = null
    }
}