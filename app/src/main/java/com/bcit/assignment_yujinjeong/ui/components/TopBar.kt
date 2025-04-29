package com.bcit.assignment_yujinjeong.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Displaying top bar method.
 * If showBackButton is true, then display back arrow(Going back to previous page), else display
 * home Icon in the top bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    showBackButton: Boolean = false,

    //Callback for buttons
    onBackClick: () -> Unit = {},
) {
    TopAppBar(
        title = { Text(
            text = title,
            modifier = if (!showBackButton) {
                Modifier.fillMaxWidth().wrapContentSize(Alignment.Center)
            } else {
                Modifier
            }
        )},
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        }
    )
}