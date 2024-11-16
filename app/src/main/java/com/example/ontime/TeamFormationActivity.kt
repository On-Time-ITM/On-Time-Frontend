package com.example.ontime

// 또는
import AppBar
import CustomButton
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ontime.ui.theme.MainColor
import com.example.ontime.ui.theme.OnTimeTheme
import com.example.ontime.ui.theme.shadow
import com.example.ontime.ui.theme.surfaceContainerLowest

class TeamFormationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OnTimeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier.padding(innerPadding)
                    ) {}
                }
            }
        }
    }
}

@Composable
fun TeamFormation() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceContainerLowest)
    ) {
        AppBar()

        Text(
            "Make a new Team",
            fontSize = 25.sp,
            color = shadow,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)
        )
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            TeamFormationInputs()

            CustomButton(
                text = "Make a team",
                onClick = { /* Handle team creation */ },
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

    }
}

@Composable
private fun TeamFormationInputs() {
    val inputFields = listOf(
        InputField("Enter the schedule title", Icons.Filled.Edit),
        InputField("Select friends to join the schedule", Icons.Filled.Person),
        InputField("Set the location", Icons.Filled.LocationOn),
        InputField("Set the Bank Account", R.drawable.baseline_monetization_on_24)
    )

    inputFields.forEach { field ->
        InputRow(
            text = field.label,
            icon = field.icon
        )
//        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .shadow(1.dp), // 위아래 여백 조정
            thickness = 0.5.dp, // 선의 두께
            color = Color.LightGray // 선의 색상
        )
    }

    // Date and Time Row
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.DateRange,
            contentDescription = null,
            tint = shadow,
            modifier = Modifier
                .size(32.dp)
                .padding(end = 12.dp)
        )
        DateTimeInput(
            modifier = Modifier.weight(2f),
            text = "2024/10/25",
            icon = Icons.Filled.DateRange
        )
        DateTimeInput(
            modifier = Modifier.weight(1f),
            text = "10:00",
            icon = Icons.Filled.DateRange
        )
    }
    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
private fun InputRow(
    text: String,
    icon: Any
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (icon is ImageVector) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = shadow,
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 12.dp)
            )
        } else if (icon is Int) {  // R.drawable 리소스 ID
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = shadow,
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 12.dp)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(5.dp),
            colors = CardDefaults.cardColors(Color(0x1FE9E9E9))
        ) {
            Text(
                text = text,
                color = shadow,
                fontSize = 16.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun DateTimeInput(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(Color(0x1FE9E9E9))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = text,
                color = shadow,
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

private data class InputField(
    val label: String,
    val icon: Any
)

@Preview(showBackground = true)
@Composable
fun GreetingPreview4() {
    OnTimeTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding)
            ) { TeamFormation() }
        }
    }
}