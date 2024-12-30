package com.example.testforcalendarcounter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.testforcalendarcounter.databinding.DialogPackPriceBinding
import com.example.testforcalendarcounter.databinding.FragmentSettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    // Using its own SettingsViewModel
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe pack price
        settingsViewModel.packPrice.observe(viewLifecycleOwner) { packPrice ->
            val formattedPrice = "%.2f %s".format(packPrice.price, packPrice.currency)
            binding.PriceTextView.text = formattedPrice
        }

        binding.changePackPriceButton.setOnClickListener {
            showChangePackPriceDialog()
        }
    }

    private fun showChangePackPriceDialog() {
        val dialogBinding = DialogPackPriceBinding.inflate(layoutInflater)
        val currencySpinner: Spinner = dialogBinding.currencySpinner
        val currencies = listOf("USD", "EUR", "GEL")
        currencySpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currencies)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        dialogBinding.cancelButton.setOnClickListener { dialog.dismiss() }

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
