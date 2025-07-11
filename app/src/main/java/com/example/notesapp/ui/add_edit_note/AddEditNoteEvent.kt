package com.example.notesapp.ui.add_edit_note


sealed class AddEditNoteEvent {
    data class TitleChanged( val title : String): AddEditNoteEvent()
    data class BodyChanged( val body : String): AddEditNoteEvent()
    data object  TitleFocusChanged : AddEditNoteEvent()
    data object BodyFocusChanged : AddEditNoteEvent()
    data object SaveNote: AddEditNoteEvent()
    data object AlertDialogVisibilityChanged: AddEditNoteEvent()
}