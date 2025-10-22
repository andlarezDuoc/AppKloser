package cl.duoc.amigo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import cl.duoc.amigo.model.AppDatabase
import cl.duoc.amigo.repository.AmigoRepository
import cl.duoc.amigo.ui.theme.AppNavigation
import cl.duoc.amigo.ui.theme.AmigoTheme // Usando AmigoTheme
import cl.duoc.amigo.viewModel.AmigoViewModel
import cl.duoc.amigo.viewModel.FormularioViewModel


class MainActivity : ComponentActivity() {
    // Asegúrate de tener FormularioViewModel disponible o crea una clase de ejemplo si no existe
    private val viewModelForm by lazy {FormularioViewModel() }
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "amigos_db"
        ).build()
    }
    private val repository by lazy{ AmigoRepository(db.amigoDao()) }
    private val viewModelAmigos by lazy{ AmigoViewModel(repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AmigoTheme {
                val navController = rememberNavController()

                AppNavigation(
                    navController = navController,
                    viewModelForm = viewModelForm,
                    viewModelAmigo = viewModelAmigos // Pasando el ViewModel de Amigos
                )
            }
        }
    }
}