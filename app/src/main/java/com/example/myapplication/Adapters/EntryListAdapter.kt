package com.example.myapplication.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Model.Entry
import com.example.myapplication.R

class EntryListAdapter : ListAdapter<Entry, EntryListAdapter.EntryViewHolder>(EntriessComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        return EntryViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class EntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val entryItemView: TextView = itemView.findViewById(R.id.textView)

        fun bind(entry: Entry?) {
            entryItemView.text = entry.toString()
        }

        companion object {
            fun create(parent: ViewGroup): EntryViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)
                return EntryViewHolder(view)
            }
        }
    }

    class EntriessComparator : DiffUtil.ItemCallback<Entry>() {
        override fun areItemsTheSame(oldItem: Entry, newItem: Entry): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Entry, newItem: Entry): Boolean {
            return oldItem.date == newItem.date
        }
    }
}