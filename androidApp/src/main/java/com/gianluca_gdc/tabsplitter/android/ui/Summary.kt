package com.gianluca_gdc.tabsplitter.android.ui

import android.media.Image
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color.Black),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Split that Tab!", style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color.White
            ),
            modifier= Modifier.padding(16.dp)
        )
        Spacer(Modifier.width(10.dp))
        Image(
            painter = painterResource(id = R.drawable.split_bar_tab_bold_icon),
            contentDescription = "Tab Icon",
            modifier = Modifier.size(55.dp)
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(top = 75.dp)
    ) {


        Text(
            text = "Tab Summary:",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp
            )
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
                    Row(Modifier.padding(start = 25.dp, bottom = 4.dp, top = 10.dp)) {
                        Text("${item.name.replaceFirstChar { it.uppercase() }} x${item.quantity}", fontSize = 12.sp)
                        Text("  $${"%.2f".format(item.price)}", fontSize = 12.sp)
                    }
                }
                Row(modifier = Modifier.padding(top = 5.dp)) {
                    Text(
                        text = "Total: $",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(start = 15.dp)
                    )
                    Text(

                        text = "${"%.2f".format(person.total)}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF79e64f)
                        ),
                        modifier = Modifier.padding(start = 2.dp)
                    )
                    Spacer(Modifier.width(70.dp))
                    if(person.phoneNumber.isNotEmpty()){
                        Text(
                            text = "Phone :",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(start = 15.dp)
                        )
                        Text(
                            text = "${person.phoneNumber}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4287f5)
                            ),
                            modifier = Modifier.padding(start = 4.dp)
                        )

                    }

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
            Button(
                onClick = { onBack(people) },
                modifier = Modifier
                    .width(150.dp)
                    .padding(end = 10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(Black),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Back")
            }

            Button(
                onClick = { onNext(people) },
                modifier = Modifier.width(300.dp),
                enabled = true,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonColor(
                        Color.Black, Color.Gray,
                        boolean = true
                    ),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Split")
            }
        }
    }
}
