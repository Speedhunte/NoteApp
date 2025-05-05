package com.example.notesapp.ui.add_edit_note

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.notesapp.data.Note
import com.example.notesapp.data.NotesRepository
import com.example.notesapp.ui.main_screen.NotesDestination
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AddEditNoteViewModel(
    savedStateHandle: SavedStateHandle,
    private val notesRepository: NotesRepository
): ViewModel() {


    private var noteId: Int? = null

    private var _isAlertDialogVisible = MutableStateFlow(false)
    val isAlertDialogVisible: StateFlow<Boolean> = _isAlertDialogVisible

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var _noteTitle = MutableStateFlow(NoteTextFieldState(hint = "Enter your title"))
    val noteTitle: StateFlow<NoteTextFieldState> = _noteTitle


    private var _noteBody = MutableStateFlow(NoteTextFieldState(hint = "Your note body is here"))
    val noteBody: StateFlow<NoteTextFieldState> = _noteBody


    init{
        savedStateHandle.toRoute<NotesDestination.DetailsScreen>().noteId.let {
            thisNoteId->
            viewModelScope.launch {
                if (thisNoteId != null) {
                    noteId=thisNoteId
                    notesRepository.getNoteStream(thisNoteId)?.also { note->
                        _noteTitle.value=noteTitle.value.copy(
                            text = note.title,
                            isHintVisible = false
                        )
                        _noteBody.value=noteBody.value.copy(
                            text = note.body,
                            isHintVisible = false
                        )

                    }
                }
            }
        }

    }

    private fun validateInput(): Boolean{
        return noteTitle.value.text.isNotBlank() || noteBody.value.text.isNotBlank()
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

            is AddEditNoteEvent.BodyFocusChanged ->{
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
                if (validateInput()){
                    viewModelScope.launch {
                        notesRepository.addNote(
                            Note(
                                id = noteId,
                                title = noteTitle.value.text,
                                body = noteBody.value.text,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                        _eventFlow.emit(UiEvent.NavigateUp)
                    }
                }
                else{
                    _isAlertDialogVisible.value = true
                }
            }

            is AddEditNoteEvent.AlertDialogVisibilityChanged->{
                _isAlertDialogVisible.value= !isAlertDialogVisible.value
            }
        }
    }

}

sealed class UiEvent{
    data object NavigateUp:UiEvent()
}

data class NoteTextFieldState(
    val text : String="",
    val hint: String="",
    val isHintVisible: Boolean =true
)


