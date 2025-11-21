package cl.duoc.amigo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import cl.duoc.amigo.ui.theme.AmigoTheme
import cl.duoc.amigo.ui.theme.AppNavigation // Importar tu Composable de navegación

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AmigoTheme {
                // Una superficie container que usa el color 'background' del tema
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Llamamos al Composable raíz que gestiona la navegación y los ViewModels
                    AppRoot()
                }
            }
        }
    }
} // Cierre de la clase MainActivity

@Composable
fun AppRoot() {
    // 1. Crear el controlador de navegación
    val navController = rememberNavController()

    // 2. Llamar a AppNavigation sin pasar ViewModels
    // La función AppNavigation crea los ViewModels internamente (porque necesitan el Context para Room)
    AppNavigation(navController = navController)
}