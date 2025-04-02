package com.example.testforcalendarcounter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testforcalendarcounter.data.GameItem
import com.example.testforcalendarcounter.databinding.GameItemBinding

class GameAdapter(
    private val games: List<GameItem>,
    private val onGameClicked: (GameItem) -> Unit
) : RecyclerView.Adapter<GameAdapter.ViewHolder>() {

    inner class ViewHolder(
        private val binding: GameItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(game: GameItem) {
            binding.tvGameTitle.text = game.title
            binding.ivGameIcon.setImageResource(game.iconResId)
            binding.root.setOnClickListener { onGameClicked(game) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = GameItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(games[position])
    }

    override fun getItemCount(): Int = games.size


}
