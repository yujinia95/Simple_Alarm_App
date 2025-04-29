package com.bcit.assignment_yujinjeong.ui.naviagtion

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bcit.assignment_yujinjeong.ui.screens.AddAlarmScreen
import com.bcit.assignment_yujinjeong.ui.screens.CardGameScreen
import com.bcit.assignment_yujinjeong.ui.screens.HomeScreen

/**
 * Making other screen (Home, add alarm, edit alarm, card game) as subclass of Screen class for
 * organising purpose and easy to implement navigation.
 */
sealed class Screen(val route: String) {

    object Home: Screen("home")
    object AddAlarm : Screen("add_alarm")
    object EditAlarm : Screen("edit_alarm/{alarmId}") {
        fun createRoute(alarmId: Int): String = "edit_alarm/$alarmId"
    }

    object CardGame : Screen("card_game/{alarmId}") {
        fun createRoute(alarmId: Int): String = "card_game/$alarmId"
    }
}


/**
 * This method is for setting up navigation in app.
 */
@Composable
fun Nav(
    navController: NavHostController,
    startDestination: String = Screen.Home.route,   //Setting default screen as home.
    modifier: Modifier = Modifier
) {
    //Host for all other screens by listening to nav controller.
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {


        //Setting route for HomeScreen
        composable(Screen.Home.route) {

            HomeScreen(
                //Lambda for adding alarm click (Navigate to add alarm screen).
                onAddAlarmClick = {
                    navController.navigate(Screen.AddAlarm.route)
                },
                //Lambda for editing alarm click (Navigate to edit alarm screen)
                onEditAlarmClick = {
                        alarmId -> navController.navigate(Screen.EditAlarm.createRoute(alarmId))
                }
            )
        }


        //Setting route for AddAlarmScreen Route
        composable(Screen.AddAlarm.route) {
            AddAlarmScreen(
                //Button function for going back to the previous page.
                onBackClick = {
                    navController.popBackStack()
                },
                //Button function for saving alarm.
                onSaveClick = {
                    navController.popBackStack()
                }
            )
        }


        //Setting route for EditAlarmScreen Route
        composable(
            route = Screen.EditAlarm.route,

            //Extracting alarm Id when navigates to edit alarm screen.
            arguments = listOf(
                navArgument("alarmId") {
                    type = NavType.IntType
                }
            )
        ) {
            //Current navigation stack state holding route args and nav controller.
                backStackEntry ->

            //Getting alarmId arg from current nav stack state. If null, stop rendering screen.
            val alarmId = backStackEntry.arguments?.getInt("alarmId") ?: return@composable

            //Sharing AddAlarmScreen but in edit mode. (Loading existing alarm info from db).
            AddAlarmScreen(
                alarmId = alarmId,
                onBackClick = {navController.popBackStack()},
                onSaveClick = {navController.popBackStack()}
            )
        }

        //Setting route for CardGameScreen.
        composable(

            route = Screen.CardGame.route,
            arguments = listOf(
                navArgument("alarmId") {
                    type = NavType.IntType
                })
        ) {
                backStackEntry ->

            val alarmId = backStackEntry.arguments?.getInt("alarmId") ?: return@composable

            CardGameScreen(
                alarmId = alarmId,
                //If game ends, taking user to Home screen.
                onGameComplete = {
                    //Navigate to Home and remove the CardGame screen from back stack
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
            )
        }
    }
}