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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ontime.ui.auth.login.LoginActivity
import com.example.ontime.ui.auth.logout.LogoutState
import com.example.ontime.ui.auth.logout.LogoutViewModel
import com.example.ontime.ui.friend.AddFriendScreen
import com.example.ontime.ui.friend.AddFriendViewModel
import com.example.ontime.ui.main.MainScreen
import com.example.ontime.ui.team.TeamFormationViewModel

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // ViewModel을 상위 레벨에서 생성하여 화면 간 데이터 공유
    val teamFormationViewModel: TeamFormationViewModel = hiltViewModel()

    NavHost(
        navController = navController,
//        startDestination = Screen.Main.route
        startDestination = Screen.AddFriends.route
    ) {
        mainScreen(navController, context)
//        teamFormationScreen(navController, teamFormationViewModel)
//        friendSelectionScreen(navController, teamFormationViewModel)
        addFriendsScreen(navController)
    }
}

private fun NavGraphBuilder.addFriendsScreen(navController: NavController) {
    composable(Screen.AddFriends.route) {

        val viewModel: AddFriendViewModel = hiltViewModel()
        AddFriendScreen(
            viewModel = viewModel
        )
    }
}


private fun NavGraphBuilder.mainScreen(navController: NavController, context: Context) {
    composable(Screen.Main.route) {
        val viewModel: LogoutViewModel = hiltViewModel()
        val logoutState by viewModel.logoutState.collectAsState()

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
            onLogout = { viewModel.logout() },
            onAddTeamClick = {
                navController.navigate(Screen.TeamFormation.route)
            },
            onTeamClick = { team ->
                // 팀 상세 화면으로 이동
//                navController.navigate("${Screen.TeamDetail.route}/${team.title}")
            },
            viewModel = viewModel
        )
    }
}
//
//private fun NavGraphBuilder.teamFormationScreen(
//    navController: NavController,
//    viewModel: TeamFormationViewModel
//) {
//    composable(Screen.TeamFormation.route) {
//        TeamFormationScreen(
//            onFriendSelectionClick = {
//                navController.navigate(Screen.FriendSelection.route)
//            },
//            viewModel = viewModel
//        )
//    }
//}
//
//private fun NavGraphBuilder.friendSelectionScreen(
//    navController: NavController,
//    teamFormationViewModel: TeamFormationViewModel
//) {
//    composable(Screen.FriendSelection.route) {
//        FriendSelectionScreen(
//            onBackClick = { navController.popBackStack() },
//            onFriendsSelected = { selectedFriends ->
//                teamFormationViewModel.updateMembers(selectedFriends)
//                navController.popBackStack()
//            }
//        )
//    }
//}