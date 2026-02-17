package com.btech.recreate.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.btech.recreate.R
import com.btech.recreate.network.BackendApi
import org.json.JSONObject
import androidx.appcompat.app.AlertDialog
import org.json.JSONArray

class AIReuseSuggestionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_reuse_suggestions)

        title = "Reuse Suggestions"

        val analysisJson = intent.getStringExtra("analysis_json")

        if (analysisJson == null) {
            showError("No analysis data found")
            return
        }

        BackendApi.generateAIReuseIdeas(analysisJson) { result ->

            runOnUiThread {

                if (result == null) {
                    showError("Failed to get AI suggestions")
                    return@runOnUiThread
                }

                try {
                    val json = JSONObject(result)

                    val easy = json.getJSONArray("easy")
                    val moderate = json.getJSONArray("moderate")
                    val hard = json.getJSONArray("very_hard")

                    setupList(R.id.listEasy, easy)
                    setupList(R.id.listModerate, moderate)
                    setupList(R.id.listHard, hard)

                } catch (e: Exception) {
                    showError("Invalid AI response")
                }
            }
        }
    }

    private fun setupList(listId: Int, array: org.json.JSONArray) {

        val listView = findViewById<ListView>(listId)

        val titles = mutableListOf<String>()

        for (i in 0 until array.length()) {
            titles.add(array.getJSONObject(i).getString("title"))
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            titles
        )

        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->

            val intent = Intent(
                this,
                AIReuseDetailActivity::class.java
            )

            intent.putExtra("idea_title", titles[position])
            startActivity(intent)
        }
    }

    private fun showError(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}
