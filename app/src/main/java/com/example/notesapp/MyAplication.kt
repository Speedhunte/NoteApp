package com.example.notesapp

import android.app.Application
import com.example.notesapp.data.AppContainer
import com.example.notesapp.data.MyAppContainer

class NotesApplication: Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container=MyAppContainer(this)
    }
}