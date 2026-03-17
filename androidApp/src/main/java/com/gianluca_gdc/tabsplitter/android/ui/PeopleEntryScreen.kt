package com.gianluca_gdc.tabsplitter.android.ui


import android.Manifest
import android.annotation.SuppressLint
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
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.lang.Thread.sleep
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.platform.LocalContext
import com.gianluca_gdc.tabsplitter.model.Person
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalFocusManager
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults.shape
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.ripple
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import com.gianluca_gdc.tabsplitter.android.datastore.loadPeople
import com.gianluca_gdc.tabsplitter.android.datastore.savePerson
import androidx.compose.runtime.collectAsState
import com.gianluca_gdc.tabsplitter.android.ui.ButtonColor

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
    var searchInput by remember { mutableStateOf("") }
    var expanded by remember {mutableStateOf(false)}
    val coroutineScope = rememberCoroutineScope()
    // collecting contacts as state
    val savedFriends by loadPeople(context).collectAsState(initial = emptyMap())
    val focusManager = LocalFocusManager.current
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
                                    if(people.size < 10) { people[trimmed] = phone}
                                    coroutineScope.launch {
                                        savePerson(context = context, name = trimmed, phone = phone)
                                    }
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
            .height(80.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Add Friends", style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,

            ),
            modifier = Modifier.padding(start = 15.dp, top = 25.dp)
        )

    }
    Column(
        modifier = Modifier
            .padding(start = 16.dp, bottom = 0.dp, end = 16.dp, top = 5.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }
    ) {


        Spacer(modifier = Modifier.height(70.dp))

        OutlinedTextField(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth()
                .border(color = Color.Transparent, width = 0.dp)
                .onKeyEvent { event ->
                    if (event.key == Key.Enter && event.type == KeyEventType.KeyDown) {
                        focusManager.clearFocus()
                        true
                    } else {
                        false
                    }
                },
            value = searchInput,
            onValueChange = { searchInput = it },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFf5f5f5),
                focusedContainerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(30.dp),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search ,
                    contentDescription = "Search"
                )
            }
        )

        Spacer(modifier = Modifier.height(7.dp))
        Box(modifier = Modifier.fillMaxWidth()
            .padding(top = 4.dp),
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

        Spacer(modifier = Modifier.height(5.dp))
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
            ){

            Text("Recent Friends",
                style = MaterialTheme.typography.headlineLarge)

            OutlinedButton(
                border = null,
                onClick = {
                    val permission = Manifest.permission.READ_CONTACTS
                    if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(context, arrayOf(permission), 0)
                    } else {
                        contactPickerLauncher.launch(null)
                    }
                },
                contentPadding = PaddingValues(10.dp),
                modifier = Modifier
                    .indication(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(
                            color = Color.White
                        )
                    ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Black,
                    containerColor = Color(0xFF97b2e8),
                )
            )  {
                Text("Add Contact",
                    Modifier.padding(bottom = 0.dp),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    ))
            }
        }

            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.heightIn(10.dp, 300.dp)
            ) {

                val entries = if (searchInput.isNotBlank()) {
                    savedFriends.filter { (key, _) ->
                        key.contains(searchInput, ignoreCase = true)
                    }.entries.toList()
                } else {
                    savedFriends.entries.toList()
                }

                items(
                    items = entries,
                    key = { it.key } // use the friend name as a stable key
                ) { (name, phone) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                focusManager.clearFocus()
                                searchInput = ""
                                if (people.size < 10)
                                    people[name] = phone
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = phone,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

            }



        Spacer(Modifier.height(10.dp))
        Divider(
            thickness = 2.dp,
            color = Color.LightGray,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        FlowRow(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            people.forEach {
                FilterChip(modifier = Modifier.padding(3.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFedd3d1)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    selected = true,
                    onClick = {
                        searchInput = ""
                        focusManager.clearFocus()
                        people.entries.remove(it) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "delete"
                        )
                }, label = { Text("${it.key}") })
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
                onClick = { people[payerName] = ""
                    onNext(people) },
                enabled = people.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonColor(Color(Black),Color.Gray,people.isNotEmpty()),
                    contentColor = Color.White
                )
            ) {
                Text("Next")
            }
        }
    }
}