package com.btech.recreate.activities

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
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
                    txtSteps.text = "Network error while loading instructions"
                    return@runOnUiThread
                }

                try {

                    println("STEP FULL RESPONSE: $result")

                    val json = JSONObject(result)

                    if (!json.optBoolean("success", false)) {
                        txtSteps.text = "Step generation failed"
                        return@runOnUiThread
                    }

                    val stepsData = json.opt("steps")

                    var materialsArray = JSONArray()
                    var stepsArray = JSONArray()

                    // ✅ Handle both possible formats safely
                    if (stepsData is JSONObject) {

                        materialsArray = stepsData.optJSONArray("required_materials") ?: JSONArray()
                        stepsArray = stepsData.optJSONArray("steps") ?: JSONArray()

                    } else if (stepsData is JSONArray) {

                        // fallback if backend accidentally returns list
                        stepsArray = stepsData
                    }

                    val builder = StringBuilder()

                    if (materialsArray.length() > 0) {

                        builder.append("Required Materials:\n\n")

                        for (i in 0 until materialsArray.length()) {
                            builder.append("• ")
                            builder.append(materialsArray.get(i).toString())
                            builder.append("\n")
                        }

                        builder.append("\n")
                    }

                    builder.append("Step-by-Step Instructions:\n\n")

                    for (i in 0 until stepsArray.length()) {

                        val stepObj = stepsArray.optJSONObject(i)

                        if (stepObj != null) {
                            builder.append("Step ${stepObj.optInt("step_number", i + 1)}:\n")
                            builder.append(stepObj.optString("instruction", ""))
                            builder.append("\n\n")
                        } else {
                            builder.append("Step ${i + 1}:\n")
                            builder.append(stepsArray.get(i).toString())
                            builder.append("\n\n")
                        }
                    }

                    if (stepsArray.length() == 0) {
                        builder.append("No instructions were generated.")
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