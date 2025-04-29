package com.bcit.assignment_yujinjeong.ui.screens

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bcit.assignment_yujinjeong.di.cardGameViewModel
import com.bcit.assignment_yujinjeong.ui.components.GameCompletedDialog
import com.bcit.assignment_yujinjeong.ui.components.TopBar
import android.provider.Settings
import android.util.Log
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import com.bcit.assignment_yujinjeong.alarm.AlarmReceiver
import com.bcit.assignment_yujinjeong.alarm.AlarmService
import com.bcit.assignment_yujinjeong.data.dataclass.Card
import com.bcit.assignment_yujinjeong.data.dataclass.CardGameUiState
import com.bcit.assignment_yujinjeong.ui.components.FlipCard
import com.bcit.assignment_yujinjeong.viewmodel.CardGameViewModel

/**
 * Creating screen for memory card matching game to stop alarm.
 */
@Composable
fun CardGameScreen(
    alarmId: Int,
    onGameComplete: () -> Unit,
    // Use our custom extension function instead of hiltViewModel()
    viewModel: CardGameViewModel = cardGameViewModel()
) {
    // Set the alarm ID
    LaunchedEffect(alarmId) {
        viewModel.setAlarmId(alarmId)
    }

    val uiState by viewModel.uiState.collectAsState()

    //This state is for alarm sound.
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    //This state is for completing card game
    var showCompletionDialog by remember { mutableStateOf(false) }

    //Effects for alarm sound and game state
    //Why handling both at the same time? because alarm sounds turns off after game complete.
    HandleSoundEffects(

        isGameCompleted = uiState.gameCompleted,
        onSoundInitialized = { player -> mediaPlayer = player },

        onGameComplete = {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            showCompletionDialog = true
        }
    )

    //Making sure cleaning up media player when leaving game screen.
    DisposableEffect(Unit) {
        onDispose{
            try {
                // Safely stop and release mediaPlayer
                mediaPlayer?.apply {
                    if (isPlaying) {
                        stop()
                    }
                    release()
                }
                mediaPlayer = null
            } catch (e: Exception) {
                Log.e("CardGameScreen", "Error stopping media player", e)
            }
        }
    }

    Scaffold(
        topBar = {
            TopBar(title = "Match all pairs to turn off the alarm!")
        }
    ) { paddingValues ->
        GameContent(
            paddingValues = paddingValues,
            uiState = uiState,
            onCardClicked = viewModel::onCardClicked,
            onResetGame = viewModel::initializeGame
        )

        //After game ends, displaying completion dialog.
        if (showCompletionDialog) {
            val context = LocalContext.current

            GameCompletedDialog(
                //After dialog display, gotta launch onGameComplete function
                onDismiss = {
                    showCompletionDialog = false

                    try {
                        AlarmService.stopService(context)

                        AlarmReceiver.stopAlarm(context)
                    } catch (e: Exception) {
                        Log.e("CardGameScreen", "Error stopping alarm", e)
                    }

                    onGameComplete()
                }
            )
        }
    }
}

/**
 * Initializes alarm sound on launch, stops it when game completes, and cleans up.
 *
 */
@Composable
private fun HandleSoundEffects(
    isGameCompleted: Boolean,
    onSoundInitialized: (MediaPlayer) -> Unit,
    onGameComplete: () -> Unit
) {

    //Allowing access to sys resources and media player.
    val context = LocalContext.current

    //Start playing alarm sound when game screen opens
    LaunchedEffect(Unit) {

        try {

            //Getting default alarm sound from kotlin.(Configure first)
            val player = MediaPlayer().apply {
                setAudioAttributes(

                    AudioAttributes.Builder()
                        //Sonification set up.(No speech, no-music sound)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        //Marking as alarm sound.
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                )
                //Setting sound src.
                setDataSource(context, Settings.System.DEFAULT_ALARM_ALERT_URI)

                //Key lines for continue the alarm sound until the game ends.
                isLooping = true
                prepare()
                start()
            }

            onSoundInitialized(player)

        } catch (e: Exception) {
            Log.e("CardGameScreen", "Error playing alarm", e)

            //This is for fallback if first alarm sound failed in case.
            try {
                val player = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .build()
                    )
                    setDataSource(context, Settings.System.DEFAULT_NOTIFICATION_URI)
                    isLooping = true
                    prepare()
                    start()
                }
                onSoundInitialized(player)
            } catch (e: Exception) {
                Log.e("CardGameScreen", "Error playing fallback notification sound", e)
            }
        }
    }

    //Stopping alarm and display completion dialog only if user completely matches the cards
    LaunchedEffect(isGameCompleted) {
        if (isGameCompleted) {
            onGameComplete()
        }
    }
}


/**
 * This method is displaying card with grid 3x3.
 */
@Composable
private fun CardGrid(
    cards: List<Card>,
    onCardClicked: (Int) -> Unit
) {
    if(cards.isNotEmpty() && cards.size >= 8) {

        //First row
        CardRow(
            cards = cards.subList(0,3),
            startIndex =0,
            onCardClicked = onCardClicked
        )

        Spacer(modifier = Modifier.height(16.dp))

        //Second row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement  = Arrangement.SpaceEvenly
        ) {
            FlipCard(
                imageUrl = cards[3].imageUrl,
                isFlipped = cards[3].isFlipped,
                isMatched = cards[3].isMatched,
                onClick = {onCardClicked(3)}
            )

            //Since taking only 4 pairs(3x3 grid) making empty space in the middle.
            Box(
                modifier = Modifier
                    .size(100.dp)
            )

            FlipCard(
                imageUrl = cards[4].imageUrl,
                isFlipped = cards[4].isFlipped,
                isMatched = cards[4].isMatched,
                onClick = { onCardClicked(4) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        //Third Row
        CardRow(
            cards = cards.subList(5, 8),
            startIndex = 5,
            onCardClicked = onCardClicked
        )
    }
}


/**
 * Helper function displaying row of three cards.
 */
@Composable
private fun CardRow(
    cards: List<Card>,
    startIndex: Int,
    onCardClicked: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        //forEachIndexed loop giving card FlipCard composable for using properties.
        cards.forEachIndexed {
                index, card ->
            FlipCard(
                imageUrl = card.imageUrl,
                isFlipped = card.isFlipped,
                isMatched = card.isMatched,
                onClick = { onCardClicked(startIndex + index)}
            )
        }
    }
}

/**
 * helper function to display game instructions and reset button.
 */
@Composable
private fun GameInstructions(
    onResetGame: () -> Unit
) {
    Spacer(modifier = Modifier.height(32.dp))

    Text(
        text = "Match all pairs to turn off the alarm!",
        style = MaterialTheme.typography.bodyLarge
    )

    Spacer(modifier = Modifier.height(16.dp))

    Button(onClick = onResetGame) {
        Text("Reset Game")
    }
}

/**
 * Game content holding card grid and control for the game.
 */
@Composable
private fun GameContent(
    paddingValues: PaddingValues,
    uiState: CardGameUiState,
    onCardClicked: (Int) -> Unit,
    onResetGame: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        //In case taking some time to card, placing loading sign.
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                LinearProgressIndicator(
                    progress = {uiState.matchesFound / 4f},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                )

                //Card grid
                CardGrid(
                    cards = uiState.cards,
                    onCardClicked = onCardClicked
                )

                GameInstructions(onResetGame = onResetGame)
            }
        }
    }
}






















