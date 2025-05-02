package com.example.notesapp.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notesapp.data.Note
import com.example.notesapp.data.NotesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val notesRepository: NotesRepository
): ViewModel() {

    private var recentlyDeletedNote: Note? = null
    val homeUiState: StateFlow<HomeUiState> =
        notesRepository.getNotesStream().map { HomeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = HomeUiState()
            )

     fun deleteNote(note: Note){
         viewModelScope.launch {
             notesRepository.deleteNote(note)
             recentlyDeletedNote=note
         }
    }

    suspend fun restoreNote(){
            recentlyDeletedNote?.let { notesRepository.addNote(it) }
            recentlyDeletedNote=null
    }
}

data class HomeUiState( val notes: List<Note> = listOf())