package com.carin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.carin.R

class CustomAdapter(private val context: Context, private val data: Array<String>) : BaseAdapter() {

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_route, parent, false)
            viewHolder = ViewHolder(
                view.findViewById(R.id.originTextView),
                view.findViewById(R.id.destinationTextView),
                view.findViewById(R.id.driverNameTextView),
                view.findViewById(R.id.hoursTextView),
                view.findViewById(R.id.departureDateTextView),
                view.findViewById(R.id.vehicleTextView),
                view.findViewById(R.id.kmTextView)
            )
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val dataArray = data[position].split(", ")

        viewHolder.originTextView.text = dataArray[0]
        viewHolder.destinationTextView.text = dataArray[1]
        viewHolder.driverNameTextView.text = dataArray[2]
        viewHolder.hoursTextView.text = dataArray[3]
        viewHolder.departureDateTextView.text = dataArray[4]
        viewHolder.vehicleMakeTextView.text = dataArray[5]
        viewHolder.kmTextView.text = dataArray[6]


        return view
    }

    private class ViewHolder(
        val originTextView: TextView,
        val destinationTextView: TextView,
        val driverNameTextView: TextView,
        val hoursTextView: TextView,
        val departureDateTextView: TextView,
        val vehicleMakeTextView: TextView,
        val kmTextView: TextView
    )
}
