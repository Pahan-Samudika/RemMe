package com.example.todo.fragments

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
import androidx.navigation.fragment.navArgs
import com.example.todo.MainActivity
import com.example.todo.R
import com.example.todo.databinding.FragmentEditItemBinding
import com.example.todo.model.Task
import com.example.todo.viewmodel.TaskViewModel

class EditItemFragment : Fragment(R.layout.fragment_edit_item), MenuProvider {

    private var editItemBinding: FragmentEditItemBinding? = null
    private val binding get() = editItemBinding!!

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var currentTask: Task


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

        binding.editNoteTitle.setText(currentTask.title)
        binding.editNoteDesc.setText(currentTask.description)

        binding.editNoteFab.setOnClickListener{
            val title = binding.editNoteTitle.text.toString().trim()
            val description = binding.editNoteDesc.text.toString().trim()

            if (title.isNotEmpty()){
                val task = Task(currentTask.id, title, description)
                taskViewModel.updateTask(task)
                view.findNavController().popBackStack(R.id.homeFragment, false)
            }else{
                Toast.makeText(context,"Title cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun deleteTask(){
        AlertDialog.Builder(activity).apply{
            setTitle("Delete Task")
            setMessage("Are you sure you want to delete this task?")
            setPositiveButton("Yes"){_, _ ->
                taskViewModel.deleteNote(currentTask)
                Toast.makeText(context, "Task deleted successf ully", Toast.LENGTH_SHORT).show()
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

