package com.btech.recreate.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.btech.recreate.R
import com.btech.recreate.network.BackendApi
import org.json.JSONArray
import org.json.JSONObject

class AIReuseSuggestionsActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_reuse_suggestions)

        title = "AI Reuse Suggestions"

        progressBar = findViewById(R.id.progressBar)

        val analysisJson = intent.getStringExtra("analysis_json")

        if (analysisJson.isNullOrEmpty()) {
            Toast.makeText(this, "No analysis data found", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        Toast.makeText(this, "Generating ideas...", Toast.LENGTH_SHORT).show()
        progressBar.visibility = View.VISIBLE

        BackendApi.generateAIReuseIdeas(analysisJson) { result ->

            runOnUiThread {

                progressBar.visibility = View.GONE

                if (result == null) {
                    Toast.makeText(
                        this,
                        "Network error. Check connection.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@runOnUiThread
                }

                try {

                    println("FULL AI RESPONSE: $result")

                    val json = JSONObject(result)

                    val easy = json.optJSONArray("easy") ?: JSONArray()
                    val moderate = json.optJSONArray("moderate") ?: JSONArray()
                    val advanced = json.optJSONArray("advanced") ?: JSONArray()

                    if (easy.length() == 0 &&
                        moderate.length() == 0 &&
                        advanced.length() == 0
                    ) {
                        Toast.makeText(
                            this,
                            "No ideas generated",
                            Toast.LENGTH_LONG
                        ).show()
                        return@runOnUiThread
                    }

                    setupList(R.id.listEasy, easy, analysisJson)
                    setupList(R.id.listModerate, moderate, analysisJson)
                    setupList(R.id.listAdvanced, advanced, analysisJson)

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        this,
                        "Parsing error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun setupList(
        listId: Int,
        array: JSONArray,
        analysisJson: String
    ) {

        val listView = findViewById<ListView>(listId)

        val displayItems = mutableListOf<String>()

        for (i in 0 until array.length()) {

            val obj = array.getJSONObject(i)

            val title = obj.optString("title", "")
            val description = obj.optString("short_description", "")

            displayItems.add("$title\n\n$description")
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            displayItems
        )

        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->

            val selectedObj = array.getJSONObject(position)
            val ideaTitle = selectedObj.getString("title")

            val intent = Intent(
                this,
                AIReuseDetailActivity::class.java
            )

            intent.putExtra("idea_title", ideaTitle)
            intent.putExtra("analysis_json", analysisJson)

            startActivity(intent)
        }
    }
}