package com.btech.recreate.activities

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.btech.recreate.R
import java.text.SimpleDateFormat
import java.util.*

class SavedProjectsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_projects)

        val container = findViewById<LinearLayout>(R.id.projectsContainer)

        // 🔥 Temporary Sample Data
        val savedProjects = getSampleProjects()

        if (savedProjects.isEmpty()) {
            showEmptyMessage(container)
        } else {
            displayProjects(container, savedProjects)
        }
    }

    private fun getSampleProjects(): List<Project> {
        return listOf(
            Project("Plastic Bottle Planter", Date()),
            Project("Wooden Box Storage", Date()),
            Project("Glass Jar Lamp", Date())
        )
    }

    private fun displayProjects(container: LinearLayout, projects: List<Project>) {

        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        projects.forEachIndexed { index, project ->

            val card = LinearLayout(this)
            card.orientation = LinearLayout.VERTICAL
            card.setPadding(30, 30, 30, 30)
            card.setBackgroundColor(0xFFFFFFFF.toInt())

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 30)
            card.layoutParams = params

            val title = TextView(this)
            title.text = project.title
            title.textSize = 18f
            title.setTextColor(0xFF1B5E20.toInt())

            val dateText = TextView(this)
            dateText.text = "Saved on: ${dateFormat.format(project.date)}"
            dateText.textSize = 13f
            dateText.setTextColor(0xFF4CAF50.toInt())

            card.addView(title)
            card.addView(dateText)

            container.addView(card)
        }
    }

    private fun showEmptyMessage(container: LinearLayout) {
        val textView = TextView(this)
        textView.text = "No saved projects yet."
        textView.textSize = 16f
        textView.setTextColor(0xFF757575.toInt())
        container.addView(textView)
    }

    data class Project(
        val title: String,
        val date: Date
    )
}