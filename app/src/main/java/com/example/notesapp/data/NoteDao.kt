package com.example.notesapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNote (note: Note )

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM notes_table WHERE id=:id")
    suspend fun getNote(id : Int ) : Note?

    @Query(" SELECT * FROM notes_table")
    fun getAllNotes(): Flow<List<Note>>
}