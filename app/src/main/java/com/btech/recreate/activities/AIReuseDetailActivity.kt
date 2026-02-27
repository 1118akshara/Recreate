package com.btech.recreate.activities

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.btech.recreate.R
import com.btech.recreate.network.BackendApi
import org.json.JSONArray
import org.json.JSONObject

class AIReuseDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_reuse_detail)

        val ideaTitle = intent.getStringExtra("idea_title")
        val analysisJson = intent.getStringExtra("analysis_json")

        val txtTitle = findViewById<TextView>(R.id.txtIdeaTitle)
        val txtSteps = findViewById<TextView>(R.id.txtSteps)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        if (ideaTitle.isNullOrEmpty() || analysisJson.isNullOrEmpty()) {
            txtSteps.text = "Missing data"
            return
        }

        txtTitle.text = ideaTitle
        progressBar.visibility = View.VISIBLE

        BackendApi.generateSteps(ideaTitle, analysisJson) { result ->

            runOnUiThread {

                progressBar.visibility = View.GONE

                if (result == null) {
                    txtSteps.text = "Failed to load instructions"
                    return@runOnUiThread
                }

                try {

                    val json = JSONObject(result)

                    val stepsObject = json.optJSONObject("steps")

                    if (stepsObject == null) {
                        txtSteps.text = "No instructions generated"
                        return@runOnUiThread
                    }

                    val materials = stepsObject.optJSONArray("required_materials") ?: JSONArray()
                    val stepsArray = stepsObject.optJSONArray("steps") ?: JSONArray()

                    val builder = StringBuilder()

                    if (materials.length() > 0) {
                        builder.append("Required Materials:\n\n")
                        for (i in 0 until materials.length()) {
                            builder.append("• ${materials.getString(i)}\n")
                        }
                        builder.append("\n")
                    }

                    builder.append("Step-by-Step Instructions:\n\n")

                    for (i in 0 until stepsArray.length()) {
                        val step = stepsArray.getJSONObject(i)

                        builder.append("Step ${step.optInt("step_number", i + 1)}:\n")
                        builder.append("${step.optString("instruction", "")}\n\n")
                    }

                    if (stepsArray.length() == 0) {
                        builder.append("Instructions could not be generated.")
                    }

                    txtSteps.text = builder.toString()

                } catch (e: Exception) {
                    e.printStackTrace()
                    txtSteps.text = "Parsing error: ${e.message}"
                }
            }
        }
    }
}