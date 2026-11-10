package com.xiaofan.ledger.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.xiaofan.ledger.ui.edit.OrderEditScreen
import com.xiaofan.ledger.ui.list.OrderListScreen

object Routes {
    const val LIST = "list"
    const val EDIT = "edit?orderId={orderId}"
    fun edit(orderId: Long): String = "edit?orderId=$orderId"
}

@Composable
fun AppNav() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.LIST) {
        composable(Routes.LIST) {
            OrderListScreen(
                onAdd = { navController.navigate(Routes.edit(-1L)) },
                onEdit = { id -> navController.navigate(Routes.edit(id)) },
            )
        }
        composable(
            route = Routes.EDIT,
            arguments = listOf(
                navArgument("orderId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            ),
        ) {
            OrderEditScreen(
                onBack = { navController.popBackStack() },
                onDone = { navController.popBackStack() },
            )
        }
    }
}
