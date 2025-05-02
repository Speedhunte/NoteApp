package com.example.notesapp.ui.add_edit_note

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.toRoute
import com.example.notesapp.data.Note
import com.example.notesapp.data.NotesRepository
import com.example.notesapp.ui.NotesDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddEditNoteViewModel(
    savedStateHandle: SavedStateHandle,
    private val notesRepository: NotesRepository
): ViewModel() {


    private var noteId: Int? = null
    private var _noteTitle = MutableStateFlow(NoteTextFieldState())
    val noteTitle: StateFlow<NoteTextFieldState> = _noteTitle


    private var _noteBody = MutableStateFlow(NoteTextFieldState())
    val noteBody: StateFlow<NoteTextFieldState> = _noteBody


    init{
        savedStateHandle.toRoute<NotesDestination.DetailsScreen>().noteId.let {
            thisNoteId->
            viewModelScope.launch {
                if (thisNoteId != null) {
                    noteId=thisNoteId
                    notesRepository.getNoteStream(thisNoteId)?.also { note->
                        _noteTitle.value=NoteTextFieldState(
                            text = note.title,
                            isHintVisible = false
                        )
                        _noteBody.value=NoteTextFieldState(
                            text = note.body,
                            isHintVisible = false
                        )

                    }
                }
            }
        }

    }

    fun onEvent(event: AddEditNoteEvent){
        when(event){
            is AddEditNoteEvent.BodyChanged ->{
                _noteBody.value=_noteBody.value.copy(
                    text = event.body
                )
            }
            is AddEditNoteEvent.TitleChanged ->{
                _noteTitle.value=_noteTitle.value.copy(
                    text = event.title
                )
            }

            is AddEditNoteEvent.BodyFocusChange ->{
                _noteBody.value = _noteBody.value.copy(
                    isHintVisible = noteBody.value.text.isBlank()
                )
            }

            is AddEditNoteEvent.TitleFocusChanged ->{
                _noteTitle.value=_noteTitle.value.copy(
                    isHintVisible =  noteTitle.value.text.isBlank()
                )
            }

            is AddEditNoteEvent.SaveNote ->{
                viewModelScope.launch {
                    notesRepository.addNote(
                        Note(
                            id = noteId,
                            title = noteTitle.value.text,
                            body = noteBody.value.text
                        )
                    )
                }
            }
        }
    }

}

data class NoteTextFieldState(
    val text : String="",
    val hint: String="",
    val isHintVisible: Boolean =true
)


