package com.bcit.lab9yujinjeong.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcit.lab9yujinjeong.data.dataclass.CardGameUiState
import com.bcit.lab9yujinjeong.data.imageAPI.ImageRepository
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
class CardGameViewModel(private val imageRepository: ImageRepository) : ViewModel() {

    //Mutable state obj that has game state. (Update, observe, make it available)
    private val _uiState = MutableStateFlow(CardGameUiState())

    //SateFlow is a read-only version of MutableStateFlow.
    //asStateFlow makes MutableStateFlow to become a read-only.
    val uiState: StateFlow<CardGameUiState> = _uiState.asStateFlow()

    //For tracking current flipped card
    private var firstFlippedCardIndex: Int? = null

    //For catching error in case coroutine fails.
    private val exceptionHandler = CoroutineExceptionHandler {_, _ ->

        //If error occurs, retries initializeGame.
        viewModelScope.launch {
            initializeGame()
        }
    }

    //This init will run initializeGame() automatically when ViewModel is created.
    init {
        initializeGame()
    }

    //Initializing game (Starting new game).
    fun initializeGame() {

        viewModelScope.launch(exceptionHandler) {

            //Change current state isLoading to true.
            _uiState.update {it.copy(isLoading = true)}

            //'withContext(Dispatchers.IO)' switch to IO thread to get images.
            // If doesn't work throw err.
            val imageSources = withContext(Dispatchers.IO) {

                try {

                    //If getting image fail, return null, but instead null return emptyList
                    imageRepository.getImagesForCards().getOrNull() ?: emptyList()

                } catch (error: Exception){
                    emptyList()
                }
            }

            val limitImageSource = imageSources.take(4)
            //Create pairs and mix it
            val cardPairs = limitImageSource.flatMap{listOf(it, it)}.shuffled()

            val cards = cardPairs.mapIndexed {index, imageSource ->
                Card(

                )
            }
        }
    }
}