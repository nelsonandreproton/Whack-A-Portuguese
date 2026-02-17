package pt.whackaportugues.app.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import pt.whackaportugues.app.R
import pt.whackaportugues.app.ads.AdManager
import pt.whackaportugues.app.databinding.FragmentMenuBinding

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPlay.setOnClickListener {
            findNavController().navigate(R.id.action_menu_to_characterSelect)
        }

        binding.btnLeaderboard.setOnClickListener {
            findNavController().navigate(R.id.action_menu_to_leaderboard)
        }

        // Load non-intrusive banner ad at bottom of menu
        AdManager.loadBanner(binding.adView)

        // Pre-load interstitial for after a round ends
        AdManager.preloadInterstitial(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.adView.destroy()
        _binding = null
    }
}
