package com.example.notesapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notesapp.AppViewModelProvider
import com.example.notesapp.R
import com.example.notesapp.data.Note
import com.example.notesapp.ui.add_edit_note.AddEditNoteScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

sealed interface NotesDestination{
    @Serializable
    data object HomeScreen: NotesDestination

    @Serializable
    data class DetailsScreen(val noteId : Int?): NotesDestination
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    navigateUp: ()->Unit={}
){
    CenterAlignedTopAppBar(
        title = {
            Text(text = title)
        },
        navigationIcon = {
            if(canNavigateBack){
                IconButton(
                    navigateUp
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        },
    )
}

@Composable
fun NotesApp(){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = NotesDestination.HomeScreen
    ){
        composable<NotesDestination.HomeScreen> {
            HomeScreen(
                navigateToCreateNote = {
                    navController.navigate(NotesDestination.DetailsScreen(null))
                   },
                navigateToNote = { id ->
                    navController.navigate(NotesDestination.DetailsScreen(id))
                    },
            )
        }
        composable<NotesDestination.DetailsScreen>{
            AddEditNoteScreen(
                navigateUp = {navController.navigateUp()}
            )
        }

    }
}

@Composable
fun HomeScreen(
    navigateToNote:(Int)-> Unit,
    navigateToCreateNote:()-> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
){

    val homeUiState by viewModel.homeUiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold (
        snackbarHost = {SnackbarHost(hostState = snackbarHostState)},
        topBar = {
            NotesTopAppBar(
              title = stringResource(R.string.bar_title),
                canNavigateBack = false
            )

        },
        floatingActionButton = {
            FloatingActionButton(
                onClick =navigateToCreateNote
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        },

    ){ innerPadding->
        if(homeUiState.notes.isEmpty()){
            Text(
                modifier = Modifier.fillMaxWidth().padding(innerPadding),
                textAlign = TextAlign.Center,
                text = "Currently you have  no notes",
                style = MaterialTheme.typography.titleLarge
            )
        }
        else {
            NotesItemList(
                items = homeUiState.notes,
                navigateToNote = navigateToNote,
                onDeleteNote ={
                    viewModel.deleteNote(it)
                    scope.launch {
                        val snackbarResult = snackbarHostState.showSnackbar(
                            message = "Note deleted",
                            actionLabel = "Undo"
                        )
                        if(snackbarResult== SnackbarResult.ActionPerformed){
                            viewModel.restoreNote()
                        }
                    }
                  },
                contentPadding = innerPadding
            )
        }
    }
}

@Composable
fun NotesItemList(
    items: List<Note>,
    onDeleteNote: (Note) -> Unit,
    navigateToNote: (Int) -> Unit,
    contentPadding: PaddingValues
){
    LazyVerticalStaggeredGrid(
        contentPadding = contentPadding,
        columns = StaggeredGridCells.Adaptive(150.dp)

    ) {
        items(items){
            NoteCardItem(
                onDeleteNote = onDeleteNote,
                onCardClick = navigateToNote,
                note = it)
        }
    }
}

@Composable
fun NoteCardItem(
    onDeleteNote: (Note)->Unit,
    onCardClick: (Int)->Unit,
    note: Note,
){
    Card(
        modifier = Modifier
            .padding(8.dp)
            .wrapContentHeight()
            .clickable {
                note.id?.let { onCardClick(it) }
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ){
        Column(
            modifier = Modifier.fillMaxWidth()
        ){

            Text(
                modifier = Modifier.padding(8.dp),
                text = note.title,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                modifier=Modifier.padding(8.dp),
                text = note.body,
                maxLines = 5
            )

            IconButton(
                modifier = Modifier.align(Alignment.End),
                onClick = {onDeleteNote(note)}
            ) {
                Icon(

                    imageVector = Icons.Default.Delete,
                    contentDescription = null
                )
            }

        }
    }

}

@Preview(showSystemUi = true)
@Composable
fun CardPreview(){
//    NoteCardItem(
//        note = Note(
//            title = "Dima",
//            body = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
//        )
//    )
}