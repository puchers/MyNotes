package com.example.mynotes

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mynotes.data.Note
import com.example.mynotes.databinding.FragmentAddNoteBinding

class AddNoteFragment : Fragment() {

    private val viewModel: NoteViewModel by activityViewModels {
        NoteViewModelFactory(
            (activity?.application as NoteApplication).database
                .noteDao()
        )
    }
    private val navigationArgs: NoteDetailFragmentArgs by navArgs()

    lateinit var note: Note

    private var _binding: FragmentAddNoteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddNoteBinding.inflate(inflater, container, false)
        return binding.root
    }


    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.noteTitle.text.toString(),
            binding.noteBody.text.toString(),
        )
    }


    private fun bind(note: Note) {
        binding.apply {
            noteTitle.setText(note.noteTitle, TextView.BufferType.SPANNABLE)
            noteBody.setText(note.noteBody, TextView.BufferType.SPANNABLE)
            saveAction.setOnClickListener { updateNote() }
        }
    }


    private fun addNewNote() {
        if (isEntryValid()) {
            viewModel.addNewNote(
                binding.noteTitle.text.toString(),
                binding.noteBody.text.toString(),
            )
            val action = AddNoteFragmentDirections.actionAddNoteFragmentToNoteListFragment()
            findNavController().navigate(action)
        }
    }


    private fun updateNote() {
        if (isEntryValid()) {
            viewModel.updateNote(
                this.navigationArgs.noteId,
                this.binding.noteTitle.text.toString(),
                this.binding.noteBody.text.toString()
            )
            val action = AddNoteFragmentDirections.actionAddNoteFragmentToNoteListFragment()
            findNavController().navigate(action)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.noteId
        if (id > 0) {
            viewModel.retrieveNote(id).observe(this.viewLifecycleOwner) { selectedNote ->
                note = selectedNote
                bind(note)
            }
        } else {
            binding.saveAction.setOnClickListener {
                addNewNote()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}