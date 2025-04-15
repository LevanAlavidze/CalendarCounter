package com.example.testforcalendarcounter.settings.settings_colors

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.testforcalendarcounter.MainActivity
import com.example.testforcalendarcounter.databinding.FragmentColorsBinding
import com.example.testforcalendarcounter.R

class ColorsFragment : Fragment() {

    private var _binding: FragmentColorsBinding? = null
    private val binding get() = _binding!!

    private var selectedType: String = "solid"  // "solid", "gradient", or "default"
    private var selectedValue: String? = null   // For a color hex (#FF0000) or gradient name ("gradient_bg_1")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentColorsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Arrays from resources
        val solidColors = resources.getStringArray(R.array.solid_background_colors)
        val gradients = resources.getStringArray(R.array.gradient_background_drawables)

        // Set up solid colors grid
        val solidAdapter = ColorGridAdapter(requireContext(), solidColors.toList())
        binding.gridViewSolid.adapter = solidAdapter
        binding.gridViewSolid.setOnItemClickListener { _, _, position, _ ->
            selectedType = "solid"
            selectedValue = solidColors[position]
            Toast.makeText(requireContext(), "Selected solid: ${solidColors[position]}", Toast.LENGTH_SHORT).show()
            binding.checkboxSystemDefault.isChecked = false
        }

        // Set up gradients grid
        val gradientAdapter = ColorGridAdapter(requireContext(), gradients.toList())
        binding.gridViewGradient.adapter = gradientAdapter
        binding.gridViewGradient.setOnItemClickListener { _, _, position, _ ->
            selectedType = "gradient"
            selectedValue = gradients[position]
            Toast.makeText(requireContext(), "Selected gradient: ${gradients[position]}", Toast.LENGTH_SHORT).show()
            binding.checkboxSystemDefault.isChecked = false
        }

        // "Use system default" checkbox
        binding.checkboxSystemDefault.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedType = "default"
                Toast.makeText(requireContext(), "Using system default background", Toast.LENGTH_SHORT).show()
            }
        }

        // Save button
        binding.btnSaveBackground.setOnClickListener {
            if (selectedType == "default") {
                saveBackgroundChoice("default", "")
            } else if (!selectedValue.isNullOrEmpty()) {
                saveBackgroundChoice(selectedType, selectedValue!!)
            } else {
                Toast.makeText(requireContext(), "Please select a background", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Force the activity to recreate so the new background applies immediately
            requireActivity().recreate()
        }
    }

    private fun saveBackgroundChoice(type: String, value: String) {
        val prefs = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("background_type", type)
            putString("background_value", value)
            apply()
        }
        (activity as? MainActivity)?.applyBackgroundFromPreferences()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
