package com.example.notesapp.data

import android.content.Context

interface  AppContainer {
    val notesRepository: NotesRepository
}

class MyAppContainer(private val context: Context): AppContainer{

    override val notesRepository: NotesRepository by lazy {
        NotesRepository(NotesDatabase.getDataBase(context).getDao())
    }
}


