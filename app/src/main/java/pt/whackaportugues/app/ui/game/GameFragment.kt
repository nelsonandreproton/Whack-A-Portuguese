package pt.whackaportugues.app.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import pt.whackaportugues.app.R
import pt.whackaportugues.app.databinding.FragmentGameBinding
import pt.whackaportugues.app.model.Character
import pt.whackaportugues.app.viewmodel.GameViewModel

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GameViewModel by activityViewModels()

    private val moleViews = mutableListOf<ImageView>()
    private var character: Character? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val characterId = requireArguments().getInt("characterId")
        character = Character.fromId(characterId)

        setupCharacterTheme()
        buildMoleGrid()
        observeViewModel()

        viewModel.startGame(character!!)
    }

    private fun setupCharacterTheme() {
        val char = character ?: return
        binding.gameRoot.setBackgroundResource(char.backgroundRes)
        binding.tvCharacterName.text = char.displayName
        binding.tvCharacterTitle.text = char.title

        val primaryColor = ContextCompat.getColor(requireContext(), char.primaryColor)
        binding.tvScore.setTextColor(primaryColor)
        binding.tvTimer.setTextColor(primaryColor)
    }

    private fun buildMoleGrid() {
        val char = character ?: return
        val grid = binding.gridHoles
        grid.columnCount = 3
        grid.rowCount = 3
        moleViews.clear()

        val holeSizePx = resources.getDimensionPixelSize(R.dimen.hole_size)
        val holePaddingPx = resources.getDimensionPixelSize(R.dimen.hole_padding)

        for (i in 0 until 9) {
            val holeView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_hole, grid, false)

            val moleImageView = holeView.findViewById<ImageView>(R.id.imgMole)
            moleImageView.setImageResource(char.caricatureRes)
            moleImageView.visibility = View.INVISIBLE

            val params = android.widget.GridLayout.LayoutParams().apply {
                width = holeSizePx
                height = holeSizePx
                setMargins(holePaddingPx, holePaddingPx, holePaddingPx, holePaddingPx)
                columnSpec = android.widget.GridLayout.spec(i % 3, 1f)
                rowSpec = android.widget.GridLayout.spec(i / 3, 1f)
            }
            holeView.layoutParams = params

            val holeIndex = i
            holeView.setOnClickListener {
                viewModel.onHoleTapped(holeIndex)
            }

            grid.addView(holeView)
            moleViews.add(moleImageView)
        }
    }

    private fun observeViewModel() {
        viewModel.score.observe(viewLifecycleOwner) { score ->
            binding.tvScore.text = getString(R.string.score_format, score)
        }

        viewModel.timeLeftSec.observe(viewLifecycleOwner) { seconds ->
            binding.tvTimer.text = getString(R.string.timer_format, seconds / 60, seconds % 60)
            binding.progressTimer.progress = seconds.toInt()
        }

        viewModel.activeMoleIndex.observe(viewLifecycleOwner) { index ->
            moleViews.forEachIndexed { i, moleView ->
                if (i == index) showMole(moleView) else if (moleView.visibility == View.VISIBLE) hideMole(moleView)
            }
        }

        viewModel.hitIndex.observe(viewLifecycleOwner) { index ->
            if (index in 0 until moleViews.size) {
                playHitAnimation(moleViews[index])
            }
        }

        viewModel.gameState.observe(viewLifecycleOwner) { state ->
            if (state == GameViewModel.GameState.FINISHED) {
                navigateToResult()
            }
        }
    }

    private fun showMole(moleView: ImageView) {
        val translationAmount = moleView.height.toFloat().takeIf { it > 0f } ?: 200f
        moleView.translationY = translationAmount
        moleView.visibility = View.VISIBLE
        moleView.animate()
            .translationY(0f)
            .setDuration(180)
            .start()
    }

    private fun hideMole(moleView: ImageView) {
        val translationAmount = moleView.height.toFloat().takeIf { it > 0f } ?: 200f
        moleView.animate()
            .translationY(translationAmount)
            .setDuration(180)
            .withEndAction { moleView.visibility = View.INVISIBLE }
            .start()
    }

    private fun playHitAnimation(moleView: ImageView) {
        moleView.animate()
            .scaleX(1.4f).scaleY(1.4f)
            .setDuration(80)
            .withEndAction {
                moleView.animate()
                    .scaleX(1f).scaleY(1f)
                    .setDuration(80)
                    .withEndAction { hideMole(moleView) }
                    .start()
            }
            .start()
    }

    private fun navigateToResult() {
        val char = character ?: return
        val bundle = Bundle().apply {
            putInt("characterId", char.id)
            putInt("score", viewModel.score.value ?: 0)
        }
        findNavController().navigate(R.id.action_game_to_result, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        moleViews.clear()
        _binding = null
    }
}
