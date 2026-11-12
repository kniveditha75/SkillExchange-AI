package com.simats.skillexchange.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import com.simats.skillexchange.ui.viewmodels.AuthViewModel
import com.simats.skillexchange.ui.viewmodels.AuthViewModelFactory

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.simats.skillexchange.data.local.SessionManager
import com.simats.skillexchange.ui.components.SEBottomNavBar
import com.simats.skillexchange.ui.screens.auth.*
import com.simats.skillexchange.ui.screens.freelancer.*
import com.simats.skillexchange.ui.screens.ideaowner.*
import com.simats.skillexchange.ui.screens.investor.*
import com.simats.skillexchange.ui.screens.shared.*
import kotlinx.coroutines.launch

// ── Route destinations ─────────────────────────────────────────────────────────
sealed class Screen(val route: String) {
    // Auth
    object Splash      : Screen("splash")
    object Login       : Screen("login")
    object Signup      : Screen("signup")
    object RoleSelect  : Screen("role_select")
    object Onboarding  : Screen("onboarding")

    // Main / Dashboard (Role dependent)
    object Dashboard      : Screen("dashboard")
    
    // Freelancer
    object Projects       : Screen("projects")
    object ProjectDetail  : Screen("project_detail/{projectId}") {
        fun createRoute(id: Int) = "project_detail/$id"
    }
    object MyWork         : Screen("my_work")
    object SkillAnalysis  : Screen("skill_analysis")
    
    // Idea Owner
    object CreateProject  : Screen("create_project")
    object ManageProject  : Screen("manage_project/{projectId}") {
        fun createRoute(id: Int) = "manage_project/$id"
    }
    object FindTeam       : Screen("find_team/{projectId}") {
        fun createRoute(id: Int) = "find_team/$id"
    }
    object FindInvestor   : Screen("find_investor")
    object MyProjects     : Screen("my_projects")
    object AllTeams       : Screen("all_teams")
    object Collab         : Screen("collab")
    
    // Investor
    object Invest         : Screen("invest")
    object Portfolio      : Screen("portfolio")
    object Analytics      : Screen("analytics")
    
