package gps.navigation.speedmeter.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import gps.navigation.speedmeter.R
import gps.navigation.speedmeter.utils.Constants
import gps.navigation.speedmeter.utils.HistoryDisplayItem
import gps.navigation.speedmeter.utils.OnItemDelete

class HistoryOuterAdapter(val dataSet: List<HistoryDisplayItem>, var onItemDelete: OnItemDelete) :
    RecyclerView.Adapter<HistoryOuterAdapter.HistoryViewHolder>() {


    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val viewHolder =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.history_outer_adapter, parent, false)
        return HistoryViewHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val date = holder.itemView.findViewById<TextView>(R.id.date)
        val rv = holder.itemView.findViewById<RecyclerView>(R.id.rv)

        val mAdapter = HistoryAdapter(dataSet[position].history, onItemDelete)
        rv.layoutManager = LinearLayoutManager(holder.itemView.context)
        rv.adapter = mAdapter
        date.text = Constants.getRelativeDateLabel(dataSet[position].date)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}