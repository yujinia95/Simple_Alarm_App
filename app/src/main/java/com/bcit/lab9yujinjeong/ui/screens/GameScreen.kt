package com.bcit.lab9yujinjeong.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MemoryCardGrid(cards: List<Card>, onCardClick: (Card) -> Unit) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.weight(1f)
    ) {
        items(cards) { card ->
            MemoryCardItem {
                card = card,
                onCardClick = {onCardClick(card)}
            }
        }
    }
}