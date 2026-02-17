package pt.whackaportugues.app.ui.characterselect

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pt.whackaportugues.app.databinding.ItemCharacterBinding
import pt.whackaportugues.app.model.Character

class CharacterAdapter(
    private val onCharacterSelected: (Character) -> Unit
) : RecyclerView.Adapter<CharacterAdapter.ViewHolder>() {

    private val characters = Character.values().toList()

    inner class ViewHolder(private val binding: ItemCharacterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(character: Character) {
            binding.imgCaricature.setImageResource(character.caricatureRes)
            binding.tvCharName.text = character.displayName
            binding.tvCharTitle.text = character.title
            binding.root.setOnClickListener { onCharacterSelected(character) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCharacterBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(characters[position])
    }

    override fun getItemCount() = characters.size
}
