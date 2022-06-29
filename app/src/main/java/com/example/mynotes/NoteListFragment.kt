package com.example.mynotes

import android.os.Bundle
import android.view.*
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

    lateinit var adapter: NoteListAdapter

    private var _binding: NoteListFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = NoteListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu, menu)
        val menuItem: MenuItem = menu.findItem(R.id.search_note)
        val searchView: SearchView = menuItem.actionView as SearchView
        searchView.queryHint = "Search..."

        searchView.setOnQueryTextListener(this)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val deleteNote = menu.findItem(R.id.delete_note)
        deleteNote.isVisible = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = NoteListAdapter({
            val action =
                NoteListFragmentDirections.actionNoteListFragmentToAddNoteFragment(it.id)
            this.findNavController().navigate(action)
        }, viewModel)
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = adapter

        viewModel.allNotes.observe(this.viewLifecycleOwner) { notes ->
            notes.let {
                adapter.submitList(it)
            }
        }

        binding.floatingActionButton.setOnClickListener {
            val action = NoteListFragmentDirections.actionNoteListFragmentToAddNoteFragment(-1)

            this.findNavController().navigate(action)
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null && query.isNotBlank()) {
            searchDatabase(query)
        } else updateAdapter(query)
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null && query.isNotBlank()) {
            searchDatabase(query)
        } else updateAdapter(query)
        return true
    }


    private fun searchDatabase(query: String) {
        val searchQuery = "%$query%"
        viewModel.searchQuery.value = query

        viewModel.searchDatabase(searchQuery).observe(this.viewLifecycleOwner) { notes ->
            notes.let {
                adapter.submitList(it)
            }
        }
    }

    private fun updateAdapter(query: String?) {
        viewModel.searchQuery.value = query
        viewModel.allNotes.observe(this.viewLifecycleOwner) { notes ->
            notes.let {
                adapter.submitList(it)
            }
        }
    }
}