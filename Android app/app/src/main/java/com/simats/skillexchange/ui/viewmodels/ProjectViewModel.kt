package com.simats.skillexchange.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.skillexchange.data.models.*
import com.simats.skillexchange.data.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProjectViewModel : ViewModel() {
    private val repo = ProjectRepository()

    private val _projects = MutableStateFlow<List<ProjectOut>>(emptyList())
    val projects: StateFlow<List<ProjectOut>> = _projects

    private val _myProjects = MutableStateFlow<List<ProjectOut>>(emptyList())
    val myProjects: StateFlow<List<ProjectOut>> = _myProjects

    private val _selectedProject = MutableStateFlow<ProjectOut?>(null)
    val selectedProject: StateFlow<ProjectOut?> = _selectedProject

    private val _applicants = MutableStateFlow<List<ApplicantOut>>(emptyList())
    val applicants: StateFlow<List<ApplicantOut>> = _applicants

    private val _matchScore = MutableStateFlow<Double?>(null)
    val matchScore: StateFlow<Double?> = _matchScore

    private val _healthAnalysis = MutableStateFlow<ProjectHealth?>(null)
    val healthAnalysis: StateFlow<ProjectHealth?> = _healthAnalysis

    private val _generatedIdea = MutableStateFlow<GeneratedProject?>(null)
    val generatedIdea: StateFlow<GeneratedProject?> = _generatedIdea

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _successMsg = MutableStateFlow<String?>(null)
    val successMsg: StateFlow<String?> = _successMsg

    fun loadProjects(q: String? = null, category: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            try { _projects.value = repo.listProjects(q = q, category = category) }
            catch (e: Exception) { _error.value = e.message }
            finally { _isLoading.value = false }
        }
    }

    fun loadMyProjects() {
        viewModelScope.launch {
            _isLoading.value = true
            try { _myProjects.value = repo.myProjects() }
            catch (e: Exception) { _error.value = e.message }
            finally { _isLoading.value = false }
        }
    }

    fun loadProject(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _selectedProject.value = repo.getProject(id)
                _matchScore.value = repo.getMatchScore(id).match_score
                // Load health analysis for investors/owners
                _healthAnalysis.value = repo.getProjectAnalysis(id)
            }
            catch (e: Exception) { _error.value = e.message }
            finally { _isLoading.value = false }
        }
    }

    fun generateIdea(title: String, category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _generatedIdea.value = repo.generateIdea(title, category)
                _successMsg.value = "AI generated a project plan for you! ✨"
            }
            catch (e: Exception) { _error.value = e.message }
            finally { _isLoading.value = false }
        }
    }

    fun createProject(body: ProjectCreate, onSuccess: (ProjectOut) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val p = repo.createProject(body)
                _successMsg.value = "Project '${p.title}' created!"
                onSuccess(p)
            }
            catch (e: Exception) { _error.value = e.message }
            finally { _isLoading.value = false }
        }
    }

    fun updateProject(id: Int, body: ProjectUpdate) {
        viewModelScope.launch {
            try {
                val p = repo.updateProject(id, body)
                _selectedProject.value = p
                _successMsg.value = "Project updated!"
            }
            catch (e: Exception) { _error.value = e.message }
        }
    }

    fun deleteProject(id: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try { repo.deleteProject(id); onSuccess() }
            catch (e: Exception) { _error.value = e.message }
        }
    }

    fun applyToProject(id: Int, message: String?) {
        viewModelScope.launch {
            try {
                repo.applyToProject(id, message)
                _successMsg.value = "Application submitted! ✅"
                _selectedProject.value = repo.getProject(id)
            }
            catch (e: Exception) { _error.value = e.message }
        }
    }

    fun loadApplicants(projectId: Int) {
        viewModelScope.launch {
            try { _applicants.value = repo.listApplicants(projectId) }
            catch (e: Exception) { _error.value = e.message }
        }
    }

    fun acceptApplicant(projectId: Int, userId: Int) {
        viewModelScope.launch {
            try {
                repo.acceptApplicant(projectId, userId)
                _applicants.value = repo.listApplicants(projectId)
                _successMsg.value = "Member accepted! 🎉"
            }
            catch (e: Exception) { _error.value = e.message }
        }
    }

    fun rejectApplicant(projectId: Int, userId: Int) {
        viewModelScope.launch {
            try {
                repo.rejectApplicant(projectId, userId)
                _applicants.value = repo.listApplicants(projectId)
            }
            catch (e: Exception) { _error.value = e.message }
        }
    }

    private val _members = MutableStateFlow<List<ProjectMemberOut>>(emptyList())
    val members: StateFlow<List<ProjectMemberOut>> = _members

    fun loadMembers(projectId: Int) {
        viewModelScope.launch {
            try { _members.value = repo.listProjectMembers(projectId) }
            catch (e: Exception) { _error.value = e.message }
        }
    }

    fun removeMember(projectId: Int, userId: Int) {
        viewModelScope.launch {
            try {
                repo.removeProjectMember(projectId, userId)
                _members.value = repo.listProjectMembers(projectId)
                _successMsg.value = "Member removed."
            }
            catch (e: Exception) { _error.value = e.message }
        }
    }

    fun clearMessages() { _error.value = null; _successMsg.value = null }

    fun getProjectChatId(projectId: Int, onResult: (Int?) -> Unit) {
        viewModelScope.launch {
            try {
                val res = repo.getProjectConversation(projectId)
                onResult(res.id)
            } catch (e: Exception) {
                onResult(null)
            }
        }
    }
}

