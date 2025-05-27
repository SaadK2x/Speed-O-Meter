package gps.navigation.speedmeter.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import gps.navigation.speedmeter.R
import gps.navigation.speedmeter.models.LanguageModel
import gps.navigation.speedmeter.utils.OnLanguageClick

class LanguageAdapter(val list: List<LanguageModel>, var onItemDelete: OnLanguageClick) :
    RecyclerView.Adapter<LanguageAdapter.HistoryViewHolder>() {


    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val viewHolder =
            LayoutInflater.from(parent.context).inflate(R.layout.lang_layout, parent, false)
        return HistoryViewHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val cName = holder.itemView.findViewById<TextView>(R.id.cName)
        val langCheck = holder.itemView.findViewById<ImageView>(R.id.langCheck)
        val flagImg = holder.itemView.findViewById<ImageView>(R.id.flagImg)
        val langCard = holder.itemView.findViewById<LinearLayout>(R.id.langCard)

        cName.text = list[position].cName
        flagImg.setImageResource(list[position].cFlag)
        if (list[position].isChecked) {
            langCheck.visibility = View.VISIBLE
            langCard.setBackgroundResource(R.drawable.selected_lang)
        } else {
            langCheck.visibility = View.INVISIBLE
            langCard.setBackgroundResource(R.drawable.unselected_lang)
        }

        holder.itemView.setOnClickListener {
            onItemDelete.onItemClick(list[position].cCode, list[position].cName,position)
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }
}