package com.example.mynotes.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * from note ORDER BY title ASC")
    fun getNotes(): Flow<List<Note>>

    @Query("SELECT * from note WHERE id = :id")
    fun getNote(id: Int): Flow<Note>

    @Query("SELECT * from note WHERE title LIKE :searchQuery OR body LIKE :searchQuery")
    fun searchDatabase(searchQuery: String): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)
}