package com.dyor.bump_android

import BumpCalculator
import Golfer
import Hole
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dyor.bump_android.ui.theme.BumpandroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var showMatrix by remember { mutableStateOf(false) }
            val golfers = remember { mutableStateListOf<Golfer>() }

            var holes = remember {
                mutableStateListOf<Hole>(
                    Hole(1, 6),
                    Hole(2, 8),
                    Hole(3, 18),
                    Hole(4, 10),
                    Hole(5, 14),
                    Hole(6, 12),
                    Hole(7, 4),
                    Hole(8, 16),
                    Hole(9, 2),
                    Hole(10, 11),
                    Hole(11, 7),
                    Hole(12, 17),
                    Hole(13, 1),
                    Hole(14, 13),
                    Hole(15, 9),
                    Hole(16, 5),
                    Hole(17, 15),
                    Hole(18, 3)
                )
            }
            if (!showMatrix) {
                GolferInputScreen(golfers, holes) {
                    showMatrix = true
                }
            } else {
                BumpMatrixScreen(golfers, holes) {
                    showMatrix = false
                }
            }
        }
    }
}

//@OptIn(ExperimentalResourceApi::class)
@Composable
fun GolferInputScreen(golfers: MutableList<Golfer>, holes: MutableList<Hole>, onCalculateBumps: () -> Unit) {
    var golferName by remember { mutableStateOf("") }
    var golferBumps by remember { mutableStateOf("") }
    var holeDifficultiesInput by remember { mutableStateOf("") }
    // Get the keyboard controller
    val keyboardController = LocalSoftwareKeyboardController.current


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp) // Apply padding to the entire Column
    ) {
        Spacer(Modifier.height(16.dp)) // Add a top spacer for better spacing

        TextField(
            value = golferName,
            onValueChange = { golferName = it },
            label = { Text("Golfer Name") },
            modifier = Modifier.fillMaxWidth(), // Make TextField fill the width
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
        )

        TextField(
            value = golferBumps,
            onValueChange = { golferBumps = it },
            label = { Text("Bumps") },
            modifier = Modifier.fillMaxWidth(), // Make TextField fill the width
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Button(
            onClick = {
                if (golferBumps=="")
                {
                    golferBumps="0"
                }
                if (golferName=="") {
                    golferName = "Golfer"
                }
                golfers.add(Golfer(golferName, golferBumps.toInt(), 0))
                golferName = ""
                golferBumps = ""
                // Hide the keyboard
                keyboardController?.hide()
            },
            modifier = Modifier.fillMaxWidth() // Make Button fill the width
        ) {
            Text("Add Golfer")
        }


        DraggableGolfersList(golfers)

        Button(
            onClick = onCalculateBumps,
            modifier = Modifier.fillMaxWidth() // Make Button fill the width
        ) {
            Text("Calculate Bumps")
        }
        Image(
            painter = painterResource(R.drawable.app_icon_1024),
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )


        Spacer(Modifier.height(32.dp)) // Add spacing between elements


        // Section for hole difficulties input
        var holeDifficultiesInput by remember { mutableStateOf(holes.joinToString(", ") { it.difficulty.toString() }) }

        TextField(
            value = holeDifficultiesInput,
            onValueChange = { holeDifficultiesInput = it },
            label = { Text("Current Hole Difficulties (e.g., '6, 8, 18...')") },
            modifier = Modifier.fillMaxWidth() // Make TextField fill the width
        )
        Button(
            onClick = {
                val difficulties = holeDifficultiesInput.split(",").mapNotNull { it.trim().toIntOrNull() }
                updateHoleDifficulties(holes, difficulties)
            },
            modifier = Modifier.fillMaxWidth() // Make Button fill the width
        ) {
            Text("Update Hole Difficulty")
        }

//        // Show a list of holes and their difficulties
//        LazyColumn {
//            items(holes) { hole ->
//                Text("Hole ${hole.number}: ${hole.difficulty}")
//            }
//        }
    }
}
@Composable
fun DraggableGolfersList(golfers: MutableList<Golfer>) {
    val scrollState = rememberScrollState()
    var draggedIndex by remember { mutableStateOf(-1) }

    Box() {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
        ) {
            golfers.forEachIndexed { index, golfer ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${golfer.name} (${golfer.bumps} bumps)",
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
//            .background(Color.Green)
                    )
//up
                    if (index > 0) {
                        IconButton(onClick = {
                            val removed = golfers.removeAt(index)
                            golfers.add(index - 1, removed)
                        }) {
                            Text("☝\uFE0F")
                        }
                    }
//                   else {
//                        Spacer(modifier = Modifier.width(24.dp))
//                    }
                    else {
                        IconButton(onClick = {
                        }) {
                            Text(" ")
                        }
                    }
//down
                    if (index < golfers.size - 1) {
                        IconButton(onClick = {
                            if (index < golfers.size - 1) {
                                val removed = golfers.removeAt(index)
                                golfers.add(index + 1, removed)
                            }
                        }) {
                            Text("\uD83D\uDC4E")
                        }
                    }
                    else {
                        IconButton(onClick = {
                        }) {
                            Text(" ")
                        }
                    }
                    //delete
                    IconButton(onClick = {
                        golfers.removeAt(index)
                    }) {
                        Text("\uD83D\uDDD1\uFE0F")
                    }


                }
            }
        }
    }
}





