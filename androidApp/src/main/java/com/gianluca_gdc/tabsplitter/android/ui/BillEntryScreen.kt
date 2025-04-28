package com.gianluca_gdc.tabsplitter.ui


import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDirection.Companion.Content
import androidx.compose.ui.unit.dp
import com.gianluca_gdc.tabsplitter.model.BillDetails
import com.gianluca_gdc.tabsplitter.ui.constants.Color.Black



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillEntryScreen(logoPainter: Painter, onNext: (BillDetails) -> Unit,onSettings: () -> Unit) {
    var subtotal by remember { mutableStateOf("") }
    var tipPercent by remember { mutableStateOf("") }
    var taxPercent by remember { mutableStateOf("") }


    Column(modifier = Modifier.padding(24.dp)) {
        Row(Modifier
            .padding(3.dp)
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.End){
            Icon(
                modifier = Modifier
                    .clickable { onSettings() }
                    .size(40.dp),
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.Black // or any color
            )
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = logoPainter,
                contentDescription = "Tabsplitter Logo",
                colorFilter = ColorFilter.tint(Color(Black)),
                modifier = Modifier
                    .height(200.dp)
                    .align(alignment = Alignment.CenterHorizontally)


            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Enter Bill Details", style = MaterialTheme.typography.titleLarge)
            IconButton(
                onClick = { /* TODO: implement OCR scan */ },
                modifier = Modifier.size(40.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black
                )
            ) {
                Icon(
                    imageVector = Icons.Default.,
                    contentDescription = "OCR Scan"
                )
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = subtotal,
            onValueChange = { input ->
                if (input.count { it == '.' } <= 1) {
                    subtotal = input.filter { it.isDigit() || it == '.' }
                }
            },
            label = { Text("Subtotal") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.Black
            ),
            modifier = Modifier.fillMaxWidth()

        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = tipPercent,
            onValueChange = { input ->
                if (input.count { it == '.' } <= 1) {
                    tipPercent = input.filter { it.isDigit() || it == '.' }
                }
            },
            label = { Text("Tip %") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.Black
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = taxPercent,
            onValueChange = { input ->
                if (input.count { it == '.' } <= 1) {
                    taxPercent = input.filter { it.isDigit() || it == '.' }
                }
            },
            label = { Text("Tax %") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.Black
            ),
            modifier = Modifier.fillMaxWidth()

        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            enabled = subtotal.isNotBlank() && tipPercent.isNotBlank() && taxPercent.isNotBlank(),
            onClick = {
                val billDetails = BillDetails(
                    subtotal = subtotal.toDouble(),
                    tip = tipPercent.toDouble(),
                    tax = taxPercent.toDouble()
                )
                onNext(billDetails)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(Black),
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Next") }
    }
}
