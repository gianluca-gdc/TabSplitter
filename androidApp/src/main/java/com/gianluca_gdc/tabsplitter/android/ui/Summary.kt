package com.gianluca_gdc.tabsplitter.android.ui

import android.media.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.content.MediaType.Companion.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gianluca_gdc.tabsplitter.android.R
import com.gianluca_gdc.tabsplitter.model.Person
import com.gianluca_gdc.tabsplitter.ui.constants.Color.Black

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Summary(
    people: List<Person>,
    subtotal: Double,
    tip: Double,
    tax: Double,
    payerName:String,
    onBack: (List<Person>) -> Unit,
    onNext: (List<Person>) -> Unit) {
    val extraCost = ((tip / 100) * subtotal + tax) / people.size
    println(extraCost)


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(top = 25.dp)
    ) {


        Text(
            text = "Tab Summary:",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(start = 0.dp)
        )
        Spacer(Modifier.height(15.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            val orderedPeople = buildList {
                val payer = people.find { it.name == payerName }
                if (payer != null) add(payer)
                addAll(people.filter { it.name != payerName })
            }

            items(orderedPeople) { person ->
                person.total += extraCost
                Text(
                    text = if(person == orderedPeople[0]) "★ ${person.name}" else person.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    modifier = Modifier.padding(start = 15.dp)
                )
                person.items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 25.dp, end = 16.dp, bottom = 4.dp, top = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "${item.name.replace(Regex("^[\\d\\W_]+"), "").replaceFirstChar { it.uppercase() }}   x${item.quantity}",
                            fontSize = 12.sp
                        )
                        Text(
                            "$${"%.2f".format(item.price)}",
                            fontSize = 12.sp
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 15.dp, top = 10.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Total: ",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "$${"%.2f".format(person.total)}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF79e64f)
                        ),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Divider(
                    thickness = 2.dp,
                    color = Color.LightGray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = {onBack(people)},
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Text("Back")
            }

            Button(
                onClick = { onNext(people) },
                enabled = true,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonColor(
                        Color.Black, Color.Gray,
                        boolean = true
                    ),
                    contentColor = Color.White
                )
            ) {
                Text("Split")
            }
        }
    }
}
