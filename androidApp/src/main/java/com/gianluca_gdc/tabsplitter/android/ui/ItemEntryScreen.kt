package com.gianluca_gdc.tabsplitter.android.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import com.gianluca_gdc.tabsplitter.android.R
import com.gianluca_gdc.tabsplitter.model.AssignedItem
import com.gianluca_gdc.tabsplitter.model.Person
import com.gianluca_gdc.tabsplitter.ui.constants.Color.Black
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.items
@Composable
fun ButtonColor(targetValue: Color, secondTargetValue: Color, boolean: Boolean ): Color {
     val buttonColor by animateColorAsState(
    targetValue = if (boolean) targetValue else secondTargetValue,
    animationSpec = tween(300)
    )
    return buttonColor
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemEntryScreen(
   people: SnapshotStateList<Person>,
    payerName: String,
    onBack: () -> Unit,
    onNext: (List<Person>) -> Unit
) {
    // 🧩 State: itemName, itemPrice, selectedPeople, list of all added items
    val itemName = remember { mutableStateOf("") }
    val itemPrice = remember { mutableStateOf("") }
    val selectedPeople = remember { mutableStateMapOf<String, Int>() }
    val peopleWithItems = remember { people }
    val itemNameFocusRequester = remember { FocusRequester() }
    val trigger = remember { mutableStateOf(0) }
    // 🔠 Item name field ***
    // 💵 Item price field ***
    // 👥 Dropdown or Chips to select people for the item
    // ➕ Add item button
    // 📝 List of added items + assigned people
    // 🔙 Back button + 🔜 Next button
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color.Black),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Add Items", style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color.White
            ),
            modifier = Modifier.padding(16.dp)

        )
        Image(
            painter = painterResource(id = R.drawable.fork_knife_plus_vector), // replace with actual file name
            contentDescription = "People Icon",
            modifier = Modifier
                .size(45.dp)
                .padding(start = 10.dp)
        )
    }
    Spacer(Modifier.height(70.dp))
    Column(
            modifier = Modifier.fillMaxWidth()
            .padding(16.dp)
                .padding(top = 70.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 0.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = itemName.value,
                onValueChange = {
                    if (it.all { ch -> (ch.isLetter() || ch.isWhitespace()) && it.length <= 25 }) {
                        itemName.value = it
                    }
                },
                label = { Text("Item Name") },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color.Black
                ),
                modifier = Modifier.weight(1f)
                    .focusRequester(itemNameFocusRequester),
                maxLines = 1
            )
            Spacer(Modifier.width(16.dp))
            OutlinedTextField(
                value = itemPrice.value,
                onValueChange = {
                    if (it.count { ch -> ch == '.' } <= 1
                        && it.all { ch -> ch.isDigit() || ch == '.' }
                        && it.length <= 7) {
                        itemPrice.value = it
                    }
                },
                label = { Text("Price") },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                maxLines = 1,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color.Black
                ),
                modifier = Modifier.width(100.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Menu(people = people, selectedPeople = selectedPeople, payerName = payerName)

            Button(
                onClick = {
                    val totalQuantity = selectedPeople.values.sum()
                    val unitPrice = itemPrice.value.toDouble() / totalQuantity
                    selectedPeople.forEach { (name, assignedQty) ->
                        val person = peopleWithItems.find { it.name == name } ?: return@forEach
                        val actualPrice = unitPrice * assignedQty
                        person.items.add(AssignedItem(itemName.value, actualPrice, assignedQty))
                        person.total += actualPrice // Update the person's total
                    }
                    itemName.value = ""
                    itemPrice.value = ""
                    selectedPeople.clear()
                    itemNameFocusRequester.requestFocus()
                    trigger.value++
                },
                modifier = Modifier.width(100.dp).height(50.dp),
                enabled = itemName.value.isNotBlank()
                        && itemPrice.value.isNotBlank()
                        && selectedPeople.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonColor(Color.Black, Color.Gray, itemName.value.isNotBlank()
                            && itemPrice.value.isNotBlank()
                            && selectedPeople.isNotEmpty()),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp) // Ensure rounded corners
            ) {
                Text("Add")
            }
        }
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 100.dp)
        ) {
            val dummy = trigger.value
            val orderedPeople = buildList {
                val payer = people.find { it.name == payerName }
                if (payer != null) add(payer)
                addAll(people.filter { it.name != payerName })
            }

            orderedPeople.forEach { person ->
                item {
                    Text(
                        text = if(person == orderedPeople[0]) "★ ${person.name}" else person.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                    )
                }

                items(person.items) { item ->
                    Row(Modifier.padding(start = 8.dp, bottom = 2.dp)) {
                        Text("${item.name.replaceFirstChar { it.uppercase() }} x${item.quantity}", fontSize = 12.sp)
                        Text("  $${"%.2f".format(item.price)}", fontSize = 12.sp)
                    }
                }

                item {
                    Divider(
                        thickness = 1.dp,
                        color = Color.LightGray,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { onBack() },
                modifier = Modifier
                    .width(150.dp)
                    .padding(end = 10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(Black),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp) // Ensure rounded corners
            ) {
                Text("Back")
            }

            Button(
                onClick = {
                    val distinctPeople = peopleWithItems.distinctBy { it.name }
                    onNext(distinctPeople)
                },
                modifier = Modifier.width(300.dp),
                enabled = peopleWithItems.all { it.items.isNotEmpty() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonColor(Color.Black,Color.Gray,
                        boolean = peopleWithItems.all { it.items.isNotEmpty() }),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp) // Ensure rounded corners
            ) {
                Text("Next")
            }
        }
    }
}

