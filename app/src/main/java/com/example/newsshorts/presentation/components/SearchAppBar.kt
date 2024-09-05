package com.example.newsshorts.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SearchAppBar(
    modifier: Modifier = Modifier,
    value: String,
    onInputValueChange: (String) -> Unit,
    onSearchIconClicked: () -> Unit,
    onCloseIconClicked: () -> Unit
) {


        TextField(
            modifier = modifier.fillMaxWidth(),
            value = value,
            onValueChange = onInputValueChange,
            textStyle = TextStyle(color = Color.White.copy(alpha = 0.7f)),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search Icon",
                    tint = Color.White.copy(alpha = 0.7f)
                )
            },
            placeholder = {
                Text(
                    text = "Search..",
                    color = Color.White.copy(alpha = 0.7f)
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (value.isNotEmpty()) {
                            onInputValueChange("")
                        } else {
                            onCloseIconClicked()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close Icon",
                        tint = Color.White
                    )
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                onSearchIconClicked()
            }),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                cursorColor = Color.White,
                focusedIndicatorColor = Color.White
            )
        )
    }


@Preview(showBackground = true)
@Composable
fun SearchAppBarPreview() {
    SearchAppBar(
        value = "",
        onInputValueChange = {},
        onSearchIconClicked = {},
        onCloseIconClicked = {}
    )
}
