package com.simats.skillexchange.ui.screens.ideaowner

import com.simats.skillexchange.ui.theme.*

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simats.skillexchange.data.models.ApplicantOut
import com.simats.skillexchange.ui.components.*
import com.simats.skillexchange.ui.viewmodels.ProjectViewModel

@Composable
fun FindTeamScreen(
    projectId: Int,
    onBack: () -> Unit,
    vm: ProjectViewModel = viewModel()
) {
    val applicants by vm.applicants.collectAsState()
    val isLoading by vm.isLoading.collectAsState()

    val selectedProject by vm.selectedProject.collectAsState()

    LaunchedEffect(projectId) {
        vm.loadProject(projectId)
        vm.loadApplicants(projectId)
    }

    Column(modifier = Modifier.fillMaxSize().background(BgLight)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextDark)
            }
            Text("Review Applicants", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
        }
        Divider(color = BorderGray)
        
        selectedProject?.let { p ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Purple500.copy(0.05f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Project Requirements:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Purple500)
                    Text(p.required_skills ?: "No specific skills listed", fontSize = 14.sp, color = TextDark)
                }
            }
        }

        if (isLoading) {
            LoadingState()
        } else if (applicants.isEmpty()) {
            EmptyState("No applicants yet for this project.", Icons.Default.PersonSearch)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(applicants) { applicant ->
                    ApplicantCard(
                        applicant = applicant,
                        onAccept = { vm.acceptApplicant(projectId, applicant.applicant_id) },
                        onReject = { vm.rejectApplicant(projectId, applicant.applicant_id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ApplicantCard(
    applicant: ApplicantOut,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Purple500.copy(0.1f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(applicant.applicant_name.take(1).uppercase(), color = Purple500, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(applicant.applicant_name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                    Text(applicant.applicant_email, fontSize = 12.sp, color = TextGray)
                }
                Box(
                    modifier = Modifier
                        .background(
                            when(applicant.status) {
                                "accepted" -> Teal500.copy(0.1f)
                                "rejected" -> Color.Red.copy(0.1f)
                                else -> Orange500.copy(0.1f)
                            },
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        applicant.status.uppercase(), 
                        fontSize = 10.sp, 
                        fontWeight = FontWeight.Bold,
                        color = when(applicant.status) {
                            "accepted" -> Teal500
                            "rejected" -> Color.Red
                            else -> Orange500
                        }
                    )
                }
            }
            
            if (!applicant.applicant_skills.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Skills:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    applicant.applicant_skills.forEach { skill ->
                        Box(
                            modifier = Modifier
                                .background(Purple500.copy(0.08f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(skill, fontSize = 11.sp, color = Purple500, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
            
            if (!applicant.message.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth().background(BgLight, RoundedCornerShape(8.dp)).padding(12.dp)) {
                    Text("\"${applicant.message}\"", fontSize = 13.sp, color = TextDark, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                }
            }

            if (applicant.status == "pending") {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red.copy(0.3f))
                    ) { Text("Reject", fontSize = 13.sp) }
                    
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Teal500)
                    ) { Text("Accept", fontSize = 13.sp) }
                }
            }
        }
    }
}

