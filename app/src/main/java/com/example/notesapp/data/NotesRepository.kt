package com.example.notesapp.data


class NotesRepository (private val dao: NoteDao){

    fun getNotesStream()=dao.getAllNotes()

    suspend fun getNoteStream(id: Int) = dao.getNote(id)

    suspend fun deleteNote(note: Note ) = dao.deleteNote(note)

    suspend fun addNote(note: Note)= dao.addNote(note)
}