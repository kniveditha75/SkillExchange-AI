package com.simats.skillexchange.ui.screens.ideaowner

import com.simats.skillexchange.ui.theme.*

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simats.skillexchange.data.models.ProjectCreate
import com.simats.skillexchange.ui.components.*
import com.simats.skillexchange.ui.viewmodels.ProjectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProjectScreen(
    onBack: () -> Unit,
    onSuccess: (Int) -> Unit,
    vm: ProjectViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Tech") }
    var skills by remember { mutableStateOf("") }
    var fundingGoal by remember { mutableStateOf("") }
    var seekingFunding by remember { mutableStateOf(false) }
    var teamSize by remember { mutableStateOf("1") }

    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()
    val generatedIdea by vm.generatedIdea.collectAsState()

    // When AI generates an idea, update the fields
    LaunchedEffect(generatedIdea) {
        generatedIdea?.let {
            description = it.description + "\n\nKey Milestones:\n" + it.suggested_milestones.joinToString("\n") { m -> "- $m" }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(BgLight)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextDark)
            }
            Text("Create New Project", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
        }
        Divider(color = BorderGray)

        if (error != null) {
            Box(modifier = Modifier.padding(16.dp).fillMaxWidth().background(Color.Red.copy(0.1f), RoundedCornerShape(8.dp)).padding(12.dp)) {
                Text(error!!, color = Color.Red, fontSize = 14.sp)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Box(modifier = Modifier.weight(1f)) {
                    FormField("Project Title", title, { title = it }, "Enter a catchy title")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { if(title.isNotBlank()) vm.generateIdea(title, category) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Teal500),
                    contentPadding = PaddingValues(12.dp),
                    modifier = Modifier.height(56.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AI", fontWeight = FontWeight.Bold)
                }
            }
            
            FormField("Description", description, { description = it }, "Describe your vision...", isMultiLine = true)
            
            Column {
                Text("Category", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                Spacer(modifier = Modifier.height(8.dp))
                val categories = listOf("Tech", "Health", "Education", "Finance", "Social")
                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Purple500,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            FormField("Required Skills", skills, { skills = it }, "e.g. Kotlin, UI Design, Marketing (comma separated)")
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = seekingFunding, onCheckedChange = { seekingFunding = it })
                Text("Seeking Funding?", fontSize = 14.sp, color = TextDark)
            }

            if (seekingFunding) {
                FormField("Funding Goal (₹)", fundingGoal, { fundingGoal = it }, "0.00", isNumber = true)
            }

            FormField("Max Team Size", teamSize, { teamSize = it }, "1", isNumber = true)

            Spacer(modifier = Modifier.height(16.dp))

            GradientButton(
                text = if (isLoading) "Creating..." else "Launch Project",
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank()) {
                        vm.createProject(
                            ProjectCreate(
                                title = title,
                                description = description,
                                category = category,
                                required_skills = skills,
                                funding_goal = fundingGoal.toDoubleOrNull() ?: 0.0,
                                seeking_funding = seekingFunding,
                                team_size = teamSize.toIntOrNull() ?: 1
                            ),
                            onSuccess = { onSuccess(it.id) }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isMultiLine: Boolean = false,
    isNumber: Boolean = false
) {
    Column {
        Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TextGray) },
            modifier = Modifier.fillMaxWidth().then(if (isMultiLine) Modifier.height(120.dp) else Modifier),
            shape = RoundedCornerShape(12.dp),
            singleLine = !isMultiLine,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = if (isNumber) androidx.compose.ui.text.input.KeyboardType.Number else androidx.compose.ui.text.input.KeyboardType.Text
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Purple500,
                unfocusedBorderColor = BorderGray
            )
        )
    }
}

