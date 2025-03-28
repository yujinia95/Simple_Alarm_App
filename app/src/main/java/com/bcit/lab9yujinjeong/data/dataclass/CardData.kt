package com.bcit.lab9yujinjeong.data.dataclass

//For card frame
data class Card(
    val cardId: Int,
    val imageUrl: String,
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false
)

//This data class for pair of matching cards.
data class CardPair(
    val imageUrl: String,
    val firstPairId: Int,
    val SecondPairId: Int
)

