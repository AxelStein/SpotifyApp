package com.example.spotifyapp.ui.track_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.spotifyapp.databinding.FragmentTrackDetailBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class TrackDetailFragment : Fragment() {
    private var _binding: FragmentTrackDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackDetailBinding.inflate(inflater, container, false)
        val url = arguments?.getString("url") ?: ""
        if (url.isNotBlank()) {
            binding.root.loadUrl(url)
        }
        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}