    // Shared
    object ChatList       : Screen("chat_list")
    object ChatDetail     : Screen("chat_detail/{convId}") {
        fun createRoute(id: Int) = "chat_detail/$id"
    }
    object Notifications  : Screen("notifications")
    object Profile        : Screen("profile")
    object Settings       : Screen("settings")
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    sessionManager: SessionManager
) {
    val session by sessionManager.sessionFlow.collectAsState(initial = null)
    val role = session?.role ?: ""
    val fullName = session?.fullName ?: "User"

    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(sessionManager))

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Sync token with RetrofitClient whenever session changes
    LaunchedEffect(session) {
        com.simats.skillexchange.data.network.RetrofitClient.setToken(session?.token)
    }

    // ── Session Redirect Logic ──────────────────────────────────────────────────
    LaunchedEffect(session, currentRoute) {
        if (currentRoute == Screen.Splash.route || currentRoute == null) return@LaunchedEffect
        
        if (session == null) {
            // Not logged in: Redirect to login if on a protected screen
            if (currentRoute !in listOf(Screen.Login.route, Screen.Signup.route)) {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0)
                }
            }
        } else {
            // Logged in: Check if role is missing
            if (role.isEmpty()) {
                // No role: Must go to RoleSelect or Onboarding
                if (currentRoute !in listOf(Screen.RoleSelect.route, Screen.Onboarding.route)) {
                    navController.navigate(Screen.RoleSelect.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            } else {
                // Role exists: Redirect away from auth screens if user tries to go there
                if (currentRoute in listOf(Screen.Login.route, Screen.Signup.route, Screen.RoleSelect.route)) {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(0)
                    }
                }
            }
        }
    }

    // Check if current route is a main screen that should show bottom nav
    val showBottomNav = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.Projects.route,
        Screen.MyWork.route,
        Screen.ChatList.route,
        Screen.Profile.route,
        Screen.Portfolio.route,
        Screen.Invest.route,
        Screen.Analytics.route,
        Screen.MyProjects.route,
        Screen.AllTeams.route,
        Screen.Collab.route
    )

    androidx.compose.material3.Scaffold(
        bottomBar = {
            if (showBottomNav && role.isNotEmpty()) {
                SEBottomNavBar(
                    role = role,
                    currentRoute = currentRoute ?: "",
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // ── Splash ────────────────────────────────────────────────────────────
            composable(Screen.Splash.route) {
                SplashScreen(
                    onSplashFinished = {
                        val destination = if (session != null) Screen.Dashboard.route else Screen.Login.route
                        navController.navigate(destination) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            // ── Auth ──────────────────────────────────────────────────────────────
            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel             = authViewModel,
                    onNavigateToSignup    = { navController.navigate(Screen.Signup.route) },
                    onNavigateToDashboard = { navController.navigate(Screen.Dashboard.route) }
                )
            }
            composable(Screen.Signup.route) {
                SignupScreen(
                    viewModel              = authViewModel,
                    onNavigateToLogin      = { navController.navigate(Screen.Login.route) },
                    onNavigateToRoleSelect = { navController.navigate(Screen.RoleSelect.route) }
                )
            }
            composable(Screen.RoleSelect.route) {
                RoleSelectScreen(
                    viewModel              = authViewModel,
                    onNavigateToOnboarding = { navController.navigate(Screen.Onboarding.route) }
                )
            }
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    viewModel             = authViewModel,
                    onNavigateToDashboard = { 
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onBack                = { navController.popBackStack() }
                )
            }

            // ── Dashboard (Role-Based Selection) ───────────────────────────────────
            composable(Screen.Dashboard.route) {
                when (role) {
                    "freelancer" -> FreelancerDashboard(
                        fullName = fullName,
                        onNavigateToProjects = { navController.navigate(Screen.Projects.route) },
                        onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                        onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                        onNavigateToProjectDetail = { id -> navController.navigate(Screen.ProjectDetail.createRoute(id)) },
                        onNavigateToChat = { id -> navController.navigate(Screen.ChatDetail.createRoute(id)) }
                    )
                    "idea_owner" -> IdeaOwnerDashboard(
                        fullName = fullName,
                        onNavigateToCreate = { navController.navigate(Screen.CreateProject.route) },
                        onNavigateToManage = { id -> navController.navigate(Screen.ManageProject.createRoute(id)) },
                        onNavigateToFindTeam = { id -> navController.navigate(Screen.FindTeam.createRoute(id)) },
                        onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                        onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
                    )
                    "investor" -> InvestorDashboard(
                        fullName = fullName,
                        onNavigateToInvest = { navController.navigate(Screen.Invest.route) },
                        onNavigateToPortfolio = { navController.navigate(Screen.Portfolio.route) },
                        onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                        onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
                    )
                    else -> com.simats.skillexchange.ui.components.LoadingState()
                }
            }

            // ── Freelancer Screens ────────────────────────────────────────────────
            composable(Screen.Projects.route) {
                ProjectMarketplace(
                    onNavigateToDetail = { id -> navController.navigate(Screen.ProjectDetail.createRoute(id)) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.ProjectDetail.route) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId")?.toIntOrNull() ?: 0
                ProjectDetail(
                    projectId = projectId,
                    currentUserId = session?.userId ?: 0,
                    onBack = { navController.popBackStack() },
                    onNavigateToChat = { id -> navController.navigate(Screen.ChatDetail.createRoute(id)) }
                )
            }
            composable(Screen.MyWork.route) {
                MyWorkScreen(
                    onNavigateToProjectDetail = { id -> navController.navigate(Screen.ProjectDetail.createRoute(id)) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.SkillAnalysis.route) {
                SkillAnalysisScreen(onBack = { navController.popBackStack() })
            }

            // ── Idea Owner Screens ────────────────────────────────────────────────
            composable(Screen.CreateProject.route) {
                CreateProjectScreen(
                    onBack = { navController.popBackStack() },
                    onSuccess = { _ -> 
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.CreateProject.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.ManageProject.route) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId")?.toIntOrNull() ?: 0
                ManageProjectScreen(
                    projectId = projectId,
                    onBack = { navController.popBackStack() },
                    onNavigateToFindTeam = { id -> navController.navigate(Screen.FindTeam.createRoute(id)) },
                    onNavigateToChat = { id -> navController.navigate(Screen.ChatDetail.createRoute(id)) }
                )
            }
            composable(Screen.FindTeam.route) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId")?.toIntOrNull() ?: 0
                FindTeamScreen(
                    projectId = projectId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.FindInvestor.route) {
                FindInvestorScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.MyProjects.route) {
                IdeaOwnerMyProjectsScreen(
                    onNavigateToManage = { id -> navController.navigate(Screen.ManageProject.createRoute(id)) },
                    onNavigateToFindTeam = { id -> navController.navigate(Screen.FindTeam.createRoute(id)) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.AllTeams.route) {
                IdeaOwnerAllTeamsScreen(
                    onNavigateToFindTeam = { id -> navController.navigate(Screen.FindTeam.createRoute(id)) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Collab.route) {
                CollabScreen(
                    onNavigateToChat = { id -> navController.navigate(Screen.ChatDetail.createRoute(id)) },
                    onNavigateToProjectDetail = { id -> navController.navigate(Screen.ProjectDetail.createRoute(id)) },
                    onBack = { navController.popBackStack() }
                )
            }

            // ── Investor Screens ──────────────────────────────────────────────────
            composable(Screen.Invest.route) {
                InvestmentMarketplaceScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.Portfolio.route) {
                MyPortfolioScreen(
                    onBack = { navController.popBackStack() },
                    onChat = { id -> navController.navigate(Screen.ChatDetail.createRoute(id)) }
                )
            }
            composable(Screen.Analytics.route) {
                // To be implemented or using Dashboard stats
                PlaceholderScreen("Analytics")
            }

            // ── Shared Screens ────────────────────────────────────────────────────
            composable(Screen.ChatList.route) {
                ChatListScreen(
                    onNavigateToChat = { id -> navController.navigate(Screen.ChatDetail.createRoute(id)) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.ChatDetail.route) { backStackEntry ->
                val convId = backStackEntry.arguments?.getString("convId")?.toIntOrNull() ?: 0
                IndividualChatScreen(
                    convId = convId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Notifications.route) {
                NotificationsScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.Profile.route) {
                val scope = rememberCoroutineScope()
                val onLogoutAction = {
                    scope.launch {
                        sessionManager.clearSession()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0)
                        }
                    }
                    Unit
                }

                when (role) {
                    "investor" -> InvestorProfileScreen(
                        onLogout = onLogoutAction,
                        onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                    )
                    "idea_owner" -> IdeaOwnerProfileScreen(
                        onLogout = onLogoutAction,
                        onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                    )
                    else -> FreelancerProfileScreen(
                        onLogout = onLogoutAction,
                        onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                    )
                }
            }
            composable(Screen.Settings.route) {
                PlaceholderScreen("Settings")
            }
        }
    }
}

@Composable
fun PlaceholderScreen(label: String) {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Text(text = "$label Screen Coming Soon")
    }
}

