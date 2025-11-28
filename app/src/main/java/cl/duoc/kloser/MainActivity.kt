package cl.duoc.amigo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels // Importación necesaria
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider // Importación necesaria
import androidx.navigation.compose.rememberNavController
import cl.duoc.amigo.ui.theme.*
import cl.duoc.amigo.viewModel.AmigoViewModel
import cl.duoc.amigo.viewModel.FormularioViewModel
import cl.duoc.amigo.model.AppDatabase
import cl.duoc.amigo.repository.AmigoRepository
import cl.duoc.kloser.data.remote.RetrofitInstance // Importar tu instancia de Retrofit
import cl.duoc.amigo.viewModel.AmigoViewModelFactory // Importar el Factory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {

    private lateinit var currentPhotoPath: String

    // --- DEPENDENCIAS ---
    // 1. Obtener la instancia del DAO de Room (Data Access Object)
    private val amigoDao by lazy {
        AppDatabase.getDatabase(this).amigoDao()
    }

    // 2. Obtener la instancia del API Service de Retrofit
    private val apiService by lazy {
        // Asumiendo que has creado el objeto RetrofitInstance con la variable 'api'
        RetrofitInstance.api
    }

    // 3. Crear el Repositorio inyectando el DAO y el API Service
    private val amigoRepository by lazy {
        AmigoRepository(amigoDao, apiService)
    }

    // 4. Crear el ViewModel usando el Factory para inyectar el Repository
    private val amigoViewModel: AmigoViewModel by viewModels {
        AmigoViewModelFactory(amigoRepository)
    }
    // --- FIN DEPENDENCIAS ---

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Toast.makeText(this, "Foto guardada en: $currentPhotoPath", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Cancelaste la captura", Toast.LENGTH_SHORT).show()
            }
        }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) openCamera()
            else Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewModels
        val formularioViewModel = FormularioViewModel(this)
        formularioViewModel.initDatabase()

        // **LÍNEAS OBSOLETAS ELIMINADAS:**
        // val amigoDao = AppDatabase.getDatabase(this).amigoDao()
        // val amigoRepository = AmigoRepository(amigoDao)
        // val amigoViewModel = AmigoViewModel(null)
        // amigoViewModel.initRepository(amigoRepository)
        // **FIN LÍNEAS OBSOLETAS**

        setContent {
            AmigoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // Navegación de la app
                    AppNavigation(
                        navController = navController,
                        formularioViewModel = formularioViewModel,
                        amigoViewModel = amigoViewModel // Usamos la variable 'amigoViewModel' inicializada arriba
                    )
                }
            }
        }
    }

    // ... (El resto de las funciones checkCameraPermissionAndOpen, openCamera, createImageFile se mantienen igual)
    fun checkCameraPermissionAndOpen() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    // Abre la cámara y guarda la foto en un archivo temporal
    private fun openCamera() {
        val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = try { createImageFile() } catch (ex: IOException) { null }

        photoFile?.also {
            val photoUri: Uri = FileProvider.getUriForFile(this, "cl.duoc.amigo.provider", it)
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri)

            if (intent.resolveActivity(packageManager) != null) {
                takePictureLauncher.launch(intent)
            } else {
                Toast.makeText(this, "No se encontró cámara disponible", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
            .apply { currentPhotoPath = absolutePath }
    }
}