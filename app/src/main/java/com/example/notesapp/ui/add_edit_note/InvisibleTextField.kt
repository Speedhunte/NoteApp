package com.example.notesapp.ui.add_edit_note

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
fun InvisibleTextField(
    modifier: Modifier= Modifier,
    value: String,
    onValueChange: (String)-> Unit,
    isHintVisible: Boolean,
    placeholder: String,
    singleLine: Boolean = false,
    textStyle: TextStyle
){
    Box{
        BasicTextField(
            modifier = modifier,
            value = value,
            onValueChange = onValueChange,
            textStyle = textStyle,
            singleLine = singleLine
        )
        if(isHintVisible){
            Text(
                modifier = modifier,
                text =placeholder,
                style = textStyle,
                color = Color.Black.copy(alpha = 0.5f)
            )
        }
    }


}