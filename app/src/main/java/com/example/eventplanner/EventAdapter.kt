package com.example.eventplanner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormat
import java.util.*

class EventAdapter(
    private var events: List<Event>,
    private val onClick: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventVH>() {

    inner class EventVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle = itemView.findViewById<TextView>(R.id.tvItemTitle)
        private val tvDate  = itemView.findViewById<TextView>(R.id.tvItemDateTime)

        fun bind(event: Event) {
            tvTitle.text = event.title
            tvDate.text  = DateFormat.getDateTimeInstance().format(Date(event.dateTime))
            itemView.setOnClickListener { onClick(event) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_item, parent, false)
        return EventVH(view)
    }

    override fun getItemCount(): Int = events.size

    override fun onBindViewHolder(holder: EventVH, position: Int) {
        holder.bind(events[position])
    }

    fun update(newList: List<Event>) {
        events = newList
        notifyDataSetChanged()
    }
}
