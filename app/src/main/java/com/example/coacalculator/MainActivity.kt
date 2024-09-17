@file:Suppress("IMPLICIT_CAST_TO_ANY")

package com.example.coacalculator

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coacalculator.sampledata.Student
import com.example.coacalculator.ui.theme.Purple40
import java.time.LocalDate
import java.time.LocalDate.parse
import kotlin.math.round
import kotlin.random.Random
import kotlin.streams.asSequence

const val LOG_TAG = "MAD"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
                Header()
                UsersFullNameInputBoxAndOutput()
            }
        }
    }
}

/**
 * I need some time to digest this as it is a lot.
 * What I do know is that we encapsulate the logic in a box.
 * We need a row to hold our selected option.
 * After the row, we concern ourselves with our DropDownMenu logic.
 * A DropDown meny has two inputs (expanded and on Dismiss Request)
 * Expanded should take a state
 * OnDismissRequest should change that state.
 * Within the DropDownMenu we use a consumer that prints out a DropDownMenuItem
 * To the app. We also implement the onClick logic for these items as well.
 */

@Composable
fun DropDownMenuDemos(menuItems: List<String>) {

    val isDropDownExpanded = remember {
        mutableStateOf(false)
    }

    val selectedOption = remember {
        mutableStateOf(0)
    }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Card(border = BorderStroke(width = 1.dp, color = Color.Black)) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp).clickable {
                    isDropDownExpanded.value = true
                }
            ) {
                Text(text = menuItems[selectedOption.value])
                Image(
                    painter = painterResource(id = R.drawable.dropdown_icon),
                    contentDescription = "dropdown icon"
                )
            }
            DropdownMenu(
                expanded = isDropDownExpanded.value,
                onDismissRequest = { isDropDownExpanded.value = false }) {
                menuItems.forEachIndexed { index, info ->
                    DropdownMenuItem(text = { Text(info) }, onClick = {
                        isDropDownExpanded.value = false
                        selectedOption.value = index })
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DropDownMenuDemoPreview() {
    DropDownMenuDemos(menuItems = listOf("Fall & Spring", "Fall", "Spring", "Summer"))
}


@Composable
fun SemesterSelectorRadioButtons() {
    val semesters = listOf("Both Fall and Spring", "Fall", "Spring", "Summer")
    val (selectedSemester, onSemesterSelected) = remember { mutableStateOf(semesters[0]) }

    Column(Modifier.padding(16.dp)) {
        Text(text = "Semesters", fontSize = 24.sp, fontWeight = FontWeight.Medium)
        Column(modifier = Modifier.selectableGroup()) {
            semesters.forEach { text ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    shape = RoundedCornerShape(0.dp),
                )
                {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(45.dp)
                            .selectable(
                                selected = (text == selectedSemester),
                                onClick = { onSemesterSelected(text) },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = (text == selectedSemester), onClick = null)
                        Text(
                            text = text, modifier = Modifier.padding(start = 16.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SemesterSelectorPreview() {
    SemesterSelectorRadioButtons()
}

@Composable
fun UsersFullNameInputBoxAndOutput() {
    var text by remember { mutableStateOf("") }

    var dynamicTextOutput by remember { mutableStateOf("Waiting for text input...") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Full Name") },
                modifier = Modifier.padding(8.dp),
            )
            Button(
                onClick = {
                    dynamicTextOutput =
                        if (text.isNotBlank()) "Welcome to the XULA cost of attendance calculator $text"
                        else "Please provide text as input."
                },
                shape = RoundedCornerShape(10.dp),
            ) {
                Text("Submit")
            }
        }

        Text(
            text = dynamicTextOutput,
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserFullNameInputBoxAndOutputPreview() {
    UsersFullNameInputBoxAndOutput()
}

/**
 * First function I've made in Kotlin that actually uses streams!!!!
 * I use a stream to capitalize the first letter of each word. using replaceFirstChar
 *
 * I even used LocalDate which I used during my internship
 *
 * What is interesting is how LocalDate is a Java class but it is usable by Kotlin.
 * I know that both languages are interoperable but never tried using more specific Java
 * classes explicitly.
 * */
@Composable
fun nameAndAge(name: String, date: String): String {
    val titleCaseString = name.split(" ").stream().asSequence()
        .map { it.lowercase().replaceFirstChar { it.titlecase() } }.joinToString(" ")
    val dateReceived = parse(date)
    val year = LocalDate.now().minusDays(dateReceived.dayOfMonth.toLong())
        .minusMonths(dateReceived.monthValue.toLong()).minusYears(dateReceived.year.toLong()).year

    // Now return the string
    return "$titleCaseString is $year years old. "
}

@Composable
@Preview(showBackground = true)
fun NameAndAgePreview() {
    Text(text = nameAndAge(name = "xAVier uNiversity of lOuisiana", date = "1925-10-06"))
}

@Composable
fun Header(modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxWidth()
            .background(color = Purple40)
    ) {

        Text(
            text = nameAndAge(name = "xAVier uNiversity of lOuisiana", date = "1925-10-06"),
            color = Color.Black,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 40.dp, top = 40.dp, end = 40.dp, bottom = 5.dp),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            "Managing Costs",
            color = Color.Black,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(start = 40.dp, top = 0.dp, end = 40.dp, bottom = 0.dp)
        )

        Text(
            "Cost & Tuition Rates",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 40.dp, top = 0.dp, end = 40.dp, bottom = 40.dp)
        )
    }
}

@Preview(
    showBackground = true, name = "HeaderPreviewLightMode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES, name = "HeaderPreviewDarkMode"
)
@Composable
fun HeaderPreview() {
    Header()
}

@Composable
fun listOfStudentsWithRandomProperties(size: Int = 3): List<Student> {
    return List(size) { index ->
        Student(
            "Student ${index + 1}",
            creditHours = Random.nextLong(0, 121),
            // I know this doesn't print out 4.0 as 4.0 is exclusive in this case
            gpa = round(Random.nextDouble(0.0, 4.0) * 100) / 100
        )
    }
}


@Composable
fun StudentWithHighestGpaText(size: Int = 3) {
    val randomStudentsList = listOfStudentsWithRandomProperties(size)
    val studentWithHighestGPA = randomStudentsList.stream().reduce { highestGpaHolder, student ->
        // Even though it is not specified, we update highestGpaHolder here
        if (student.gpa > highestGpaHolder.gpa) student else highestGpaHolder
    }

    Column {
        Text(text = "The list of students is:", style = MaterialTheme.typography.titleSmall)
        randomStudentsList.forEach { student ->
            Text(
                student.toString(),
                modifier = Modifier.padding(8.dp)
            )
        }
        Text(
            "The student object with the highest GPA is: ${
                if (studentWithHighestGPA.isPresent)
                    studentWithHighestGPA.get() else "No Students"
            }. ", modifier = Modifier.padding(8.dp),
            fontWeight = FontWeight.SemiBold
        )
    }
    Log.d(LOG_TAG, "Student with highest GPA: $studentWithHighestGPA")
}

@Composable
@Preview(showBackground = true)
fun PreviewStudentWithHighestGPA() {
    StudentWithHighestGpaText()
}

@Composable
fun StudentsWithHighGpas(numberOfStudents: Int = 200) {
    val listOfStudentsWithHighGpas = listOfStudentsWithRandomProperties(numberOfStudents)
        .filter { student -> student.gpa > 3.5 }.sortedByDescending { it.gpa }
    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            "The ${listOfStudentsWithHighGpas.size} students " +
                    "with a GPA greater than 3.5 are: ",
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.headlineSmall
        )
        listOfStudentsWithHighGpas.forEach { student ->
            Text(
                "\t${student.gpa} \t ${student.name}",
                modifier = Modifier.padding(8.dp),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewStudentsWithHighGpas() {
    StudentsWithHighGpas()
}
