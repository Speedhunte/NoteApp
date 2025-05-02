package com.example.notesapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Note::class], version = 2, exportSchema = false)
abstract  class NotesDatabase:RoomDatabase() {

    abstract fun getDao():NoteDao

    companion object{
        private var Instance: NotesDatabase? = null

        fun getDataBase(context: Context): NotesDatabase{
            return Instance?: synchronized(this){
                Room.databaseBuilder(context, NotesDatabase::class.java, "notes_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also{
                        Instance=it
                    }
            }
        }
    }
}