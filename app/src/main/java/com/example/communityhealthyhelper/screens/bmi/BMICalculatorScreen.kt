package com.example.communityhealthyhelper.screens.bmi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.communityhealthyhelper.utils.AnimationUtils
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.communityhealthyhelper.components.StandardButton
import java.text.DecimalFormat
import kotlin.math.pow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BMICalculatorScreen(
    onNavigateBack: () -> Unit
) {
    // State variables
    var height by remember { mutableFloatStateOf(170f) } // in cm
    var weight by remember { mutableFloatStateOf(70f) } // in kg
    var heightError by remember { mutableStateOf("") }
    var weightError by remember { mutableStateOf("") }
    var bmiResult by remember { mutableStateOf<BMIResult?>(null) }
    
    val decimalFormat = remember { DecimalFormat("#.#") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BMI Calculator") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Calculate Your BMI",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Height input
            Text(
                text = "Height (cm): ${decimalFormat.format(height)}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )
            
            Slider(
                value = height,
                onValueChange = { 
                    height = it
                    heightError = ""
                },
                valueRange = 100f..250f,
                steps = 150,
                modifier = Modifier.fillMaxWidth()
            )
            
            if (heightError.isNotEmpty()) {
                Text(
                    text = heightError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Weight input
            Text(
                text = "Weight (kg): ${decimalFormat.format(weight)}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = weight.toString(),
                onValueChange = { 
                    weight = it.toFloatOrNull() ?: weight
                    weightError = ""
                },
                label = { Text("Weight (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                isError = weightError.isNotEmpty(),
                supportingText = if (weightError.isNotEmpty()) {
                    { Text(text = weightError) }
                } else null
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            StandardButton(
                onClick = {
                    var isValid = true
                    
                    if (height < 100 || height > 250) {
                        heightError = "Height should be between 100 and 250 cm"
                        isValid = false
                    }
                    
                    if (weight < 20 || weight > 300) {
                        weightError = "Weight should be between 20 and 300 kg"
                        isValid = false
                    }
                    
                    if (isValid) {
                        val heightInMeters = height / 100
                        val bmi = weight / (heightInMeters * heightInMeters)
                        bmiResult = BMIResult(
                            bmi = bmi,
                            category = getBMICategory(bmi),
                            categoryDescription = getBMICategoryDescription(bmi)
                        )
                    }
                },
                text = "Calculate BMI"
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            AnimatedVisibility(
                visible = bmiResult != null,
                enter = AnimationUtils.bmiResultEnterAnimation,
                exit = fadeOut() + shrinkVertically()
            ) {
                bmiResult?.let { BMIResultCard(result = it) }
            }
        }
    }
}

@Composable
fun BMIResultCard(result: BMIResult) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Your BMI Result",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = String.format("%.1f", result.bmi),
                style = MaterialTheme.typography.displayMedium,
                color = result.category.color
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Box(
                modifier = Modifier
                    .background(
                        color = result.category.color.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = result.category.label,
                    style = MaterialTheme.typography.titleMedium,
                    color = result.category.color
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = result.categoryDescription,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BMIRangeItem("Underweight", "< 18.5")
                BMIRangeItem("Normal", "18.5 - 24.9")
                BMIRangeItem("Overweight", "25 - 29.9")
                BMIRangeItem("Obese", "≥ 30")
            }
        }
    }
}

@Composable
fun BMIRangeItem(label: String, range: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = range,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

data class BMIResult(
    val bmi: Float,
    val category: BMICategory,
    val categoryDescription: String
)

sealed class BMICategory(val label: String, val color: Color) {
    object Underweight : BMICategory("Underweight", Color(0xFF2196F3)) // Blue
    object Normal : BMICategory("Normal", Color(0xFF4CAF50)) // Green
    object Overweight : BMICategory("Overweight", Color(0xFFFFC107)) // Yellow
    object Obese : BMICategory("Obese", Color(0xFFFF5722)) // Orange
    object SeverelyObese : BMICategory("Severely Obese", Color(0xFFF44336)) // Red
}

fun getBMICategory(bmi: Float): BMICategory {
    return when {
        bmi < 18.5 -> BMICategory.Underweight
        bmi < 25 -> BMICategory.Normal
        bmi < 30 -> BMICategory.Overweight
        bmi < 35 -> BMICategory.Obese
        else -> BMICategory.SeverelyObese
    }
}

fun getBMICategoryDescription(bmi: Float): String {
    return when {
        bmi < 18.5 -> "You're in the underweight range. Consider consulting with a healthcare professional about healthy weight gain strategies."
        bmi < 25 -> "You're in a healthy weight range. Maintain your healthy lifestyle with balanced nutrition and regular physical activity."
        bmi < 30 -> "You're in the overweight range. Consider making lifestyle changes to reach a healthier weight."
        bmi < 35 -> "You're in the obese range. It's advisable to consult with a healthcare professional to develop a plan for achieving a healthier weight."
        else -> "You're in the severely obese range. Please consult with a healthcare professional to discuss appropriate weight management strategies."
    }
}
