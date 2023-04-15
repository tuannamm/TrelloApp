package eu.tutorials.trelloapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import eu.tutorials.trelloapp.R

class TaskListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
    }
}