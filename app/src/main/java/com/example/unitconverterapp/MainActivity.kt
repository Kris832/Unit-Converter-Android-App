package com.example.unitconverterapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unitconverterapp.ui.theme.UnitConverterAppTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UnitConverterAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UnitConverter()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UnitConverter() {

    var inputValue by remember { mutableStateOf("") }
    var outputValue by remember { mutableStateOf("") }
    var inputUnit by remember { mutableStateOf("Meters") }
    var outputUnit by remember { mutableStateOf("Meters") }
    var inputExpanded by remember { mutableStateOf(false) }
    var outputExpanded by remember { mutableStateOf(false) }
    val inputConversionFactor = remember { mutableStateOf(1.00) }
    val outputConversionFactor = remember { mutableStateOf(1.00) }

    val unitCategories = mapOf(
        "Length" to listOf(
            "Centimeters" to 0.01,
            "Meters" to 1.00,
            "Feet" to 0.3048,
            "Millimeters" to 0.001,
            "Kilometers" to 1000.0,
            "Miles" to 1609.34
        ),
        "Weight" to listOf(
            "Grams" to 0.001,
            "Kilograms" to 1.0,
            "Pounds" to 0.4536
        ),
        "Temperature" to listOf(
            "Celsius" to 1.0,
            "Fahrenheit" to 1.0
        )
    )

    var selectedCategory by remember { mutableStateOf("Length") }
    selectedCategory = unitCategories.entries.firstOrNull { category ->
        category.value.any { it.first == inputUnit }
    }?.key ?: "Length"

    val validOutputUnits = unitCategories[selectedCategory] ?: emptyList()

    fun convertUnits() {
        val inputValueDouble = inputValue.toDoubleOrNull() ?: 0.0

        outputValue = when (selectedCategory) {
            "Temperature" -> {
                if (inputUnit == "Celsius" && outputUnit == "Fahrenheit") {
                    ((inputValueDouble * 9 / 5) + 32).toString()
                } else if (inputUnit == "Fahrenheit" && outputUnit == "Celsius") {
                    ((inputValueDouble - 32) * 5 / 9).toString()
                } else {
                    "Invalid Conversion"
                }
            }
            else -> {
                ((inputValueDouble * inputConversionFactor.value * 100 / outputConversionFactor.value)
                    .roundToInt() / 100).toString()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Unit Converter",
            style = androidx.compose.ui.text.TextStyle(
                fontFamily = FontFamily.Cursive,
                fontSize = 30.sp,
                color = Color.Black
            )
        )
        Spacer(modifier = Modifier.height(18.dp))
        OutlinedTextField(value = inputValue, onValueChange = {
            inputValue = it
            convertUnits()
        }, label = { Text(text = "Enter Value") })
        Spacer(modifier = Modifier.height(18.dp))
        Row {
            Box {
                Button(onClick = { inputExpanded = true }) {
                    Text(text = inputUnit)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Drop Down Arrow")
                }
                DropdownMenu(expanded = inputExpanded, onDismissRequest = { inputExpanded = false }) {
                    unitCategories[selectedCategory]?.forEach { (unit, factor) ->
                        DropdownMenuItem(text = { Text(unit) }, onClick = {
                            inputExpanded = false
                            inputUnit = unit
                            inputConversionFactor.value = factor
                            convertUnits()
                        })
                    }
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box {
                Button(onClick = { outputExpanded = true }) {
                    Text(text = outputUnit)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Drop Down Arrow")
                }
                DropdownMenu(expanded = outputExpanded, onDismissRequest = { outputExpanded = false }) {
                    validOutputUnits.forEach { (unit, factor) ->
                        DropdownMenuItem(text = { Text(unit) }, onClick = {
                            outputExpanded = false
                            outputUnit = unit
                            outputConversionFactor.value = factor
                            convertUnits()
                        })
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Result: $outputValue $outputUnit",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun UnitConverterPreview() {
    UnitConverter()
}
