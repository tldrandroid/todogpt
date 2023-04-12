package com.tldrandroid.todogpt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.tldrandroid.todogpt.models.TaskViewModel
import com.tldrandroid.todogpt.ui.TaskListScreen
import com.tldrandroid.todogpt.ui.theme.ToDoGPTTheme

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ToDoGPTTheme {
                TaskListScreen(taskViewModel)
            }
        }
    }
}

@Composable
fun TaskListApp(content: @Composable () -> Unit) {
    MaterialTheme {
        content()
    }
}
