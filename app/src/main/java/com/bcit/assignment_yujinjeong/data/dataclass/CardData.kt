package com.bcit.assignment_yujinjeong.data.dataclass

//For card frame
data class Card(
    val cardId: Int,
    val imageUrl: ImageSrc,
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false
)

//This data class for completing UI state for the card game.
data class CardGameUiState(
    val cards: List<Card>           = emptyList(),
    val isLoading: Boolean          = false,
    val isWaitingForMatch: Boolean  = false,
    val matchesFound: Int           = 0,
    val gameCompleted: Boolean      = false
)