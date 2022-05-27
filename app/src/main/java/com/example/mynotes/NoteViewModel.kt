package com.example.mynotes

import androidx.lifecycle.*
import com.example.mynotes.data.Note
import com.example.mynotes.data.NoteDao
import kotlinx.coroutines.launch

class NoteViewModel(private val noteDao: NoteDao) : ViewModel() {


    val allNotes: LiveData<List<Note>> = noteDao.getNotes().asLiveData()
    val searchQuery: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }


    fun updateNote(
        noteId: Int,
        noteTitle: String,
        noteBody: String
    ) {
        val updatedNote = getUpdatedNoteEntry(noteId, noteTitle, noteBody)
        updateNote(updatedNote)
    }


    private fun updateNote(note: Note) {
        viewModelScope.launch {
            noteDao.update(note)
        }
    }


    fun addNewNote(noteTitle: String, noteBody: String) {
        val newNote = getNewNoteEntry(noteTitle, noteBody)
        insertNote(newNote)
    }

    private fun insertNote(note: Note) {
        viewModelScope.launch {
            noteDao.insert(note)
        }
    }


    fun deleteItem(note: Note) {
        viewModelScope.launch {
            noteDao.delete(note)
        }
    }

    fun retrieveNote(id: Int): LiveData<Note> {
        return noteDao.getNote(id).asLiveData()
    }


    fun isEntryValid(noteTitle: String, noteBody: String): Boolean {
        if (noteTitle.isBlank() || noteBody.isBlank()) {
            return false
        }
        return true
    }

    private fun getNewNoteEntry(noteTitle: String, noteBody: String): Note {
        return Note(
            noteTitle = noteTitle,
            noteBody = noteBody
        )
    }

    private fun getUpdatedNoteEntry(
        noteId: Int,
        noteTitle: String,
        noteBody: String
    ): Note {
        return Note(
            id = noteId,
            noteTitle = noteTitle,
            noteBody = noteBody
        )
    }

    fun searchDatabase(searchQuery: String): LiveData<List<Note>> {
        return noteDao.searchDatabase(searchQuery).asLiveData()
    }
}

class NoteViewModelFactory(private val noteDao: NoteDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(noteDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}