package com.bcit.assignment_yujinjeong.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bcit.assignment_yujinjeong.data.dataclass.ImageSrc


/**
 * Displaying card for card game.
 * For matched card showing different color of card.
 * This method also makes, back side of card as well.
 */
@Composable
fun FlipCard(
    imageUrl: ImageSrc,
    isFlipped: Boolean,
    isMatched: Boolean,

    //Callback for clicking card
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(100.dp)
            .padding(4.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            //For matched cards, change the colour.
            containerColor = if (isMatched) {
                MaterialTheme.colorScheme.tertiary
            } else {
                MaterialTheme.colorScheme.primaryContainer
            }
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isFlipped) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    //Used coil for loading image for loading images asynchronously.
                    // (NO blocking main thread). Good for memory management as well.
                    when {
                        imageUrl.url != null -> {
                            AsyncImage(
                                model = imageUrl.url,
                                contentDescription = "Card Image",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(4.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                        imageUrl.resourceId != null -> {
                            AsyncImage(
                                model = imageUrl.resourceId,  // Use the resource ID
                                contentDescription = "Card Image",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(4.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                        else -> {
                            // Fallback if neither is available
                            Icon(
                                imageVector = Icons.Default.QuestionMark,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            } else {
                // For card back side
                Icon(
                    imageVector = Icons.Default.QuestionMark,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}


/**
 * Popup msg for letting user know that alarm is deactivated after matching all pairs.
 */
@Composable
fun GameCompletedDialog(

    //Callback for clicking button (Closing popup).
    onDismiss: () -> Unit
) {
    //Pop up message
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Alarm Deactivated!")},
        text = {Text("You've successfully matched all cards!! Now wakey wakey")},
        confirmButton = {

            Button(onClick = onDismiss) {
                Text("Okie")

            }
        }
    )
}