package com.example.mynotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mynotes.databinding.NoteListFragmentBinding


class NoteListFragment : Fragment(), SearchView.OnQueryTextListener {
    private val viewModel: NoteViewModel by activityViewModels {
        NoteViewModelFactory(
            (activity?.application as NoteApplication).database.noteDao()
        )
    }

    private val adapter = NoteListAdapter {
        val action =
            NoteListFragmentDirections.actionNoteListFragmentToNoteDetailFragment(it.id)
        this.findNavController().navigate(action)
    }

    private var _binding: NoteListFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = NoteListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val adapter = NoteListAdapter {
//            val action =
//                NoteListFragmentDirections.actionNoteListFragmentToNoteDetailFragment(it.id)
//            this.findNavController().navigate(action)
//        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = adapter
        // Attach an observer on the allItems list to update the UI automatically when the data
        // changes.
        viewModel.allNotes.observe(this.viewLifecycleOwner) { notes ->
            notes.let {
                adapter.submitList(it)
            }
        }

        binding.floatingActionButton.setOnClickListener {
            val action = NoteListFragmentDirections.actionNoteListFragmentToAddNoteFragment(
                getString(R.string.add_fragment_title)
            )
            this.findNavController().navigate(action)
        }

        val search = view.findViewById<SearchView>(R.id.search_note)
        search?.setOnQueryTextListener(this)

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if(query != null) {
            searchDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if(query != null) {
            searchDatabase(query)
        }
        return true
    }


    private fun searchDatabase(query: String) {
        val searchQuery = "%$query%"

        viewModel.searchDatabase(searchQuery).observe(this.viewLifecycleOwner) { notes ->
            notes.let {
                adapter.submitList(it)
            }
        }
    }
}