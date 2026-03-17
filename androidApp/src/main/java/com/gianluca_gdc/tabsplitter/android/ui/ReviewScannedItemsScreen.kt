package com.gianluca_gdc.tabsplitter.android.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gianluca_gdc.tabsplitter.model.BillItem
import com.gianluca_gdc.tabsplitter.model.AssignedItem
import androidx.compose.material3.ButtonDefaults
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Column
import com.gianluca_gdc.tabsplitter.ui.constants.Color.Black
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import com.gianluca_gdc.tabsplitter.model.distribute
import androidx.compose.ui.text.style.TextOverflow


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ReviewScannedItemsScreen(
    items: List<BillItem>,
    people: List<String>,
    onConfirm: (List<AssignedItem>) -> Unit,
    onBack: () -> Unit
) {
    val updatedItems = remember { items.map { it.copy() }.toMutableStateList() }
    // Track per-item, per-person quantities
    val assignments = remember { mutableStateMapOf<Pair<BillItem, String>, Int>() }
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFFAFAFB))) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color(0xFFFAFAFB))
        ) {
            Text(
                "Scanned Items", style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.padding(top= 12.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                items(updatedItems) { item ->
                    // strip leading numbers and punctuation from scanned names
                    val displayName = item.name.replaceFirst(Regex("^\\d+[\\.\\)]?\\s*"), "")
                    var expanded by remember { mutableStateOf(false) }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxSize()
                            .shadow(
                                elevation = 5.dp,
                                shape = RoundedCornerShape(10.dp),
                                clip = false   // leave clip=false so the shadow extends beyond the shape
                            )
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .border(
                                width = 2.dp,
                                color = Color(0xFFFAFAFB),
                                shape = RoundedCornerShape(10.dp)
                            )

                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(15.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = displayName,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 8.dp),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "$${"%.2f".format(item.price)}",
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                                FlowRow(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalArrangement = Arrangement.spacedBy(5.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    people.forEach { person ->
                                        FilterChip(
                                            selected = assignments[item to person]?.let { it > 0 } ?: false,
                                            onClick = {
                                                val current = assignments[item to person] ?: 0
                                                val next = if (current >= 10) 0 else current + 1
                                                assignments[item to person] = next
                                            },
                                            border = BorderStroke(
                                                1.dp,
                                                Color.Transparent
                                            ),
                                            modifier = Modifier
                                                .padding(horizontal = 4.dp)
                                                .pointerInput(Unit) {
                                                    detectTapGestures(
                                                        onLongPress = {
                                                            // clear quantity on long press
                                                            assignments[item to person] = 0
                                                        }
                                                    )
                                                },
                                            label = {
                                                val qty = assignments[item to person] ?: 0
                                                Text(if (qty <= 1) person else "$person x$qty")
                                            },
                                            shape = RoundedCornerShape(20.dp),
                                            colors = FilterChipDefaults.filterChipColors(
                                                containerColor = Color(0xFFF3F3F3),
                                                selectedContainerColor = Color(0xFFC8DAC3)

                                            )
                                        )
                                    }
                                }

                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }

            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onBack,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text("Back")
                }
                Button(
                    enabled = ((people.all { person ->
                        assignments.entries.any { (key, qty) ->
                            key.second == person && qty > 0
                        }
                    }) &&
                            updatedItems.all { item ->
                                assignments.keys.any { (assignedItem, _) -> assignedItem == item }
                            }
                            ),
                    onClick = {
                        val result = mutableListOf<AssignedItem>()
                        updatedItems.forEach { item ->
                            val totalQty = assignments.entries
                                .filter { it.key.first == item }
                                .sumOf { it.value }
                            if (totalQty > 0) {
                                assignments.entries
                                    .filter { it.key.first == item && it.value > 0 }
                                    .forEach { (key, qty) ->
                                        val share = item.price * qty.toDouble() / totalQty.toDouble()
                                        result.add(
                                            AssignedItem(
                                                name = item.name,
                                                price = share,
                                                quantity = qty,
                                                assignedTo = key.second
                                            )
                                        )
                                    }
                            }
                        }
                        onConfirm(result)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonColor(MaterialTheme.colorScheme.primary,Color.LightGray,((people.all { person ->
                            assignments.entries.any { (key, qty) ->
                                key.second == person && qty > 0
                            }
                        }) &&
                                updatedItems.all { item ->
                                    assignments.keys.any { (assignedItem, _) -> assignedItem == item }
                                }
                                )),
                        contentColor = MaterialTheme.colorScheme.onPrimary

                    )
                ) {
                    Text("Continue")
                }
            }
        }
    }
}