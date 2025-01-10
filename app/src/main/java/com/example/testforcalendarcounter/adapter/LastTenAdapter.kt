package com.example.testforcalendarcounter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testforcalendarcounter.data.CigaretteEntry
import com.example.testforcalendarcounter.databinding.ItemCigaretteEntryBinding
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class LastTenAdapter(
    private val onDeleteClicked: (CigaretteEntry) -> Unit
) : RecyclerView.Adapter<LastTenAdapter.ViewHolder>() {

    private var data = listOf<CigaretteEntry>()

    fun setData(newData: List<CigaretteEntry>) {
        data = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCigaretteEntryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onDeleteClicked)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    class ViewHolder(
        private val binding: ItemCigaretteEntryBinding,
        private val onDeleteClicked: (CigaretteEntry) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: CigaretteEntry) {
            // Convert epoch millis to LocalDateTime:
            val instant = Instant.fromEpochMilliseconds(entry.timestamp)
            val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

            // Format date/time for display
            val datePart = dateTime.date  // e.g., 2024-10-06
            val timePart = dateTime.time  // e.g., 13:12:45.123
            val formatted = "Date: $datePart\nTime: ${timePart.hour}:${timePart.minute}:${timePart.second}"

            binding.timestampTextView.text = formatted

            // Handle delete
            binding.deleteButton.setOnClickListener {
                onDeleteClicked(entry)
            }
        }
    }
}