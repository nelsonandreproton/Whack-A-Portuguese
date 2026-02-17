package pt.whackaportugues.app.ui.leaderboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pt.whackaportugues.app.databinding.ItemScoreBinding
import pt.whackaportugues.app.model.Score

class LeaderboardAdapter : ListAdapter<Score, LeaderboardAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemScoreBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(score: Score, position: Int) {
            binding.tvPosition.text = "#${position + 1}"
            binding.tvPlayerName.text = score.playerName
            binding.tvScore.text = score.points.toString()
            binding.tvCharacterName.text = score.characterName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemScoreBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    private class DiffCallback : DiffUtil.ItemCallback<Score>() {
        override fun areItemsTheSame(oldItem: Score, newItem: Score) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Score, newItem: Score) = oldItem == newItem
    }
}
