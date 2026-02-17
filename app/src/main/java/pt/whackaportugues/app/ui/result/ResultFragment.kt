package pt.whackaportugues.app.ui.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import pt.whackaportugues.app.R
import pt.whackaportugues.app.ads.AdManager
import pt.whackaportugues.app.databinding.FragmentResultBinding
import pt.whackaportugues.app.model.Character
import pt.whackaportugues.app.viewmodel.GameViewModel

class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GameViewModel by activityViewModels()
    private var scoreSaved = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val characterId = requireArguments().getInt("characterId")
        val score = requireArguments().getInt("score")
        val character = Character.fromId(characterId)

        binding.tvFinalScore.text = score.toString()
        binding.tvCharacterName.text = character.displayName
        binding.tvCharacterTitle.text = character.title
        binding.imgCaricature.setImageResource(character.caricatureRes)

        binding.etPlayerName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveScoreIfValid()
                true
            } else false
        }

        binding.btnSave.setOnClickListener {
            saveScoreIfValid()
        }

        binding.btnPlayAgain.setOnClickListener {
            showAdThenNavigate {
                findNavController().navigate(R.id.action_result_to_menu)
            }
        }

        binding.btnLeaderboard.setOnClickListener {
            val bundle = Bundle().apply { putInt("characterId", characterId) }
            findNavController().navigate(R.id.action_result_to_leaderboard, bundle)
        }

        viewModel.savedScoreId.observe(viewLifecycleOwner) { id ->
            if (id != null && id > 0) {
                binding.tilPlayerName.visibility = View.GONE
                binding.btnSave.visibility = View.GONE
                binding.tvSavedMessage.visibility = View.VISIBLE
            }
        }
    }

    private fun saveScoreIfValid() {
        if (scoreSaved) return
        val name = binding.etPlayerName.text?.toString()?.trim() ?: ""
        if (name.isEmpty()) {
            binding.tilPlayerName.error = getString(R.string.error_name_required)
            return
        }
        binding.tilPlayerName.error = null
        scoreSaved = true
        viewModel.saveScore(name)
    }

    private fun showAdThenNavigate(onDone: () -> Unit) {
        AdManager.showInterstitial(requireActivity(), onDone)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
