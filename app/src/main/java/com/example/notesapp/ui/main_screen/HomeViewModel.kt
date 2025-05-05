package com.example.notesapp.ui.main_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.data.Note
import com.example.notesapp.data.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val notesRepository: NotesRepository
): ViewModel() {


    private val isSortSectionVisible = MutableStateFlow(false)
    private val orderType = MutableStateFlow(OrderType.Descending)


    private var recentlyDeletedNote: Note? = null


    val homeUiState: StateFlow<HomeUiState> = combine(
        notesRepository.getNotesStream(),
        orderType,
        isSortSectionVisible
    ) { notes, orderType, isSortSectionVisible ->
        val sortedNotes = when(orderType) {
            OrderType.Descending -> notes.sortedByDescending { it.timestamp }
            OrderType.Ascending -> notes.sortedBy { it.timestamp }
        }
        HomeUiState(
            notes = sortedNotes,
            isSortSectionVisible = isSortSectionVisible,
            orderType = orderType
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun onSortSectionVisibilityChanged(){
        isSortSectionVisible.value=!isSortSectionVisible.value
    }

    fun onOrderTypeUpdated(orderType: OrderType){
        this.orderType.value=orderType
    }

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

enum class OrderType{
    Ascending,
    Descending
}

data class HomeUiState(
    val notes: List<Note> = listOf(),
    val isSortSectionVisible: Boolean= false,
    val orderType: OrderType = OrderType.Descending
)