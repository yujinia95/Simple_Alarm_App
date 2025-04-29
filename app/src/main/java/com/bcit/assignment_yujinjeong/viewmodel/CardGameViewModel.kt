package com.bcit.assignment_yujinjeong.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcit.assignment_yujinjeong.alarm.AlarmManager
import com.bcit.assignment_yujinjeong.data.database.AlarmRepository
import com.bcit.assignment_yujinjeong.data.dataclass.Card
import com.bcit.assignment_yujinjeong.data.dataclass.CardGameUiState
import com.bcit.assignment_yujinjeong.data.imageAPI.ImageRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for card matching game.
 * Responsible for handling game state and game logic.
 * Getting images for card from imageRepository.
 * Also inheriting from ViewModel() to use ViewModel component.
 */
class CardGameViewModel(
    private val imageRepository: ImageRepository,
    private val alarmRepository: AlarmRepository,
    private val alarmManager: AlarmManager
) : ViewModel() {

    //Mutable state obj that has game state. (Update, observe, make it available)
    private val _uiState = MutableStateFlow(CardGameUiState())

    //SateFlow is a read-only version of MutableStateFlow.
    //asStateFlow makes MutableStateFlow to become a read-only.
    val uiState: StateFlow<CardGameUiState> = _uiState.asStateFlow()

    //For tracking current flipped card
    private var firstFlippedCardIndex: Int? = null

    // For storing the current alarm ID
    private var currentAlarmId: Int? = null

    //For catching error in case coroutine fails.
    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        //If error occurs, retries initializeGame.
        viewModelScope.launch {
            initializeGame()
        }
    }

    //This init will run initializeGame() automatically when ViewModel is created.
    init {
        initializeGame()
    }

    /**
     * Set the alarm ID for the current game
     */
    fun setAlarmId(alarmId: Int) {
        currentAlarmId = alarmId
    }

    /**
     * Initializing game (Starting new with loading images and setting up the game state.).
     */
    fun initializeGame() {
        viewModelScope.launch(exceptionHandler) {
            //Change current state isLoading to true.
            _uiState.update { it.copy(isLoading = true) }

            //'withContext(Dispatchers.IO)' switch to IO thread to get images.
            // If doesn't work throw err.
            val imageSources = withContext(Dispatchers.IO) {
                try {
                    //If getting image fail, return null, but instead null return emptyList
                    imageRepository.getImagesForCards().getOrNull() ?: emptyList()
                } catch (error: Exception) {
                    emptyList()
                }
            }

            val limitImageSource = imageSources.take(4)

            //Create pairs and mix it
            val cardPairs = limitImageSource.flatMap { listOf(it, it) }.shuffled()
            //Creating card objs
            val cards = cardPairs.mapIndexed { index, imageSource ->
                Card(
                    cardId = index,
                    imageUrl = imageSource,
                    isFlipped = false,  //False making cards starts face down
                    isMatched = false
                )
            }.toMutableList()

            //Making sure graves 8 cards.
            if (cards.size >= 8) {
                val gameCards = cards.take(8).toMutableList()
                //'it' is pointing current state, and update the state with the prepared cards and
                // reset other values.
                _uiState.update {
                    it.copy(
                        cards = gameCards,
                        isLoading = false,
                        gameCompleted = false,
                        matchesFound = 0
                    )
                }
            }
        }
    }

    /**
     * This method handles clicking card during gameplay.
     */
    fun onCardClicked(cardIndex: Int) {
        val currentState = _uiState.value
        //Error handling if invalid card index, just ignore it.
        if (cardIndex >= currentState.cards.size) return
        //Copying cards to make changes later.
        val cards = currentState.cards.toMutableList()
        //If card is waiting for match(animation) or already matched, just ignore it.
        if (currentState.isWaitingForMatch || cards[cardIndex].isMatched) return

        //Flip card
        cards[cardIndex] = cards[cardIndex].copy(isFlipped = true)
        //Tracking which card is flipped first (For comparing with second flipped card).
        when (firstFlippedCardIndex) {
            //If there is no card flipped yet, set the card as first flipped card and update state.
            null -> {
                firstFlippedCardIndex = cardIndex
                _uiState.update { it.copy(cards = cards) }
            }
            //If user click the same card again, just ignore
            cardIndex -> {
                return
            }
            //Flipping second card and checking if cards are matched by the same images.
            else -> {
                // '!!' meaning "IT'S NOT NULL!!!!!!!!"
                val firstCardIndex = firstFlippedCardIndex!!
                val firstCard = cards[firstCardIndex]
                val secondCard = cards[cardIndex]
                val isMatch = firstCard.imageUrl == secondCard.imageUrl

                if (isMatch) {
                    cards[firstCardIndex] = firstCard.copy(isMatched = true)
                    cards[cardIndex] = secondCard.copy(isMatched = true)

                    val newMatchesFound = currentState.matchesFound + 1
                    val gameCompleted = newMatchesFound >= 4 //Checking if all 4 pairs are found.

                    //Updating current state with new state
                    _uiState.update {
                        it.copy(
                            cards = cards,
                            matchesFound = newMatchesFound,
                            gameCompleted = gameCompleted
                        )
                    }

                    // If game is completed, disable the alarm
                    if (gameCompleted) {
                        disableAlarm()
                    }

                    //Below blocks get triggered if cards don't match.
                } else {
                    _uiState.update {
                        it.copy(
                            cards = cards,
                            isWaitingForMatch = true
                        )
                    }

                    //Giving enough time to flip back for unmatched cards(Making a delay).
                    viewModelScope.launch {
                        //Waiting 1 second.
                        kotlinx.coroutines.delay(1000)
                        val updatedCards = _uiState.value.cards.toMutableList()

                        //Flipped first and second cards back to non-face side.
                        updatedCards[firstCardIndex] =
                            updatedCards[firstCardIndex].copy(isFlipped = false)
                        updatedCards[cardIndex] =
                            updatedCards[cardIndex].copy(isFlipped = false)

                        //This state update preventing user from clicking other cards.
                        _uiState.update {
                            it.copy(
                                cards = updatedCards,
                                isWaitingForMatch = false
                            )
                        }
                    }
                }
                //Making sure reset the first flipped card for next matching.
                firstFlippedCardIndex = null
            }
        }
    }

    /**
     * Disable the current alarm when the game is completed
     */
    private fun disableAlarm() {
        viewModelScope.launch {
            currentAlarmId?.let { id ->
                val alarm = alarmRepository.getAlarmById(id.toLong())
                alarm?.let {
                    val updatedAlarm = it.copy(isEnabled = false)
                    alarmRepository.updateAlarm(updatedAlarm)
                    alarmManager.cancelAlarm(updatedAlarm)
                }
            }
        }
    }
}