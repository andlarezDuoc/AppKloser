package cl.duoc.amigo.ui.theme

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cl.duoc.amigo.viewModel.AmigoViewModel
import cl.duoc.amigo.viewModel.FormularioViewModel


// CLASE DE FÁBRICA: Define cómo crear el ViewModel con el Context
// Esto es necesario porque FormularioViewModel requiere el Context para Room.
class FormularioViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FormularioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FormularioViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
) {
    // 1. Obtener el contexto de la aplicación
    val context = LocalContext.current

    // 2. Crear instancias de los ViewModels usando el Factory
    val formularioFactory = FormularioViewModelFactory(context)
    val viewModelForm: FormularioViewModel = viewModel(factory = formularioFactory)

    // AmigoViewModel no tiene dependencias de Context, se crea por defecto
    val viewModelAmigo: AmigoViewModel = viewModel()

    NavHost(navController = navController, startDestination = "formulario") {

// ... (El resto del código va en la siguiente parte) ...
        // ... (Viene de la Parte 1) ...

        // --- Pantalla de Formulario de Registro ---
        composable("formulario") {
            Formulario(
                viewModel = viewModelForm,
                onFormularioEnviado = {
                    navController.navigate("amigos")
                },
                onLoginClick = {
                    navController.navigate("login")
                }
            )
        }

        // --- Pantalla de Lista de Amigos
        composable("amigos") {
            Amigos(
                navController = navController,
                viewModel = viewModelAmigo,
                onLogout = {
                    // Cierre de sesión y limpieza de la pila de navegación
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                navController = navController,
                viewModel = viewModelForm, // Pasar el VM para la persistencia
                onLoginSuccess = {
                    // Inicio de sesión exitoso, navega y limpia la pila
                    navController.navigate("amigos") {
                        popUpTo("formulario") { inclusive = true }
                    }
                },
                onRegisterClick = {
                    // Navega a registro
                    navController.navigate("formulario") {
                        popUpTo("formulario") { inclusive = true }
                    }
                }
            )
        }
    } // Cierre del NavHost
} // Cierre de la función AppNavigation