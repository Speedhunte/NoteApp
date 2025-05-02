package com.example.notesapp

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.notesapp.ui.HomeViewModel
import com.example.notesapp.ui.add_edit_note.AddEditNoteViewModel


object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(
                notesApplication().container.notesRepository
            )
        }


        initializer {
            AddEditNoteViewModel(
                this.createSavedStateHandle(),
                notesApplication().container.notesRepository
            )
        }

    }
}

fun CreationExtras.notesApplication(): NotesApplication =
    (this[APPLICATION_KEY] as NotesApplication)