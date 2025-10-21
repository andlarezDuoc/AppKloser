package cl.duoc.kloser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.kloser.repository.MediaRepository
import cl.duoc.kloser.ui.theme.KloserTheme
import cl.duoc.kloser.ui.theme.Screens.HomeScreen
import cl.duoc.kloser.ui.theme.Screens.LoginScreen
import cl.duoc.kloser.viewmodel.AuthViewModel


class MainActivity : ComponentActivity() {
// This Activity is created from an Empty Activity template.


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


// Provide repository to ViewModel manually for this skeleton. In a real project use DI.
        val viewModelFactory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(MediaRepository()) as T
            }
        }


        setContent {
            KloserTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory)


                    AppNavHost(navController = navController, authViewModel = authViewModel)
                }
            }
        }
    }
}


@Composable
fun AppNavHost(navController: NavHostController, authViewModel: AuthViewModel) {
// rememberSaveable + ViewModel used so state survives rotation.
    val currentUser by authViewModel.user.collectAsState()


    NavHost(navController = navController, startDestination = if (currentUser == null) "login" else "home") {
        composable("login") {
            LoginScreen(viewModel = authViewModel, onLoggedIn = { navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            } })
        }
        composable("home") {
            HomeScreen(viewModel = authViewModel, onLogout = {
                authViewModel.logout()
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            })
        }
    }
}