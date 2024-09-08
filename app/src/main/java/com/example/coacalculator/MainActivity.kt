package com.example.coacalculator

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.coacalculator.sampledata.Student
import com.example.coacalculator.ui.theme.Purple40

const val LOG_TAG = "MAD"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val ahmed = Student("Ahmed El-Desoky", 140, 3.95)
            Log.d(LOG_TAG,"The Student Ahmed -> $ahmed")
            Header()
        }
    }
}

@Composable
fun Header(modifier: Modifier = Modifier) {
    print("In Header")
    Column(
        modifier
            .fillMaxWidth()
            .background(color = Purple40)
    ) {
        Text(
            "Managing Costs",
            color = Color.Black,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(start = 40.dp, top = 40.dp, end = 40.dp, bottom = 0.dp)
        )

        Text(
            "Cost & Tuition Rates",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 40.dp, top = 0.dp, end = 40.dp, bottom = 40.dp)
        )
    }
}

@Preview(
    showBackground = true,
    name = "HeaderPreviewLightMode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "HeaderPreviewDarkMode"
)
@Composable
fun HeaderPreview() {
    Header()
}