package com.example.spotifyapp.ui.track_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.spotifyapp.R
import com.example.spotifyapp.databinding.FragmentListBinding

class TrackListFragment : Fragment() {
    private val viewModel: TrackListViewModel by viewModels()

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TrackListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)

        adapter = TrackListAdapter()
        adapter.setOnItemClickListener { pos, item ->
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        binding.list.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.trackListLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.search("Hate")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}