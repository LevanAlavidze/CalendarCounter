package com.example.testforcalendarcounter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testforcalendarcounter.data.health.HealthAchievement
import com.example.testforcalendarcounter.databinding.ItemHealthAchievementBinding

class HealthAchievementsAdapter(
    private val achievements: List<HealthAchievement>
): RecyclerView.Adapter<HealthAchievementsAdapter.ViewHolder>(){

    class ViewHolder(val binding: ItemHealthAchievementBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
       val binding = ItemHealthAchievementBinding.inflate(
           LayoutInflater.from(parent.context), parent,false
       )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val achievement = achievements[position]
        with(holder.binding){
            tvAchievementText.text = "${achievement.title}: ${achievement.description}"

            pbAchievement.progress = achievement.progress

            tvAchievementProgress.text = if (achievement.progress >=100){
                "Achieved"
            }else{
                "${achievement.progress}%"
            }
        }
    }

    override fun getItemCount(): Int = achievements.size

}