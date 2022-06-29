package com.example.mynotes

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mynotes.data.Note
import com.example.mynotes.databinding.FragmentAddNoteBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddNoteFragment : Fragment() {

    private val viewModel: NoteViewModel by activityViewModels {
        NoteViewModelFactory(
            (activity?.application as NoteApplication).database
                .noteDao()
        )
    }
    private val navigationArgs: AddNoteFragmentArgs by navArgs()

    lateinit var note: Note
    private var color: Int = Color.parseColor("#FF81deea")

    private var _binding: FragmentAddNoteBinding? = null
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
        _binding = FragmentAddNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_note -> {
                showConfirmationDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val id = navigationArgs.noteId
        val searchNote = menu.findItem(R.id.search_note)
        val deleteNote = menu.findItem(R.id.delete_note)
        if (id == -1) {
            deleteNote.isVisible = false
        }
        searchNote.isVisible = false
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
            root.setBackgroundColor(note.noteColor)
            color1.setOnClickListener {
                note.noteColor =
                    Color.parseColor("#FF81deea"); root.setBackgroundColor(note.noteColor)
            }
            color2.setOnClickListener {
                note.noteColor =
                    Color.parseColor("#AED581"); root.setBackgroundColor(note.noteColor)
            }
            color3.setOnClickListener {
                note.noteColor =
                    Color.parseColor("#FF94DA"); root.setBackgroundColor(note.noteColor)
            }
            color4.setOnClickListener {
                note.noteColor =
                    Color.parseColor("#f06292"); root.setBackgroundColor(note.noteColor)
            }
            color5.setOnClickListener {
                note.noteColor =
                    Color.parseColor("#FFAB91"); root.setBackgroundColor(note.noteColor)
            }
        }
    }

    private fun addNewNote() {
        if (isEntryValid()) {
            viewModel.addNewNote(
                binding.noteTitle.text.toString(),
                binding.noteBody.text.toString(),
                color
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
                this.binding.noteBody.text.toString(),
                this.note.noteColor
            )
            val action = AddNoteFragmentDirections.actionAddNoteFragmentToNoteListFragment()
            findNavController().navigate(action)
        }
    }

    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteNote()
            }
            .show()
    }

    private fun deleteNote() {
        viewModel.deleteItem(note)
        findNavController().navigateUp()
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
            binding.color1.setOnClickListener {
                color = Color.parseColor("#FF81deea"); binding.root.setBackgroundColor(color)
            }
            binding.color2.setOnClickListener {
                color = Color.parseColor("#AED581"); binding.root.setBackgroundColor(color)
            }
            binding.color3.setOnClickListener {
                color = Color.parseColor("#FF94DA"); binding.root.setBackgroundColor(color)
            }
            binding.color4.setOnClickListener {
                color = Color.parseColor("#f06292"); binding.root.setBackgroundColor(color)
            }
            binding.color5.setOnClickListener {
                color = Color.parseColor("#FFAB91"); binding.root.setBackgroundColor(color)
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