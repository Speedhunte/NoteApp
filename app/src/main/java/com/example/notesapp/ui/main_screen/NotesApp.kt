package com.example.notesapp.ui.main_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
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
import androidx.compose.ui.text.font.FontWeight
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
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

sealed interface NotesDestination{
    @Serializable
    data object HomeScreen: NotesDestination

    @Serializable
    data class DetailsScreen(val noteId : Int?): NotesDestination
}

fun formatDate(timestamp: Long): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        .withZone(ZoneId.systemDefault())
    return formatter.format(Instant.ofEpochMilli(timestamp))
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    navigateUp: ()->Unit={},
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
        //Modifier.background(color = MaterialTheme.colorScheme.)
        snackbarHost = {SnackbarHost(hostState = snackbarHostState)},
        topBar = {
            NotesTopAppBar(
                title = stringResource(R.string.bar_title),
                canNavigateBack = false,
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

    ){innerPadding->
        if(homeUiState.notes.isEmpty()){
            Text(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                textAlign = TextAlign.Center,
                text = "Currently you have  no notes",
                style = MaterialTheme.typography.titleLarge
            )
        }
        else {
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                Row (
                    modifier = Modifier.height(70.dp)
                ){
                    AnimatedVisibility(visible = homeUiState.isSortSectionVisible) {
                        Column {
                            Text(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal ),
                                text = "Sort by date:"
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            )
                            {
                                SortRadioButton(
                                    currentOrderType = homeUiState.orderType,
                                    buttonOrderType = OrderType.Ascending,
                                    onClick = viewModel::onOrderTypeUpdated,
                                    buttonName = "Ascending"
                                )
                                SortRadioButton(
                                    currentOrderType = homeUiState.orderType,
                                    buttonOrderType = OrderType.Descending,
                                    onClick = viewModel::onOrderTypeUpdated,
                                    buttonName = "Descending"
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = viewModel::onSortSectionVisibilityChanged
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = null
                        )
                    }
                }

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
                )
            }
        }
    }
}

@Composable
fun NotesItemList(
    items: List<Note>,
    onDeleteNote: (Note) -> Unit,
    navigateToNote: (Int) -> Unit,
    //contentPadding: PaddingValues = PaddingValues(10.dp)
){
    LazyVerticalStaggeredGrid(
        //contentPadding = PaddingValues(top = 10.dp),
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
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){

            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = note.title,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                modifier=Modifier,
                text = note.body,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 5
            )
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = formatDate(note.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )


                IconButton(
                    onClick = {onDeleteNote(note)}
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        tint = MaterialTheme.colorScheme.error,
                        contentDescription = null
                    )
                }
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