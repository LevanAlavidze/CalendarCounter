package com.example.testforcalendarcounter.memorygame

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.GridLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.testforcalendarcounter.R
import com.example.testforcalendarcounter.data.memorycard.MemoryCard
import com.example.testforcalendarcounter.databinding.FragmentMemoryGameBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MemoryGameFragment : Fragment() {

    private var _binding: FragmentMemoryGameBinding? = null
    private val binding get() = _binding!!

    // All memory cards + parallel list of imageViews
    private lateinit var cards: MutableList<MemoryCard>
    private lateinit var cardViews: MutableList<ImageView>

    // Game logic
    private var selectedCardIndex: Int? = null
    private var matchedPairsCount = 0
    private var isBusy = false

    // Handler for flipping all cards face-down after a delay
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMemoryGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startNewGame() // Initialize and start the first game.
    }

    /**
     * Called at startup and again whenever the game finishes.
     * It will:
     * 1. Clear any old views.
     * 2. Shuffle and create cards.
     * 3. Set up the GridLayout with ImageViews.
     * 4. Temporarily flip all cards face up for 1s, then flip them down.
     */
    private fun startNewGame() {
        // Reset game variables
        matchedPairsCount = 0
        selectedCardIndex = null
        isBusy = false

        // Clear the grid if there's anything from a previous run
        binding.gridLayout.removeAllViews()

        // Create a new shuffled deck
        setupMemoryCards()

        // Initialize the grid layout with new ImageViews
        initGridLayout()

        // Immediately flip all cards up for 1 second
        flipAllCardsFaceUp()
        mainHandler.postDelayed({
            // After 1 second, flip them face down to start the game
            flipAllCardsFaceDown()
        }, 1000)
    }

    /**
     * Sets up a deck of 10 images, duplicated and shuffled -> 20 total cards.
     */
    private fun setupMemoryCards() {
        val images = listOf(
            R.drawable.card_apple,
            R.drawable.card_banana,
            R.drawable.card_cherry,
            R.drawable.card_grape,
            R.drawable.card_lemon,
            R.drawable.card_mango,
            R.drawable.card_orange,
            R.drawable.card_peach,
            R.drawable.card_strawberry,
            R.drawable.card_watermelon
        )
        val cardImages = (images + images).shuffled()
        cards = cardImages.mapIndexed { index, imageRes ->
            MemoryCard(id = index, content = imageRes)
        }.toMutableList()
    }

    /**
     * Creates 20 ImageViews (4 cols x 5 rows) and adds them to the GridLayout.
     * Also populates cardViews with references for easy flipping.
     */
    private fun initGridLayout() {
        cardViews = mutableListOf()

        val columns = 4
        val rows = 5
        val totalCards = columns * rows

        binding.gridLayout.columnCount = columns
        binding.gridLayout.rowCount = rows

        for (i in 0 until totalCards) {
            val imageView = ImageView(requireContext()).apply {
                setImageResource(R.drawable.card_back)
                adjustViewBounds = true
                scaleType = ImageView.ScaleType.FIT_CENTER

                // On click -> flip logic
                setOnClickListener { onCardClicked(i) }
            }

            val rowIndex = i / columns
            val colIndex = i % columns

            val param = GridLayout.LayoutParams(
                GridLayout.spec(rowIndex, 1f),
                GridLayout.spec(colIndex, 1f)
            ).apply {
                width = 0
                height = 0
                leftMargin = 4
                rightMargin = 4
                topMargin = 4
                bottomMargin = 4
            }
            imageView.layoutParams = param

            binding.gridLayout.addView(imageView)
            cardViews.add(imageView)
        }
    }

    /**
     * Flip all cards face up.
     */
    private fun flipAllCardsFaceUp() {
        for (i in cards.indices) {
            cards[i].isFaceUp = true
            cardViews[i].setImageResource(cards[i].content)
        }
    }

    /**
     * Flip all cards face down.
     */
    private fun flipAllCardsFaceDown() {
        for (i in cards.indices) {
            cards[i].isFaceUp = false
            cardViews[i].setImageResource(R.drawable.card_back)
        }
    }

    /**
     * Handle clicks on a card.
     */
    private fun onCardClicked(position: Int) {
        if (isBusy) return
        val card = cards[position]
        if (card.isFaceUp || card.isMatched) return

        // Flip face up
        card.isFaceUp = true
        showCardFace(position)

        if (selectedCardIndex == null) {
            // first card
            selectedCardIndex = position
        } else {
            // second card
            isBusy = true
            checkForMatch(selectedCardIndex!!, position)
            selectedCardIndex = null
        }
    }

    /**
     * Checks if the two cards match.
     * If not, flips them back after a short delay.
     */
    private fun checkForMatch(firstPos: Int, secondPos: Int) {
        val firstCard = cards[firstPos]
        val secondCard = cards[secondPos]

        if (firstCard.content == secondCard.content) {
            // matched
            firstCard.isMatched = true
            secondCard.isMatched = true
            matchedPairsCount++
            isBusy = false

            if (matchedPairsCount == 10) {
                onGameOver()
            }
        } else {
            // Not a match -> flip them back after short delay
            viewLifecycleOwner.lifecycleScope.launch {
                delay(500) // 0.5s
                firstCard.isFaceUp = false
                secondCard.isFaceUp = false
                showCardBack(firstPos)
                showCardBack(secondPos)
                isBusy = false
            }
        }
    }

    private fun showCardFace(position: Int) {
        val card = cards[position]
        if (card.isFaceUp) {
            cardViews[position].setImageResource(card.content)
        }
    }

    private fun showCardBack(position: Int) {
        cardViews[position].setImageResource(R.drawable.card_back)
    }

    /**
     * Once all pairs are matched, immediately start a fresh game.
     */
    private fun onGameOver() {
        // Small delay so user sees the final match before resetting
        mainHandler.postDelayed({
            startNewGame()
        }, 800)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
