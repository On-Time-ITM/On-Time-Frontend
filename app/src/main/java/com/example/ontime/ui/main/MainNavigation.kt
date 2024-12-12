package com.example.ontime.ui.main

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ontime.navigation.Screen
import com.example.ontime.ui.auth.login.LoginActivity
import com.example.ontime.ui.auth.logout.LogoutState
import com.example.ontime.ui.auth.logout.LogoutViewModel
import com.example.ontime.ui.calendar.CalendarScreen
import com.example.ontime.ui.calendar.CalendarViewModel
import com.example.ontime.ui.friend.addFriend.AddFriendScreen
import com.example.ontime.ui.friend.addFriend.AddFriendViewModel
import com.example.ontime.ui.friend.contactList.ContactListScreen
import com.example.ontime.ui.friend.contactList.ContactListViewModel
import com.example.ontime.ui.friend.friendList.FriendListScreen
import com.example.ontime.ui.friend.friendList.FriendListViewModel
import com.example.ontime.ui.friend.requestAccpet.RequestListScreen
import com.example.ontime.ui.friend.requestAccpet.RequestListViewModel
import com.example.ontime.ui.friendSelection.FriendSelectionEvent
import com.example.ontime.ui.location.LocationSelectionEvent
import com.example.ontime.ui.location.LocationSelectionScreen
import com.example.ontime.ui.location.LocationSelectionViewModel
import com.example.ontime.ui.team.TeamFormationScreen
import com.example.ontime.ui.team.TeamFormationViewModel
import com.example.ontime.ui.team.teamDetail.TeamDetailScreen
import com.example.ontime.ui.team.teamDetail.TeamDetailViewModel

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // ViewModel을 상위 레벨에서 생성하여 화면 간 데이터 공유
    val teamFormationViewModel: TeamFormationViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
//        startDestination = Screen.TeamDetail.route
    ) {
        mainScreen(navController, context)
        teamFormationScreen(navController, teamFormationViewModel)
        friendSelectionScreen(navController, teamFormationViewModel)
        addFriendsScreen(navController)
        contactListScreen(navController)
        friendsListScreen(navController)
        requestListScreen(navController)
        locationSelectionScreen(navController, teamFormationViewModel)
        calendarScreen(navController, teamFormationViewModel)
        teamDetailScreen(navController)
    }
}

