package com.example.notesapp.ui.main_screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SortRadioButton(
    currentOrderType: OrderType,
    buttonOrderType: OrderType,
    onClick: (OrderType)-> Unit,
    buttonName: String
){
    Row(verticalAlignment = Alignment.CenterVertically){
        RadioButton(
            selected = currentOrderType ==buttonOrderType,
            onClick= {onClick(buttonOrderType   )},

        )
        Text(
            modifier = Modifier.padding(8.dp),
            text= buttonName)
    }
}