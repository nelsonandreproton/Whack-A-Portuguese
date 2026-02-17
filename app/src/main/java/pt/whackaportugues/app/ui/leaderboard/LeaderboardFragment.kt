package pt.whackaportugues.app.ui.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import pt.whackaportugues.app.R
import pt.whackaportugues.app.ads.AdManager
import pt.whackaportugues.app.databinding.FragmentLeaderboardBinding
import pt.whackaportugues.app.model.Character
import pt.whackaportugues.app.viewmodel.LeaderboardViewModel

class LeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LeaderboardViewModel by viewModels()
    private lateinit var adapter: LeaderboardAdapter
    private var filterCharacterId = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        filterCharacterId = requireArguments().getInt("characterId", -1)
        adapter = LeaderboardAdapter()

        binding.rvScores.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@LeaderboardFragment.adapter
        }

        setupTabs()
        AdManager.loadBanner(binding.adView)
    }

    private fun setupTabs() {
        // Tab "Todos"
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText(R.string.tab_all)
        )

        // One tab per character
        Character.values().forEach { character ->
            binding.tabLayout.addTab(
                binding.tabLayout.newTab().setText(character.displayName)
            )
        }

        // Select the character's tab if coming from result screen
        if (filterCharacterId >= 0) {
            binding.tabLayout.getTabAt(filterCharacterId + 1)?.select()
            observeCharacterScores(filterCharacterId)
        } else {
            observeGlobalScores()
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val pos = tab.position
                if (pos == 0) {
                    observeGlobalScores()
                } else {
                    observeCharacterScores(pos - 1)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun observeGlobalScores() {
        viewModel.globalTopScores.observe(viewLifecycleOwner) { scores ->
            adapter.submitList(scores)
            binding.tvEmpty.visibility = if (scores.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun observeCharacterScores(characterId: Int) {
        viewModel.getTopScoresForCharacter(characterId).observe(viewLifecycleOwner) { scores ->
            adapter.submitList(scores)
            binding.tvEmpty.visibility = if (scores.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.adView.destroy()
        _binding = null
    }
}
