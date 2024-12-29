package com.example.testforcalendarcounter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.testforcalendarcounter.databinding.DialogPackPriceBinding
import com.example.testforcalendarcounter.databinding.FragmentSettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe pack price changes
        settingsViewModel.packPrice.observe(viewLifecycleOwner) { packPrice ->
            binding.PriceTextView.text = "${packPrice.currency} ${packPrice.price}"
        }

        // Handle Change Pack Price Button click
        binding.changePackPriceButton.setOnClickListener {
            showChangePackPriceDialog()
        }
    }

    private fun showChangePackPriceDialog() {
        val dialogBinding = DialogPackPriceBinding.inflate(layoutInflater)

        // Populate Spinner with currency options
        val currencySpinner: Spinner = dialogBinding.currencySpinner
        val currencies = listOf("USD", "EUR", "GEL")
        currencySpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currencies)

        // Build and show the dialog
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        // Handle Cancel button
        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        // Handle Change button
        dialogBinding.changeButton.setOnClickListener {
            val newPrice = dialogBinding.priceEditText.text.toString()
            val selectedCurrency = currencySpinner.selectedItem.toString()

            if (newPrice.isNotEmpty()) {
                settingsViewModel.updatePackPrice(newPrice.toDouble(), selectedCurrency)
                dialog.dismiss()
                Toast.makeText(requireContext(), "Pack price updated!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Please enter a valid price", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
