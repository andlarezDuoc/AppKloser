package cl.duoc.amigo.ui.theme

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cl.duoc.amigo.viewModel.AmigoViewModel
import cl.duoc.amigo.viewModel.FormularioViewModel
// Nota: Tu importación de Amigos apunta a 'cl.duoc.kloser.ui.theme.Amigos'.
// Asegúrate de que esta ruta sea la correcta, o cámbiala a cl.duoc.amigo.ui.theme.Amigos
import cl.duoc.kloser.ui.theme.Amigos

// Se añade la anotación para evitar la advertencia experimental de Compose Navigation
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    navController: NavHostController,
    formularioViewModel: FormularioViewModel,
    amigoViewModel: AmigoViewModel // Este es el ViewModel que usaremos en la lista de amigos
) {
    NavHost(navController = navController, startDestination = "login") {

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
                // ⚠️ CORRECCIÓN 1: El Composable Amigos solo espera 'amigoViewModel' (sin nombre de parámetro)
                // ⚠️ CORRECCIÓN 2: El Composable Amigos ya no recibe 'onLogout'
                amigoViewModel = amigoViewModel
            )

            // Nota: Si necesitas funcionalidad de Logout en la pantalla de Amigos,
            // debes implementarla dentro del Composable Amigos y usar el navController.
        }
    }
}