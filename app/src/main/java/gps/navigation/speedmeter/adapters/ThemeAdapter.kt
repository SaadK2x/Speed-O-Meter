package gps.navigation.speedmeter.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import gps.navigation.speedmeter.sharedprefrences.SharedPreferenceHelperClass
import gps.navigation.speedmeter.R
import gps.navigation.speedmeter.models.ThemeModel

class ThemeAdapter(private var listTheme: ArrayList<ThemeModel>, var color: ColorCallback) :
    RecyclerView.Adapter<ThemeAdapter.ThemeViewHolder>() {


    class ThemeViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeViewHolder {
        val viewHolder =
            LayoutInflater.from(parent.context).inflate(R.layout.colors_layout, parent, false)
        return ThemeViewHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: ThemeViewHolder, position: Int) {
        val inner = holder.itemView.findViewById<CardView>(R.id.innerColor)
        val outer = holder.itemView.findViewById<CardView>(R.id.outerColor)

        inner.setCardBackgroundColor(Color.parseColor(listTheme[position].color))
        if (listTheme[position].isSelected) {
            outer.setCardBackgroundColor(Color.WHITE)
        } else {
            outer.setCardBackgroundColor(Color.TRANSPARENT)
        }
        holder.itemView.setOnClickListener {
            val sp = SharedPreferenceHelperClass(holder.itemView.context)
            sp.putString("AppColor", listTheme[position].color)
            changeColor(position)
            color.onColorChanger(listTheme[position].color)
        }

    }

    override fun getItemCount(): Int {
        return listTheme.size
    }

    fun changeColor(position: Int) {
        for (i in 0..<listTheme.size) {
            listTheme[i].isSelected = position == i
        }
        notifyDataSetChanged()
    }

    interface ColorCallback {
        fun onColorChanger(color: String)
    }


}