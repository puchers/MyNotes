package com.example.mynotes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mynotes.data.Note
import com.example.mynotes.databinding.NoteListItemBinding

class NoteListAdapter(private val onNoteClicked: (Note) -> Unit, private val viewModel: NoteViewModel) :
    ListAdapter<Note, NoteListAdapter.NoteViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            NoteListItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onNoteClicked(current)
        }
        holder.bind(current)
    }

        inner class NoteViewHolder(private var binding: NoteListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) {
            val searchQuery = viewModel.searchQuery
            binding.note = note

            if (searchQuery.value.toString().isNotBlank()) {
                val highlightedText = note.noteTitle.replace(searchQuery.value.toString(), "<span style=\"background:yellow\">${searchQuery.value.toString()}</span>", true)
                binding.noteTitle.text = HtmlCompat.fromHtml(highlightedText, HtmlCompat.FROM_HTML_MODE_LEGACY)
            } else {
                binding.noteTitle.text = note.noteTitle
            }
            if (searchQuery.value.toString().isNotBlank()) {
                val highlightedText = note.noteBody.replace(searchQuery.value.toString(), "<span style=\"background:yellow\">${searchQuery.value.toString()}</span>", true)
                binding.noteBody.text = HtmlCompat.fromHtml(highlightedText, HtmlCompat.FROM_HTML_MODE_LEGACY)
            } else {
                binding.noteBody.text = note.noteBody
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
                return oldItem.noteTitle == newItem.noteTitle
            }
        }
    }
}