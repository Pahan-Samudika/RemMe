package com.example.todo.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todo.MainActivity
import com.example.todo.R
import com.example.todo.databinding.FragmentEditItemBinding
import com.example.todo.model.Task
import com.example.todo.viewmodel.TaskViewModel
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

class EditItemFragment : Fragment(R.layout.fragment_edit_item), MenuProvider {

    // Define variables to hold selected date and time
    private var selectedDate: Calendar = Calendar.getInstance()
    private var selectedTime: Calendar = Calendar.getInstance()

    private var editItemBinding: FragmentEditItemBinding? = null
    private val binding get() = editItemBinding!!

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var currentTask: Task

    private val args:EditItemFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        editItemBinding = FragmentEditItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        taskViewModel = (activity as MainActivity).taskViewModel
        currentTask = args.task!!

        val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
        val formattedDate = dateFormat.format(currentTask.created)

        val calendarDate = Calendar.getInstance().apply { timeInMillis = currentTask.date }
        val calendarTime = Calendar.getInstance().apply { timeInMillis = currentTask.time }


        binding.editNoteTitle.setText(currentTask.title)
        binding.editNoteDesc.setText(currentTask.description)
        binding.createdAt.text = "Created on: $formattedDate"
        // Set the date and time in the DatePicker and TimePicker
        binding.editTextDate.init(
            calendarDate.get(Calendar.YEAR),
            calendarDate.get(Calendar.MONTH),
            calendarDate.get(Calendar.DAY_OF_MONTH)
        ) { _, year, monthOfYear, dayOfMonth ->
            // Handle date change if necessary
        }

        // Set the time in the TimePicker
        binding.editTextTime.hour = calendarTime.get(Calendar.HOUR_OF_DAY)
        binding.editTextTime.minute = calendarTime.get(Calendar.MINUTE)

        binding.editNoteFab.setOnClickListener{
                val title = binding.editNoteTitle.text.toString().trim()
                val description = binding.editNoteDesc.text.toString().trim()
                val date = getDateOnly()
                val time = getTimeOnly()


                if (title.isNotEmpty()){
                    taskViewModel.updateOnTask(currentTask.id, title, description, date, time)
                    Toast.makeText(context, "Task edited successfully", Toast.LENGTH_SHORT).show()
                    view.findNavController().popBackStack(R.id.homeFragment, false)
                } else {
                    Toast.makeText(context,"Title cannot be empty", Toast.LENGTH_SHORT).show()
                }
        }



        binding.TaskCompleted.apply {
            // Set initial state based on completion status
            text = if (currentTask.completed) {
                resources.getString(R.string.mark_as_incomplete)
            } else {
                resources.getString(R.string.mark_as_completed)
            }

            // Set initial background color based on completion status
            setBackgroundColor(if (currentTask.completed) {
                ContextCompat.getColor(requireContext(), R.color.cinnabar)
            } else {
                ContextCompat.getColor(requireContext(), R.color.greenGrey)
            })

            // Set click listener to toggle completion status
            setOnClickListener {
                val newCompletionStatus = !currentTask.completed
                text = if (newCompletionStatus) {
                    resources.getString(R.string.mark_as_incomplete)
                } else {
                    resources.getString(R.string.mark_as_completed)
                }
                setBackgroundColor(if (newCompletionStatus) {
                    ContextCompat.getColor(requireContext(), R.color.cinnabar)
                } else {
                    ContextCompat.getColor(requireContext(), R.color.greenGrey)
                })
                // Update the completion status of the task
                taskViewModel.updateCompleted(currentTask.id, newCompletionStatus)
            }

        }


        binding.cancelFab.setOnClickListener {
            view.findNavController().popBackStack(R.id.homeFragment, false)
        }

        binding.deleteFab.setOnClickListener {
            deleteTask()
        }

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

    private fun deleteTask(){
        AlertDialog.Builder(activity).apply{
            setTitle("Delete Task")
            setMessage("Are you sure you want to delete this task?")
            setPositiveButton("Yes"){_, _ ->
                taskViewModel.deleteNote(currentTask)
                Toast.makeText(context, "Task deleted successfully", Toast.LENGTH_SHORT).show()
                view?.findNavController()?.popBackStack(R.id.homeFragment, false)
            }
            setNegativeButton("No",null)
        }.create().show()
        }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_edit_item, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId){
            R.id.deleteMenu -> {
                deleteTask()
                true
            }
            else -> false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        editItemBinding = null
    }
}


