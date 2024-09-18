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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Header()
                UsersFullNameInputBoxAndOutput()

                /** Mutable State Values that will need to change as they get passed through
                 * the composables to send them into other composables.
                 *
                 * When you send in the state into a composable and modify it,
                 * you can use the new state to modify the UI*/

                // Semesters
                val semesters = listOf("Fall and Spring", "Fall", "Spring", "Summer")
                val (selectedSemester, onSelectedSemester) = remember { mutableStateOf(semesters[0]) }

                // Living Situations
                val livingSituations =
                    listOf("Living on campus", "Living off campus", "Living with parents")
                val (selectedSituation, onSelectedSituation) = remember {
                    mutableStateOf(
                        livingSituations[0]
                    )
                }

                // Levels of Study
                val levels = listOf(
                    "Doctor of Pharmacy",
                    "Masters in Pharmacy Science",
                    "Undergraduate",
                    "Graduate",
                    "Doctor of Education",
                    "Physician Assistant",
                )
                val level = remember { mutableIntStateOf(0) }

                RadioButtonsSelector(
                    category = "Semesters",
                    values = semesters,
                    selectedValue = selectedSemester,
                    onSelected = onSelectedSemester
                )
                RadioButtonsSelector(
                    category = "Living Situation",
                    values = livingSituations,
                    selectedValue = selectedSituation,
                    onSelected = onSelectedSituation
                )
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Top
                ) {
                    DropDownMenuDemos(
                        category = "Level",
                        menuItems = levels,
                        level,
                    )
                }
                EstimatedCostOfAttendance(
                    semester = selectedSemester,
                    situation = selectedSituation,
                    level = levels[level.intValue]
                )
            }
        }
    }
}

fun priceMapping(level: String, semester: String, livingSituation: String): String {
    val semesters = mapOf(
        "Fall and Spring" to 1.0,
        "Fall" to 0.5,
        "Spring" to 0.5,
        "Summer" to 0.3
    )

    val livingSituations = mapOf(
        "Living on campus" to 10_439,
        "Living off campus" to 16_115,
        "Living with parents" to 4_196
    )

    val levels = mapOf(
        "Doctor of Pharmacy" to 39_759,
        "Masters in Pharmacy Science" to 7_944,
        "Undergraduate" to 25_829,
        "Graduate" to 3_120,
        "Doctor of Education" to 15_276,
        "Physician Assistant" to 27_142
    )

    val levelCost = levels[level] ?: 0
    val semesterCost = semesters[semester] ?: 1.0
    val situationCost = livingSituations[livingSituation] ?: 0
    return "$ ${"%,d".format((levelCost * semesterCost).toInt() + situationCost)}"
}

@Composable
fun EstimatedCostOfAttendance(
    semester: String,
    situation: String,
    level: String
) {

    val costOfAttendance = remember { mutableStateOf("$ ${"__,___"}") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(onClick = {
            costOfAttendance.value =
                priceMapping(level = level, semester = semester, livingSituation = situation)
        }) {
            Text("Submit")
        }

        Text(
            "Estimated Cost Of Attendance: ",
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(8.dp),
            fontWeight = FontWeight.Medium,
            fontSize = 24.sp
        )

        Text(
            text = costOfAttendance.value,
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(8.dp),
            fontWeight = FontWeight.Light,
            fontSize = 24.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EstimatedCostOfAttendancePreview() {
    Column() {
        EstimatedCostOfAttendance("random semester", "some living situation", "SomeTypathing")
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
fun DropDownMenuDemos(
    category: String, menuItems: List<String>,
    selectedOptionIndex: MutableIntState
) {

    val isDropDownExpanded = remember {
        mutableStateOf(false)
    }

    Column(modifier = Modifier.padding(start = 16.dp)) {
        Text(text = category, fontSize = 24.sp, fontWeight = FontWeight.Medium)

        Card(
            border = BorderStroke(width = 1.dp, color = Color.Black),
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        isDropDownExpanded.value = true
                    }
            ) {
                Text(text = menuItems[selectedOptionIndex.intValue])
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
                        selectedOptionIndex.intValue = index
                    })
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DropDownMenuDemoPreview() {
    DropDownMenuDemos(
        category = "Semesters",
        menuItems = listOf("Fall & Spring", "Fall", "Spring", "Summer"),
        remember {
            mutableIntStateOf(0)
        }
    )
}


@Composable
fun RadioButtonsSelector(
    category: String, values: List<String>, selectedValue: String,
    onSelected: (String) -> Unit
) {

    Column(Modifier.padding(16.dp)) {
        Text(text = category, fontSize = 24.sp, fontWeight = FontWeight.Medium)
        Column(modifier = Modifier.selectableGroup()) {
            values.forEach { text ->
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
                                selected = (text == selectedValue),
                                onClick = { onSelected(text) },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = (text == selectedValue), onClick = null)
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
    val values = listOf("Fall and Spring", "Fall", "Spring", "Summer")
    val (selectedOption, onSelect) = remember {
        mutableStateOf(values[0])
    }
    RadioButtonsSelector(
        category = "Semesters",
        values = values,
        selectedValue = selectedOption,
        onSelected = onSelect
    )
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
        .map { it -> it.lowercase().replaceFirstChar { it.titlecase() } }.joinToString(" ")
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
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 40.dp, top = 40.dp, end = 40.dp, bottom = 5.dp),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            "Managing Costs",
            color = Color.White,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(start = 40.dp, top = 0.dp, end = 40.dp, bottom = 0.dp)
        )

        Text(
            "Cost & Tuition Rates",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 40.dp, top = 0.dp, end = 40.dp, bottom = 40.dp),
            color = Color.White,
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
