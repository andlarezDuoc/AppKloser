package cl.duoc.amigo.ui.theme

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cl.duoc.amigo.viewModel.AmigoViewModel
import cl.duoc.amigo.viewModel.FormularioViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    formularioViewModel: FormularioViewModel,
    amigoViewModel: AmigoViewModel
) {
    NavHost(navController = navController, startDestination = "login") { // <-- LOGIN primero

        // Pantalla de Login
        composable("login") {
            LoginScreen(
                navController = navController,
                viewModel = formularioViewModel,
                onLoginSuccess = {
                    navController.navigate("amigos") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate("formulario") {
                        popUpTo("login") { inclusive = false }
                    }
                }
            )
        }

        // Pantalla de Formulario de Registro
        composable("formulario") {
            Formulario(
                viewModel = formularioViewModel,
                onFormularioEnviado = { navController.navigate("amigos") },
                onLoginClick = { navController.navigate("login") }
            )
        }

        // Pantalla de Lista de Amigos
        composable("amigos") {
            Amigos(
                viewModel = amigoViewModel,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
