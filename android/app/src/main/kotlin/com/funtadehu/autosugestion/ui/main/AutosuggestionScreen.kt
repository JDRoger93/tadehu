package com.funtadehu.autosugestion.ui.main

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funtadehu.autosugestion.R
import com.funtadehu.autosugestion.data.AutosuggestionPreferences
import com.funtadehu.autosugestion.scheduler.NotificationScheduler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutosuggestionScreen() {
    val context = LocalContext.current
    val prefs = remember { AutosuggestionPreferences(context) }

    var isRestMode by remember { mutableStateOf(prefs.isRestMode()) }
    var phrases by remember { mutableStateOf(prefs.getPhrases()) }
    var currentQuote by remember { mutableStateOf(prefs.getLastQuote()) }
    var selectedInterval by remember { mutableIntStateOf(prefs.getIntervalMinutes()) }

    var showAddDialog by remember { mutableStateOf(false) }
    var newPhraseText by remember { mutableStateOf("") }

    val bgGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0E2448), Color(0xFF081120))
    )

    Scaffold(
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGradient)
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header with Logo
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .border(3.dp, Color(0xFF38BDF8), CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_funtadehu),
                                contentDescription = "FunTaDeHu Logo",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "FunTaDeHu",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Autosugestión • Para Ser Eficaces y Felices",
                            fontSize = 13.sp,
                            color = Color(0xFF38BDF8),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Status Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xCC10203C)
                        ),
                        shape = RoundedCornerShape(18.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                color = if (isRestMode) Color(0x33F59E0B) else Color(0x3310B981),
                                shape = RoundedCornerShape(20.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isRestMode) Color(0xFFF59E0B) else Color(0xFF10B981)
                                )
                            ) {
                                Text(
                                    text = if (isRestMode) "⏸️ MODO DESCANSO (PAUSADO)" else "🟢 SISTEMA ACTIVO",
                                    color = if (isRestMode) Color(0xFFFBBF24) else Color(0xFF34D399),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isRestMode)
                                    "Las notificaciones y el envío de frases están desactivadas para que descanses."
                                else
                                    "El sistema está activo. Recibirás frases aleatorias de autosugestión durante el día.",
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Main Action Buttons
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Modo Descanso Button
                            Button(
                                onClick = {
                                    prefs.setRestMode(true)
                                    isRestMode = true
                                    NotificationScheduler.cancel(context)
                                    Toast.makeText(context, "⏸️ Modo Descanso activado", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(54.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFD97706)
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("⏸️ Modo Descanso", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            // Reactivar Button
                            Button(
                                onClick = {
                                    prefs.setRestMode(false)
                                    isRestMode = false
                                    NotificationScheduler.scheduleNext(context)
                                    Toast.makeText(context, "▶️ Reactivado. Recibirás frases.", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(54.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF059669)
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("▶️ Reactivar", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }

                        // Agregar Frase Button
                        Button(
                            onClick = {
                                newPhraseText = ""
                                showAddDialog = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2563EB)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("➕ Agregar Frase de Autosugestión", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        // Probar Notificación Ahora Button
                        OutlinedButton(
                            onClick = {
                                val random = prefs.getRandomPhrase()
                                currentQuote = random
                                NotificationScheduler.showNotification(context, random)
                                Toast.makeText(context, "🔔 Notificación enviada", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x44FFFFFF))
                        ) {
                            Text("🔔 Probar Notificación Ahora", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }
                }

                // Current Quote Preview Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0x661E3A8A)),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x4438BDF8))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "✨ FRASE ACTUAL EN MENTE:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "\"$currentQuote\"",
                                fontSize = 15.sp,
                                fontStyle = FontStyle.Italic,
                                color = Color.White,
                                lineHeight = 21.sp
                            )
                        }
                    }
                }

                // Phrases Section Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📚 Lista de Frases (${phrases.size})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        // Quick Interval selection
                        Text(
                            text = "Frecuencia: ~$selectedInterval min",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                // Phrases List Items
                itemsIndexed(phrases) { index, phrase ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0x22FFFFFF)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = phrase,
                                color = Color(0xFFE2E8F0),
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1f),
                                lineHeight = 18.sp
                            )
                            if (phrases.size > 1) {
                                IconButton(
                                    onClick = {
                                        prefs.deletePhrase(index)
                                        phrases = prefs.getPhrases()
                                        Toast.makeText(context, "Frase eliminada", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Text("🗑️", fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }

    // Add Phrase Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = Color(0xFF0F1F38),
            title = {
                Text(
                    text = "Nueva Frase de Autosugestión",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Escribe la frase que deseas que la app te envíe aleatoriamente:",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newPhraseText,
                        onValueChange = { newPhraseText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ej: Me siento sereno, enfocado y capaz...") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF38BDF8),
                            unfocusedBorderColor = Color(0x44FFFFFF),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val text = newPhraseText.trim()
                        if (text.isNotBlank()) {
                            prefs.addPhrase(text)
                            phrases = prefs.getPhrases()
                            currentQuote = text
                            showAddDialog = false
                            Toast.makeText(context, "✅ ¡Frase agregada con éxito!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Text("Guardar Frase", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar", color = Color(0xFF94A3B8))
                }
            }
        )
    }
}

