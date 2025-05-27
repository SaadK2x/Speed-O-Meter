package gps.navigation.speedmeter.adapters

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import gps.navigation.speedmeter.R
import gps.navigation.speedmeter.activities.HistoryDetail
import gps.navigation.speedmeter.database.HistoryWithRoutePoints
import gps.navigation.speedmeter.utils.OnItemDelete

class HistoryAdapter(val dataSet: List<HistoryWithRoutePoints>, var onItemDelete: OnItemDelete) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {


    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val viewHolder =
            LayoutInflater.from(parent.context).inflate(R.layout.history_adapter, parent, false)
        return HistoryViewHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = dataSet[position].history
        val destination = holder.itemView.findViewById<TextView>(R.id.destination)
        val duration = holder.itemView.findViewById<TextView>(R.id.duration)
        val distance = holder.itemView.findViewById<TextView>(R.id.distance)
        val mainLayout = holder.itemView.findViewById<FrameLayout>(R.id.mainLayout)
        val innerLayout = holder.itemView.findViewById<FrameLayout>(R.id.innerLayout)
        destination.text = history.destination
        duration.text = history.time
        distance.text = history.distance.toString() + "km/h"
        mainLayout.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("pointID", history.pointsID)
            val intent = Intent(holder.itemView.context, HistoryDetail::class.java)
            intent.putExtras(bundle)
            holder.itemView.context.startActivity(intent)
        }
        innerLayout.setOnClickListener {
            onItemDelete.onItemClick(history.pointsID, position)
        }


    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}