//@OptIn(ExperimentalResourceApi::class)
@Composable
fun BumpMatrixScreen(golfers: List<Golfer>, holes: List<Hole>, onBack: () -> Unit) {
    // Handle the system back button press
    //xxx BackHandler(onBack = onBack)
    // Calculate the bumps matrix
    val bumpCalculator = BumpCalculator()
    val bumpMatrix = bumpCalculator.calculateBumps(golfers, holes)
    //Log.d("tag", "$bumpMatrix")

    // Display the matrix
    //add column headers for the name of each golfer

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }

        // Display a row with the first cell labeled 'hole' and another cell for the name of each golfer
        // This row will be made more prominent
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray) // Example of making the first row more prominent
                .padding(8.dp)
        ) {
            Text(
                "Hole",
                modifier = Modifier.weight(1f),
//                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp) // Increase font size to make it more prominent
            )
            golfers.forEach { golfer ->
                Text(
                    text = "${golfer.name}:${golfer.bumps}",
                    //style = MaterialTheme.typography.bodyMedium, // Original style commented out, adjust as needed
                    modifier = Modifier.weight(1f),
//                    fontSize = 16.sp // Slightly larger text for emphasis
                )

            }
        }

        LazyColumn {
            itemsIndexed(holes.sortedBy { it.number }) { index, hole ->
                val clickCounts = remember { mutableStateMapOf<Golfer, Int>() }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "${hole.number} (${hole.difficulty}): ",
                        //style = MaterialTheme.typography.body1, // Style can be adjusted as needed
                        modifier = Modifier.weight(1f)
                            .background(if (index % 2 == 1) Color.LightGray else Color.Transparent)
                    )
                    // for each golfer in golfers write a 1 if they get a bump on this hole, 0 if not
                    golfers.forEach { golfer ->
                        val getsBump = bumpMatrix[golfer.name]?.contains(hole.number) ?: false
                        var bumpText = if (getsBump) "YES" else "-"
                        if (golfer.bumps > 18) {
                            bumpText = if (getsBump) "YESx2" else "YES"
                        }
//                        val clickCount = remember { mutableStateOf(0) }

                        val isClicked = remember { mutableStateOf(false) }

                        Text(
                            text = bumpText,
                            //style = MaterialTheme.typography.body1, // Style can be adjusted as needed
                            modifier = Modifier.weight(1f)
                                .background(if (index % 2 == 1) Color.LightGray else Color.Transparent)
                                .background(if (isClicked.value) Color.hsl(168F, 0.97F, 0.42F) else if (index % 2 == 1) Color.LightGray else Color.Transparent)
                                .clickable {
                                    isClicked.value = !isClicked.value
                                    if (isClicked.value) {
                                        golfer.wins += 1
                                    }
                                    else {
                                        golfer.wins -= 1
                                    }


                                } // Change state when clicked
                        )
                        if (isClicked.value) {
                            Text(text = (golfer.wins).toString(), modifier = Modifier.background(
                                Color.hsl(168F, 0.97F, 0.42F) ))
                        }
                    }
                }
            }
        }
        Image(
            painter = painterResource(R.drawable.app_icon_1024),
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

fun updateHoleDifficulties(holes: MutableList<Hole>, difficulties: List<Int>) {
    difficulties.forEachIndexed { index, difficulty ->
        if(index < holes.size) {
            holes[index] = holes[index].copy(difficulty = difficulty)
        }
    }
}
