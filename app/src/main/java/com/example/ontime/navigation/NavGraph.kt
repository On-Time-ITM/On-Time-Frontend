// navigation/NavGraph.kt
sealed class Screen(val route: String) {
    object Main : Screen("main")
    object TeamFormation : Screen("teamFormation")
    object FriendSelection : Screen("friendSelection")
}
