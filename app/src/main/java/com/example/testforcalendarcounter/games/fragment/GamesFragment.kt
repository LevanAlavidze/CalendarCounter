package com.example.testforcalendarcounter.games.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.testforcalendarcounter.R
import com.example.testforcalendarcounter.adapter.GameAdapter
import com.example.testforcalendarcounter.data.GameItem
import com.example.testforcalendarcounter.databinding.FragmentGamesBinding

class GamesFragment : Fragment() {

    private var _binding: FragmentGamesBinding? = null
    private val binding get() = _binding!!

    // Define your available games.
    // Make sure the destinationId values match your navigation graph IDs.
    private val gameList = listOf(
        GameItem(
            title = "Memory Game",
            iconResId = R.drawable.ic_game, // Replace with your icon resource
            destinationId = R.id.memoryGameFragment
        ),
        GameItem(
            title = "Tap the Cigarette",
            iconResId = R.drawable.ic_games, // Replace with your icon resource
            destinationId = R.id.tapGameFragment
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGamesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView with GridLayoutManager (2 columns here)
        binding.rvGames.layoutManager = GridLayoutManager(requireContext(), 2)
        val adapter = GameAdapter(gameList) { gameItem ->
            // Navigate to the selected game's fragment.
            findNavController().navigate(gameItem.destinationId)
        }
        binding.rvGames.adapter = adapter


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
