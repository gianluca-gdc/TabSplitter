package com.gianluca_gdc.tabsplitter.android.ui


import android.app.Activity
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.indication
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.focusModifier
import com.gianluca_gdc.tabsplitter.ui.constants.Color.Black
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import com.gianluca_gdc.tabsplitter.android.R
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import java.lang.Thread.sleep
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.platform.LocalContext
import com.gianluca_gdc.tabsplitter.model.Person
import androidx.compose.runtime.LaunchedEffect
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import androidx.compose.runtime.mutableStateMapOf
import androidx.core.content.ContextCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeopleEntryScreen(
    onNext: (Map<String,String>) -> Unit,
    payerName:String,
    onBack: () -> Unit
) {
    var nameInput by remember { mutableStateOf("") }
    val people = remember { mutableStateMapOf<String, String>() }
    var alreadyEntered by remember { mutableStateOf(false) }
    var limitReached by remember { mutableStateOf(false) }
    val context = LocalContext.current as Activity

    val contactPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) { uri: Uri? ->
        try {
            println("Picked URI: $uri")
            if (uri != null) {
                val contactId = uri.lastPathSegment?.toLongOrNull()
                println("Resolved contact ID: $contactId")
                if (contactId != null) {
                    val phoneCursor = context.contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        arrayOf(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                        ),
                        "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                        arrayOf(contactId.toString()),
                        null
                    )

                    phoneCursor?.use {
                        if (it.moveToFirst()) {
                            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                            val phoneIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                            if (nameIndex != -1 && phoneIndex != -1) {
                                val name = it.getString(nameIndex) ?: "Unnamed"
                                val phone = it.getString(phoneIndex) ?: "No number"
                                println("Resolved Name: $name, Phone: $phone")

                                val trimmed = name.trim().replaceFirstChar { c -> c.uppercase() }

                                if (trimmed.isNotBlank() && !people.contains(trimmed)) {
                                    people[trimmed] = phone
                                    nameInput = ""
                                    alreadyEntered = false
                                } else if (people.contains(trimmed)) {
                                    alreadyEntered = true
                                } else if (people.size >= 10) {
                                    limitReached = true
                                }
                            } else {
                                println("Missing nameIndex or phoneIndex in cursor")
                            }
                        } else {
                            println("Cursor returned no rows")
                        }
                    } ?: println("phoneCursor was null")
                } else {
                    println("Failed to parse contact ID from URI")
                }
            } else {
                println("URI was null")
            }
        } catch (e: Exception) {
            println("Error reading contact: ${e.localizedMessage}")
            e.printStackTrace()
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color.Black),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Add Friends", style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color.White,

            ),
            modifier = Modifier.padding(16.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.person_plus_icon_vector), // replace with actual file name
            contentDescription = "People Icon",
            modifier = Modifier
                .size(45.dp)
                .padding(start = 5.dp, top = 0.dp)
        )
    }
    Column(modifier = Modifier.padding(start = 16.dp, bottom = 0.dp, end = 16.dp, top = 16.dp)) {


        Spacer(modifier = Modifier.height(70.dp))

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = { Text("Name") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        val name = nameInput.trim().capitalize()
                        if (name.isNotBlank() && !people.containsKey(name)) {
                            people[name] = ""
                            nameInput = ""
                            alreadyEntered = false
                        } else if (people.contains(nameInput.trim().capitalize())) {
                            alreadyEntered = true
                        }else if (people.size >= 10){
                            limitReached = true
                        }
                    }
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color.Black
                ),
                maxLines = 1

            )
            Button(
                onClick = {
                    val name = nameInput.trim().capitalize()
                    if (name.isNotBlank() && !people.containsKey(name)) {
                        people[name] = ""
                        nameInput = ""
                        alreadyEntered = false
                    } else if (people.contains(nameInput.trim().capitalize())) {
                        alreadyEntered = true
                    }else if (people.size >= 10){
                        limitReached = true
                    }
                },
                modifier = Modifier
                    .height(70.dp)
                    .width(50.dp) // square button for icon-like size
                    .padding(top = 10.dp, bottom = 10.dp),
                contentPadding = PaddingValues(0.dp), // optional: remove default padding
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(Black),
                    contentColor = Color.White
                )
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(7.dp))
        Box(modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center){
            Text(
                if(alreadyEntered) {
                    "Name already entered"
                } else {
                    "Limit of 10 names already added"
                },
                color = Red,
                modifier = Modifier
                    .alpha(
                        if (alreadyEntered || limitReached) {
                            1F
                        } else 0F
                    )
                    .offset(y = -10.dp),

                style = MaterialTheme.typography.bodyMedium,

                )

            }
        OutlinedButton(
            onClick = {
                val permission = android.Manifest.permission.READ_CONTACTS
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(context, arrayOf(permission), 0)
                } else {
                    contactPickerLauncher.launch(null)
                }
            },
            contentPadding = PaddingValues(10.dp),
            modifier = Modifier.align(Alignment.CenterHorizontally)
                .height(40.dp)
                .width(170.dp)
                .indication(
                    interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(color = Color.Black) // <-- Customize here
        ),
            colors = ButtonDefaults.outlinedButtonColors( // optional, if you want to change background or content
                contentColor = Color.Black
            )
        )  {
            Text("Import Contact +",
                Modifier.padding(bottom = 0.dp),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E90FF),
                    fontSize = 16.sp
                ))
        }
        Spacer(modifier = Modifier.height(5.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "★ $payerName - YOU",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .weight(4f)
                    .padding(vertical = 15.dp)
                    .padding(start = 10.dp)
            )
        }
        people.keys.forEachIndexed { index, person ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = person,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .weight(4f)
                        .padding(vertical = 15.dp)
                        .padding(start = 10.dp)
                )
                IconButton(onClick = { people.remove(person)
                    limitReached = false}) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))


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
                )
            ) {
                Text("Back")
            }

            Button(
                onClick = { people[payerName] = ""
                    onNext(people) },
                modifier = Modifier.width(300.dp),
                enabled = people.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(Black),
                    contentColor = Color.White
                )
            ) {
                Text("Next")
            }
        }
    }
}