private fun NavGraphBuilder.teamDetailScreen(navController: NavController) {
    composable(
        route = "${Screen.TeamDetail.route}/{teamId}",
        arguments = listOf(
            navArgument("teamId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val viewModel: TeamDetailViewModel = hiltViewModel()

        LaunchedEffect(Unit) {
            // 화면 진입 시 팀 상세 정보 로드
            viewModel.getTeamDetail()
        }

        TeamDetailScreen(viewModel = viewModel)
    }
}

private fun NavGraphBuilder.calendarScreen(
    navController: NavController,
    teamFormationViewModel: TeamFormationViewModel
) {
    composable(Screen.Calendar.route) {

        val viewModel: CalendarViewModel = hiltViewModel()

        // 날짜 선택 완료 이벤트 처리
//        LaunchedEffect(Unit) {
//            viewModel
//        }
        CalendarScreen(
            viewModel = viewModel,
            onConfirm = { date, time ->
                // 직접 LocalDate와 LocalTime을 전달
                teamFormationViewModel.updateDateTime(date, time)
                navController.popBackStack()
            }
        )
    }
}


private fun NavGraphBuilder.locationSelectionScreen(
    navController: NavController,
    teamFormationViewModel: TeamFormationViewModel
) {
    composable(Screen.LocationSelection.route) {

        val viewModel: LocationSelectionViewModel = hiltViewModel()

        // 위치 선택 완료 이벤트 처리
        LaunchedEffect(Unit) {
            viewModel.navigationEvent.collect { event ->
                when (event) {
                    is LocationSelectionEvent.LocationConfirmed -> {
                        teamFormationViewModel.updateLocation(event.address, event.latLng)
                        navController.popBackStack()
                    }
                }
            }
        }

        LocationSelectionScreen(viewModel = viewModel)
    }
}


private fun NavGraphBuilder.requestListScreen(navController: NavController) {
    composable(Screen.RequestList.route) {
        val viewModel: RequestListViewModel = hiltViewModel()
        RequestListScreen(viewModel = viewModel)
    }
}

private fun NavGraphBuilder.friendsListScreen(navController: NavController) {
    composable(Screen.FriendsList.route) {
        val viewModel: FriendListViewModel = hiltViewModel()
        FriendListScreen(
            viewModel = viewModel,
            onNavigateToContactList = { navController.navigate(Screen.ContactList.route) },
            onNavigateToDirectAdd = { navController.navigate(Screen.AddFriends.route) },
            onNavigateToRequests = { navController.navigate(Screen.RequestList.route) })
    }
}

private fun NavGraphBuilder.contactListScreen(navController: NavController) {
    composable(Screen.ContactList.route) {
        val viewModel: ContactListViewModel = hiltViewModel()
        ContactListScreen(viewModel = viewModel, onNavigateBack = { navController.popBackStack() })
    }
}

private fun NavGraphBuilder.addFriendsScreen(navController: NavController) {
    composable(Screen.AddFriends.route) {

        val viewModel: AddFriendViewModel = hiltViewModel()
        AddFriendScreen(
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}


private fun NavGraphBuilder.mainScreen(navController: NavController, context: Context) {
    composable(Screen.Main.route) {
        val logoutViewModel: LogoutViewModel = hiltViewModel()
        val viewModel: MainViewModel = hiltViewModel()
        val logoutState by logoutViewModel.logoutState.collectAsState()

        // 로그아웃 상태 처리
        LaunchedEffect(logoutState) {
            when (logoutState) {
                is LogoutState.Success -> {
                    // Activity로 구현된 로그인 화면으로 이동
                    context.startActivity(Intent(context, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                }

                else -> {}
            }
        }
        MainScreen(
            onLogout = { logoutViewModel.logout() },
            onAddTeamClick = {
                navController.navigate(Screen.TeamFormation.route)
            },
            onFriendClick = {
                navController.navigate(Screen.FriendsList.route)
            },
            onTeamClick = { meeting ->
                // TeamDetail 화면으로 이동하면서 meetingId 전달
                navController.navigate("${Screen.TeamDetail.route}/${meeting.id}")
            },
            viewModel = viewModel
        )
    }
}

private fun NavGraphBuilder.teamFormationScreen(
    navController: NavController,
    viewModel: TeamFormationViewModel
) {
    composable(Screen.TeamFormation.route) {
        LaunchedEffect(Unit) {
            viewModel.resetState()
        }
        TeamFormationScreen(
            onSetLocationClick = {
                navController.navigate(Screen.LocationSelection.route)
            },
            onFriendSelectionClick = {
                navController.navigate(Screen.FriendSelection.route)
            },
            onCalendarClick = {
                navController.navigate(Screen.Calendar.route)
            },
            onNavigateToTeamDetail = { teamId ->

                viewModel.resetAfterCreation()
                // 팀 상세 페이지로 이동
                navController.navigate("${Screen.TeamDetail.route}/$teamId") {
                    // 팀 생성 화면들을 백스택에서 제거
                    popUpTo(Screen.TeamFormation.route) {
                        inclusive = true
                    }
                }
            },
            viewModel = viewModel
        )
    }
}

private fun NavGraphBuilder.friendSelectionScreen(
    navController: NavController,
    teamFormationViewModel: TeamFormationViewModel,
) {
    composable(Screen.FriendSelection.route) {
        val viewModel: FriendListViewModel = hiltViewModel()

        LaunchedEffect(Unit) {
            viewModel.navigationEvent.collect { event
                ->
                when (event) {
                    is FriendSelectionEvent.FriendsConfirmed -> {
                        teamFormationViewModel.updateMembersList(event.selectedFriendsList)
                        navController.popBackStack()
                    }
                }
            }
        }

        FriendSelectionScreen(
            viewModel = viewModel
        )
    }
}