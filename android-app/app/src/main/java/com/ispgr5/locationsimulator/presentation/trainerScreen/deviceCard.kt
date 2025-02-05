package com.ispgr5.locationsimulator.presentation.trainerScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape

@Composable
fun DeviceCard(
    userName: String,
    deviceName: String,
    activity: String,
    isOnline: Boolean,
    onPlayClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFDAD5)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = userName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = deviceName, fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = activity, fontWeight = FontWeight.Bold, fontSize = 16.sp)

                // Buttons ausrichten
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onPlayClick, modifier = Modifier.padding(start = 8.dp)) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play")
                    }
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            }

            // Online-/Offline-Indikator
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(if (isOnline) Color.Green else Color.Red, CircleShape)
                    .align(Alignment.TopEnd)
            )
        }
    }
}