@Composable
fun Menu(people: SnapshotStateList<Person>, selectedPeople: MutableMap<String, Int>, payerName:String) {
    val expanded = remember { mutableStateOf(false) }

    Column(modifier = Modifier.width(260.dp)) {
        OutlinedButton(
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            onClick = { expanded.value = !expanded.value },
            border = if (selectedPeople.isEmpty()) BorderStroke(1.dp, Color.Black)
            else BorderStroke(width = 0.dp, Color.White),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.Black,
                containerColor = ButtonColor(Color.LightGray, Color(0xFF79e64f), selectedPeople.isEmpty())),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = if (selectedPeople.isEmpty()) "Select People" else "Selected ✓",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 20.sp,
                    fontWeight = if (selectedPeople.isEmpty()) FontWeight.Normal else FontWeight.ExtraBold),
                color = if (selectedPeople.isEmpty()) Color.Black else Color.White

            )
        }

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
            modifier = Modifier
                .width(260.dp)
                .background(color = Color.White)
        ) {
            val orderedPeople = buildList {
                val payer = people.find { it.name == payerName }
                if (payer != null) add(payer)
                addAll(people.filter { it.name != payerName })
            }

            orderedPeople.forEach { person ->
                val qty = selectedPeople[person.name] ?: 0
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .padding(horizontal = 8.dp) // Optional padding for spacing
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(if(person == orderedPeople[0]) "★ ${person.name}" else person.name, fontWeight = if (qty > 0) FontWeight.Bold else FontWeight.Normal)
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (qty > 0) Color.Black else Color.Gray,
                                    contentColor = if (qty > 0) Color.White else Color.Black
                                ),
                                shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp),
                                onClick = {
                                    if (qty > 0) selectedPeople[person.name] = qty - 1
                                    if (selectedPeople[person.name] == 0) selectedPeople.remove(person.name)
                                },
                                modifier = Modifier.size(30.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("-", fontSize = 14.sp)
                            }
                            Text("  x$qty  ", fontSize = 12.sp)
                            Button(
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Black,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp),
                                onClick = {
                                    selectedPeople[person.name] = qty + 1
                                },
                                modifier = Modifier.size(30.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("+", fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

