package pt.whackaportugues.app.ui.characterselect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import pt.whackaportugues.app.R
import pt.whackaportugues.app.databinding.FragmentCharacterSelectBinding
import pt.whackaportugues.app.model.Character

class CharacterSelectFragment : Fragment() {

    private var _binding: FragmentCharacterSelectBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharacterSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = CharacterAdapter { character ->
            navigateToGame(character)
        }

        binding.rvCharacters.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            this.adapter = adapter
        }
    }

    private fun navigateToGame(character: Character) {
        val bundle = Bundle().apply {
            putInt("characterId", character.id)
        }
        findNavController().navigate(R.id.action_characterSelect_to_game, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
