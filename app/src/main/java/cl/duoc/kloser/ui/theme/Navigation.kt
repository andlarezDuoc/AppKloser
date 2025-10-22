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
    viewModelForm: FormularioViewModel,
    viewModelAmigo: AmigoViewModel
) {
    NavHost(navController = navController, startDestination = "formulario") {

        // 1. Ruta del formulario de registro
        composable("formulario"){
            Formulario(
                viewModel = viewModelForm,
                onFormularioEnviado = {
                    navController.navigate("amigos")
                },
                // Ahora el botón de Iniciar Sesión en Formulario navega a la nueva ruta "login"
                onLoginClick = {
                    navController.navigate("login")
                }
            )
        }

        // 2. Ruta de la lista de amigos
        composable("amigos"){
            Amigos(
                navController = navController,
                viewModel = viewModelAmigo
            )
        }

        // 3. 🚀 NUEVA RUTA: Pantalla de inicio de sesión
        composable("login"){
            LoginScreen(
                navController = navController,
                // Si el login es exitoso, navega a la pantalla de amigos y limpia la pila
                onLoginSuccess = {
                    navController.navigate("amigos") {
                        // Opcional: Esto limpia las pantallas anteriores (login, registro)
                        // para que el usuario no pueda "volver" al login una vez que entró.
                        popUpTo("formulario") { inclusive = true }
                    }
                },
                // Si el usuario hace clic en "Registrarse", vuelve a la pantalla de formulario
                onRegisterClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}