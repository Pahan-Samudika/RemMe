package com.example.todo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo.databinding.TaskLayoutBinding
import com.example.todo.fragments.HomeFragmentDirections
import com.example.todo.model.Task
import com.example.todo.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class TaskAdapter (private val taskViewModel: TaskViewModel) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>(){
    class TaskViewHolder (val itemBinding: TaskLayoutBinding): RecyclerView.ViewHolder(itemBinding.root)

    private val differCallBack = object : DiffUtil.ItemCallback<Task>(){
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id &&
                    oldItem.title == newItem.title &&
                    oldItem.description == newItem.description
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            TaskLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = differ.currentList[position]
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentTask.created)


        holder.itemBinding.apply {
            TaskTitle.text = currentTask.title
            TaskDesc.text = currentTask.description
            TaskCompleted.isEnabled = false
            TaskCompleted.isChecked = currentTask.completed
            TaskDate.text = formattedDate

            // Change background color based on completion status
            if (currentTask.completed) {
                root.setBackgroundResource(R.drawable.completed_task_background)
            } else {
                root.setBackgroundResource(R.drawable.task_background)
            }

            // Handle item click here
            root.setOnClickListener {
                val direction = HomeFragmentDirections.actionHomeFragmentToEditItemFragment(currentTask)
                it.findNavController().navigate(direction)
            }
        }
    }

}