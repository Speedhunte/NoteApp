package com.example.notesapp.ui.add_edit_note

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notesapp.AppViewModelProvider
import com.example.notesapp.R
import com.example.notesapp.ui.main_screen.NotesTopAppBar
import kotlinx.coroutines.flow.collectLatest


@Preview(showBackground = true)
@Composable
fun DialogWindowPreview(){
    DialogWindow (
        onDismiss = {}
    )
}


@Composable
fun DialogWindow(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .widthIn(min = 280.dp, max = 560.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "You can't save an empty note",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "OK",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    navigateUp: ()-> Unit,
    viewModel: AddEditNoteViewModel =viewModel(factory=AppViewModelProvider.Factory)
){

    val isAlertDialogVisible by viewModel.isAlertDialogVisible.collectAsState()
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
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null
                )
            }
        }

    ){ innerPadding ->

        LaunchedEffect(true) {
            viewModel.eventFlow.collectLatest {
                when(it){
                    is UiEvent.NavigateUp->navigateUp()
                }
            }
        }

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
                placeholder = noteTitle.hint,
                singleLine = true,
                textStyle = MaterialTheme.typography.headlineLarge
            )
            AnimatedVisibility(
                visible = isAlertDialogVisible
            ) {
                DialogWindow (
                    onDismiss = {
                        viewModel.onEvent(AddEditNoteEvent.AlertDialogVisibilityChanged)
                    }
                )
            }

            InvisibleTextField(
                modifier = Modifier.padding(8.dp),
                value = noteBody.text,
                onValueChange = {
                    viewModel.onEvent(AddEditNoteEvent.BodyChanged(it))
                    viewModel.onEvent(AddEditNoteEvent.BodyFocusChanged)
                },
                isHintVisible = noteBody.isHintVisible,
                placeholder = noteBody.hint,
                textStyle = MaterialTheme.typography.bodyLarge
            )

        }
    }


}