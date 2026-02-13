package com.btech.recreate.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.btech.recreate.R
import com.btech.recreate.network.BackendApi
import org.json.JSONArray

class AIReuseDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_reuse_detail)

        val idea = intent.getStringExtra("idea_title")

        findViewById<TextView>(R.id.txtIdeaTitle).text = idea

        if (idea == null) return

        BackendApi.generateSteps(idea) { result ->

            runOnUiThread {

                if (result == null) {
                    findViewById<TextView>(R.id.txtSteps).text =
                        "Failed to load steps"
                    return@runOnUiThread
                }

                try {
                    val array = JSONArray(result)

                    val builder = StringBuilder()

                    for (i in 0 until array.length()) {
                        val step = array.getJSONObject(i)

                        builder.append("Step ${step.getInt("step_number")}\n")
                        builder.append(step.getString("instruction"))
                        builder.append("\n\n")
                    }

                    findViewById<TextView>(R.id.txtSteps).text =
                        builder.toString()

                } catch (e: Exception) {
                    findViewById<TextView>(R.id.txtSteps).text =
                        "Invalid step response"
                }
            }
        }
    }
}
