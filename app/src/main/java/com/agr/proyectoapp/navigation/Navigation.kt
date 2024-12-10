package com.agr.proyectoapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.agr.proyectoapp.screens.Categoria.CategoryScreen
import com.agr.proyectoapp.screens.comida.AddProductScreen
import com.agr.proyectoapp.screens.comida.FoodScreen
import com.agr.proyectoapp.screens.splash.SplashScreen
import com.agr.proyectoapp.screens.login.LoginScreen
import com.agr.proyectoapp.screens.home.HomeScreen
import com.agr.proyectoapp.screens.pedido.ConfirmOrderScreen
import com.agr.proyectoapp.screens.pedido.FinalScreen
import com.agr.proyectoapp.screens.pedido.OrderScreen


@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screens.SplashScreen.name
    ) {
        composable(Screens.SplashScreen.name){
            SplashScreen(navController = navController)
        }
        composable(Screens.LoginScreen.name){
            LoginScreen(navController = navController)
        }
        composable(Screens.HomeScreen.name){
            HomeScreen(navController = navController)
        }
        composable(Screens.OrderScreen.name) {
            OrderScreen(navController = navController)
        }
        composable(Screens.ConfirmOrderScreen.name) {
            ConfirmOrderScreen(navController = navController)
        }
        composable(Screens.FinalScreen.name) {
            FinalScreen(navController = navController)
        }

        composable(
            route = "${Screens.AddProductScreen.name}/{categoryName}",
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            AddProductScreen(navController = navController, categoryName = categoryName)
        }

        composable(
                route = "${Screens.FoodScreen.name}/{categoryName}",
        arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
        ) { backStackEntry ->
        val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
        FoodScreen(categoryName = categoryName, navController = navController)
    }


        composable(Screens.CategoryScreen.name){
            CategoryScreen(navController = navController)
        }




    }
}

