package com.example.testforcalendarcounter.health

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testforcalendarcounter.R
import com.example.testforcalendarcounter.adapter.HealthAchievementsAdapter
import com.example.testforcalendarcounter.data.health.HealthAchievement
import com.example.testforcalendarcounter.databinding.FragmentHealthBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HealthFragment : Fragment() {

    private var _binding: FragmentHealthBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHealthBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val achievements = listOf(
            HealthAchievement(
                title = "Didn't Smoke Yet",
                description = "Great start! keep going!",
                progress = 100
            ),
            HealthAchievement(
                title = "After 20 minutes",
                description = "Your blood pressure and heart rate decrease.",
                progress = 100 // Suppose user achieved it
            ),
            HealthAchievement(
                title = "After 4 hours",
                description = "Carbon monoxide levels drop in your blood.",
                progress = 60 // Partially completed
            ),
            HealthAchievement(
                title = "After 24 hours",
                description = "Risk of heart attack begins to decrease.",
                progress = 0 // Not yet
            )
        )

        val adapter = HealthAchievementsAdapter(achievements)
        binding.healthRecyclerView.adapter = adapter
        binding.healthRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}