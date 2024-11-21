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
import com.example.ontime.ui.main.MainScreen

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        mainScreen(navController, context)
//        teamFormationScreen(navController)
//        friendSelectionScreen(navController)
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
//private fun NavGraphBuilder.teamFormationNavigation(
//    navController: NavController
//) {
//    composable(Screen.TeamFormation.route) {
//        TeamFormationScreen(
//            onFriendSelectionClick = {
//                navController.navigate(Screen.FriendSelection.route)
//            }
//        )
//    }
//}
//
//private fun NavGraphBuilder.friendSelectionNavigation(
//    navController: NavController
//) {
//    composable(Screen.FriendSelection.route) {
//        FriendSelectionScreen(
//            onBackClick = { navController.popBackStack() },
//            onFriendsSelected = { selectedFriends ->
//                // 선택된 친구들 데이터를 처리
//                navController.popBackStack()
//            }
//        )
//    }
//}