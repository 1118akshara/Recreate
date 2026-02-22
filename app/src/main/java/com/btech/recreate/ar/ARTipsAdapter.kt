package com.btech.recreate.ar

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.btech.recreate.R
import com.btech.recreate.activities.UserGuidelinesActivity

class ARTipsAdapter(
    private val tips: List<ARTip>
) : RecyclerView.Adapter<ARTipsAdapter.TipViewHolder>() {

    class TipViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: TextView = view.findViewById(R.id.tipIcon)
        val title: TextView = view.findViewById(R.id.tipTitle)
        val description: TextView = view.findViewById(R.id.tipDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ar_tip, parent, false)
        return TipViewHolder(view)
    }

    override fun onBindViewHolder(holder: TipViewHolder, position: Int) {

        val tip = tips[position]

        holder.icon.text = tip.icon
        holder.title.text = tip.title
        holder.description.text = tip.description

        if (tip.isNavigationCard) {
            holder.itemView.setOnClickListener {
                val context = holder.itemView.context
                context.startActivity(
                    Intent(context, UserGuidelinesActivity::class.java)
                )
            }
        }
    }

    override fun getItemCount(): Int = tips.size
}

data class ARTip(
    val icon: String,
    val title: String,
    val description: String,
    val isNavigationCard: Boolean = false
)