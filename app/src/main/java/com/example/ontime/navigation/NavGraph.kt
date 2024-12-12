package com.example.ontime.navigation

// navigation/NavGraph.kt
sealed class Screen(val route: String) {
    object Main : Screen("main")
    object TeamFormation : Screen("teamFormation")
    object FriendSelection : Screen("friendSelection")
    object AddFriends : Screen("addFriends")
    object ContactList : Screen("contactList")
    object FriendsList : Screen("friendsList")
    object RequestList : Screen("requestList")
    object LocationSelection : Screen("LocationSelection")
    object Calendar : Screen("calendar")
    object TeamDetail : Screen("teamDetail")
}

