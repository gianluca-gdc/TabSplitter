package com.gianluca_gdc.tabsplitter.android.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gianluca_gdc.tabsplitter.model.BillDetails
import com.gianluca_gdc.tabsplitter.ui.constants.Color.Black
import com.gianluca_gdc.tabsplitter.android.R

import kotlin.NumberFormatException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillEntryScreen(
    logoPainter: Painter,
    initialBill: BillDetails? = null,
    isAutomaticFlow: Boolean = false,
    onNext: (BillDetails) -> Unit,
    onSettings: () -> Unit,
    onScanReceipt: () -> Unit,
    onClearParsedItems: () -> Unit
) {
    var subtotal by remember { mutableStateOf(initialBill?.subtotal?.toString() ?: "") }
    var tipPercent by remember {
        mutableStateOf(
            if (initialBill != null && isAutomaticFlow) ""
            else initialBill?.tip?.toString() ?: ""
        )
    }
    var taxPercent by remember { mutableStateOf(initialBill?.tax?.toString() ?: "") }


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
                onClick = { onScanReceipt() },
                modifier = Modifier.size(50.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black
                )
            ) {
                Icon(
                    painter = if(isAutomaticFlow) painterResource(id = R.drawable.cam_flow_enabled)
                    else painterResource(id = R.drawable.cam_flow_disabled),
                    contentDescription = "OCR Scan",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(40.dp)
                )
            }
        }


        Spacer(modifier = Modifier.height(5.dp))
        Column(Modifier.fillMaxWidth()
            .padding(top = 18.dp, start = 22.dp, end = 18.dp, bottom = 18.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Subtotal",
                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 20.sp),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = subtotal,
                        onValueChange = { input ->
                            if (input.count { it == '.' } <= 1) {
                                val filtered = input.filter { it.isDigit() || it == '.' }
                                subtotal = filtered
                                if (isAutomaticFlow && filtered.isBlank()) {
                                    onClearParsedItems()
                                }
                            }
                        },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        placeholder = { if (subtotal.isBlank()) Text("0.00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .width(100.dp)
                            .height(56.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Divider(
                modifier = Modifier.fillMaxWidth()
                    .padding(end = 30.dp),
                color = Color.LightGray
            )



            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tax",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontSize = 20.sp
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = taxPercent,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        onValueChange = { input ->
                            if (input.count { it == '.' } <= 1) {
                                val filtered = input.filter { it.isDigit() || it == '.' }
                                taxPercent = filtered
                                if (isAutomaticFlow && filtered.isBlank()) {
                                    onClearParsedItems()
                                }
                            }
                        },
                        placeholder = { if(taxPercent.isBlank())Text("0.00")},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .width(100.dp)
                            .height(56.dp)

                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Divider(
                modifier = Modifier.fillMaxWidth()
                    .padding(end = 30.dp),
                color = Color.LightGray
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Tip ( % )",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontSize = 20.sp
                    )
                )
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (tipPercent.isNotBlank() && subtotal.isNotBlank()) {
                        val tipAmount = try {
                            (tipPercent.toDouble() / 100) * subtotal.toDouble()
                        } catch (e: NumberFormatException) {
                            0.0
                        }
                        Text(
                            text = "$${"%.2f".format(tipAmount)}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }

                    TextField(
                        value = tipPercent,
                        onValueChange = { input ->
                            if (input.count { it == '.' } <= 1 && input.length <= 2) {
                                tipPercent = input.filter { (it.isDigit() || it == '.') }
                            }
                        },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        placeholder = { if(tipPercent.isBlank())Text("0.00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .width(100.dp)
                            .height(56.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            if (taxPercent.isNotBlank() && subtotal.isNotBlank()) {
                val total = try {
                    subtotal.toDouble() + taxPercent.toDouble() + if (tipPercent.isNotBlank()) tipPercent.toDouble() else 0.0
                } catch (e: NumberFormatException) {
                    0.0
                }
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "$${"%.2f".format(total)}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .wrapContentWidth(Alignment.End)
                        .padding(start = 16.dp)
                )
            }
        }
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
        ) { Text("Lets start splittin!") }
    }
}
