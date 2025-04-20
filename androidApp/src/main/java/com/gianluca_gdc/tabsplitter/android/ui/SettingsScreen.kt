import android.media.Image
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.content.MediaType.Companion.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gianluca_gdc.tabsplitter.android.R
import com.gianluca_gdc.tabsplitter.ui.constants.Color.Black

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    payerName: String,
    onPayerNameChange: (String) -> Unit,
    venmoHandle: String,
    onVenmoChange: (String) -> Unit,
    cashAppHandle: String,
    onCashAppChange: (String) -> Unit,
    zelleInfo: String,
    onZelleChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Your Payment Info", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = payerName,
            onValueChange = onPayerNameChange,
            label = { Text("Your Name (will be added to people list)") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.Black
            )
        )
        Spacer(modifier = Modifier.height(30.dp))
        Text("Add your wallet options: ", style = MaterialTheme.typography.titleMedium.copy(
            fontSize = 18.sp
        ))
        Spacer(Modifier.height(12.dp))
        Image(
            painter = painterResource(id = R.drawable.applepay),
            contentDescription = "Apple Pay",
            modifier = Modifier.size(70.dp)
        )
        Text("iPhone Users: Apple Pay setup automatically.",
            style = MaterialTheme.typography.bodyLarge )
        Spacer(Modifier.height(5.dp))
        Text("Android Users: Get an iPhone lol. ",
            style = MaterialTheme.typography.bodyLarge)

        Spacer(Modifier.height(5.dp))
        Image(
            painter = painterResource(id = R.drawable.venmo),
            contentDescription = "Venmo",
            modifier = Modifier.size(90.dp)
                .clip(RectangleShape).padding(bottom = 0.dp)
        )
        OutlinedTextField(venmoHandle,
            onValueChange = onVenmoChange,
            label = { Text("Venmo Username (without @)") },
            modifier = Modifier.fillMaxWidth()
                .offset(y = -20.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.Black
            )

        )
        Spacer(Modifier.height(10.dp))
        Image(
            painter = painterResource(id = R.drawable.zelle),
            contentDescription = "Zelle",
            modifier = Modifier.size(70.dp)
                .clip(RectangleShape)
                .offset(y = -20.dp)
        )
        OutlinedTextField(zelleInfo,
            onValueChange = onZelleChange,
            label = { Text("Zelle (email or phone)") },
            modifier = Modifier.fillMaxWidth()
                .offset(y = -20.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.Black
            )

        )
        Spacer(Modifier.height(10.dp))
        Image(
            painter = painterResource(id = R.drawable.cashapp),
            contentDescription = "Cashapp",
            modifier = Modifier.size(150.dp)
                .clip(RectangleShape)
                .offset(y = -60.dp)
        )
        OutlinedTextField(cashAppHandle,
            onValueChange = onCashAppChange,
            label = { Text("Cash App Tag (without $)") },
            modifier = Modifier.fillMaxWidth().offset(y = -100.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.Black
            )

        )


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

        }
    }
}