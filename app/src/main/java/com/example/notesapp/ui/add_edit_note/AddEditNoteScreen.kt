package com.example.notesapp.ui.add_edit_note

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notesapp.AppViewModelProvider
import com.example.notesapp.R
import com.example.notesapp.ui.NotesTopAppBar

@Composable
fun AddEditNoteScreen(
    navigateUp: ()-> Unit,
    viewModel: AddEditNoteViewModel =viewModel(factory=AppViewModelProvider.Factory)
){

    val noteTitle by viewModel.noteTitle.collectAsState()
    val noteBody by viewModel.noteBody.collectAsState()

    Scaffold (
        topBar = {
            NotesTopAppBar(
                title = stringResource(R.string.add_note),
                canNavigateBack = true,
                navigateUp=navigateUp
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onEvent(AddEditNoteEvent.SaveNote)
                    navigateUp()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null
                )
            }
        }

    ){ innerPadding ->

        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            InvisibleTextField(
                modifier = Modifier.padding(8.dp),
                value = noteTitle.text,
                onValueChange = {
                    viewModel.onEvent(AddEditNoteEvent.TitleChanged(it))
                    viewModel.onEvent(AddEditNoteEvent.TitleFocusChanged)
                },

                isHintVisible = noteTitle.isHintVisible,
                placeholder = "Enter your title",
                singleLine = true,
                textStyle = MaterialTheme.typography.headlineLarge
            )

            InvisibleTextField(
                modifier = Modifier.padding(8.dp),
                value = noteBody.text,
                onValueChange = {
                    viewModel.onEvent(AddEditNoteEvent.BodyChanged(it))
                    viewModel.onEvent(AddEditNoteEvent.BodyFocusChange)
                },
                isHintVisible = noteBody.isHintVisible,
                placeholder = "Your note body is here",
                textStyle = MaterialTheme.typography.bodyLarge
            )

        }
    }


}