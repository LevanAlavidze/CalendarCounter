package com.example.testforcalendarcounter.adapter


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.example.testforcalendarcounter.R
import com.example.testforcalendarcounter.data.memorycard.MemoryCard
import com.example.testforcalendarcounter.databinding.ItemMemoryCardBinding

class MemoryGameAdapter(
    private val cards: List<MemoryCard>,
    private val onCardClicked: (position: Int) -> Unit
) : RecyclerView.Adapter<MemoryGameAdapter.ViewHolder>() {

    inner class ViewHolder(
        private val binding: ItemMemoryCardBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(card: MemoryCard) {
            // Decide which drawable to show
            val resource = if (card.isFaceUp || card.isMatched) {
                card.content
            } else {
                R.drawable.card_back
            }

            // Option A: If you want to animate *every* time we bind:
            //   animateFlip(binding.cardImage) { binding.cardImage.setImageResource(resource) }
            // But this might cause repeated flips while scrolling.

            // Simpler approach: Just set the image.
            binding.cardImage.setImageResource(resource)

            // Click to flip
            binding.root.setOnClickListener {
                onCardClicked(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMemoryCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cards[position])
    }

    override fun getItemCount(): Int = cards.size

    /**
     * Animate the flipping motion. You can call this in `bind()` or in the click listener,
     * whenever you detect a flip transition.
     *
     * onHalfFlip() is triggered halfway through the flip, so you can switch images.
     */
    private fun animateFlip(view: View, onHalfFlip: () -> Unit = {}) {
        val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0f).apply {
            duration = 100
            interpolator = AccelerateInterpolator()
        }
        val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f).apply {
            duration = 100
            interpolator = DecelerateInterpolator()
        }

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(scaleDownY, scaleUpY)
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationRepeat(animation: Animator) {
                super.onAnimationRepeat(animation)
                // Switch resource mid-flip
                onHalfFlip()
            }
        })
        animatorSet.start()
    }
}
