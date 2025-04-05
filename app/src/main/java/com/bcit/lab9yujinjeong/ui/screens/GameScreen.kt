package com.bcit.lab9yujinjeong.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bcit.lab9yujinjeong.ui.navigation.Screen
import com.bcit.lab9yujinjeong.viewmodel.CardGameViewModel

/**
 * Creating screen for card game.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardGameScreen(
    alarmId: Int,
    onGameComplete: () -> Unit,

    viewModel: CardGameViewModel = hiltViewModel()
) {

}