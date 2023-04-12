package com.tldrandroid.todogpt.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.tldrandroid.todogpt.data.Task
import com.tldrandroid.todogpt.models.TaskViewModel
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@Composable
fun TaskListScreen(taskViewModel: TaskViewModel) {
    val tasks by taskViewModel.tasks.observeAsState(emptyList())

    Scaffold(
        topBar = { TaskListTopBar() },
        floatingActionButton = { TaskListFAB(taskViewModel) }
    ) { padding ->
        Box(
            modifier = Modifier.padding(padding)
        ) {
            TaskListContent(tasks, taskViewModel)
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun TaskListTopBar() {
    TopAppBar(
        title = { Text("Todo List") }
    )
}

@ExperimentalMaterial3Api
@Composable
fun TaskListFAB(taskViewModel: TaskViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var newTaskName by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add Task") },
            text = {
                TextField(
                    value = newTaskName,
                    onValueChange = { newTaskName = it },
                    label = { Text("Task Name") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTaskName.isNotBlank()) {
                            taskViewModel.viewModelScope.launch {
                                taskViewModel.insert(Task(name = newTaskName.trim()))
                            }
                            newTaskName = ""
                            showDialog = false
                        }
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    FloatingActionButton(
        onClick = { showDialog = true },
        content = { Icon(Icons.Filled.Add, contentDescription = "Add Task") }
    )
}


@ExperimentalFoundationApi
@Composable
fun TaskListContent(tasks: List<Task>, taskViewModel: TaskViewModel) {
    if (tasks.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No Tasks")
        }
    } else {
        LazyColumn {
            items(tasks.size) { index ->
                var showDeleteDialog by remember { mutableStateOf(false) }

                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        title = { Text("Delete Task") },
                        text = { Text("Are you sure you want to delete this task?") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    taskViewModel.viewModelScope.launch {
                                        taskViewModel.delete(tasks[index])
                                    }
                                    showDeleteDialog = false
                                }
                            ) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showDeleteDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }

                TaskRow(tasks[index], taskViewModel, { showDeleteDialog = true })
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun TaskRow(task: Task, taskViewModel: TaskViewModel, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = {
                    taskViewModel.viewModelScope.launch {
                        taskViewModel.update(task.copy(isCompleted = !task.isCompleted))
                    }
                },
                onLongClick = onDelete
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { isChecked ->
                taskViewModel.viewModelScope.launch {
                    taskViewModel.update(task.copy(isCompleted = isChecked))
                }
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = task.name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall.copy(
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
            )
        )
    }
}
