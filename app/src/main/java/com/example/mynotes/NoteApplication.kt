package com.example.mynotes

import android.app.Application
import com.example.mynotes.data.NoteRoomDatabase

class NoteApplication : Application() {

    val database: NoteRoomDatabase by lazy { NoteRoomDatabase.getDatabase(this) }
}