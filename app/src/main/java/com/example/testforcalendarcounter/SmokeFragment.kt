package com.example.testforcalendarcounter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.testforcalendarcounter.databinding.FragmentSmokeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SmokeFragment : Fragment() {

    private var _binding: FragmentSmokeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SmokeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSmokeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //observers for cigarette count
        viewModel.dayCigaretteCount.observe(viewLifecycleOwner) { count ->
            binding.dailyCountTextView.text = "Daily: $count"
        }

        viewModel.weekCigaretteCount.observe(viewLifecycleOwner) { count ->
            binding.weeklyCountTextView.text = "Weekly: $count"
        }

        viewModel.monthCigaretteCount.observe(viewLifecycleOwner) { count ->
            binding.monthlyCountTextView.text = "Monthly: $count"
        }

        binding.addCigaretteButton.setOnClickListener {
            viewModel.addCigarette()
        }


    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshCounts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}