package com.example.spotifyapp.ui.track_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.spotifyapp.R
import com.example.spotifyapp.databinding.FragmentListBinding
import com.example.spotifyapp.utils.hideKeyboard

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
            findNavController().navigate(
                R.id.action_open_track_detail,
                bundleOf("url" to item.externalUrl)
            )
        }
        binding.list.adapter = adapter

        binding.search.setOnEditorActionListener { view, actionId, _ ->
            var consumed = false
            if (actionId == IME_ACTION_SEARCH) {
                viewModel.search(view.text.toString())
                (view as EditText).hideKeyboard()
                consumed = true
            }
            consumed
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadingLiveData.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) VISIBLE else GONE
        }
        viewModel.trackListLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}