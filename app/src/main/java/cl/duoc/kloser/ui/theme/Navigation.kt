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
    viewModelAmigo: AmigoViewModel // Usamos el nombre consistente: viewModelAmigo
) {
    NavHost(navController = navController, startDestination = "formulario") {
        composable("formulario"){
            Formulario(
                viewModel = viewModelForm,
                onFormularioEnviado = {
                    navController.navigate("amigos")
                }
            )
        }
        composable("amigos"){
            Amigos(viewModel = viewModelAmigo) // Usando la variable correcta
        }
    }
}