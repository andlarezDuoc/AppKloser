package cl.duoc.kloser.ui.theme

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cl.duoc.kloser.viewmodel.UsuarioViewModel
import cl.duoc.kloser.viewmodel.FormularioViewModel


@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    navController: NavHostController,
    formularioViewModel: FormularioViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    NavHost(navController = navController, startDestination = "login") {

        // Pantalla de Login
        composable("login") {
            LoginScreen(
                navController = navController,
                viewModel = formularioViewModel,
                onLoginSuccess = {
                    navController.navigate("usuarios") {
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
                onFormularioEnviado = { navController.navigate("usuarios") },
                onLoginClick = { navController.navigate("login") }
            )
        }

        // Pantalla de Lista de Usuarios
        composable("usuarios") {
            Usuarios(
                viewModel = usuarioViewModel,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
    }
}