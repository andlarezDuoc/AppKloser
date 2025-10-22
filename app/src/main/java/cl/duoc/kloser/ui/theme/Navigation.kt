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

        // 1. Ruta del formulario (la pantalla anterior)
        composable("formulario"){
            Formulario(
                viewModel = viewModelForm,
                onFormularioEnviado = {
                    // Navega a la pantalla "amigos" al enviar el formulario
                    navController.navigate("amigos")
                }
            )
        }

        // 2. Ruta de la lista de amigos (la pantalla actual)
        composable("amigos"){
            // 🚀 ¡CAMBIO CLAVE! Pasamos el navController a la función Amigos.
            // Esto permite que el botón "ArrowBack" en Amigos.kt funcione
            // llamando a navController.popBackStack().
            Amigos(
                navController = navController, // ⬅️ Nuevo parámetro
                viewModel = viewModelAmigo
            )
        }
    }
}