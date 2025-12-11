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
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.compose.rememberNavController
import cl.duoc.kloser.ui.theme.*
import cl.duoc.kloser.viewmodel.UsuarioViewModel
import cl.duoc.kloser.viewmodel.FormularioViewModel
import cl.duoc.kloser.repository.UsuarioRepository
import cl.duoc.kloser.data.remote.RetrofitInstance
import cl.duoc.kloser.viewmodel.UsuarioViewModelFactory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {

    private lateinit var currentPhotoPath: String

    // Removed AmigoDao as we use Xano now
    
    private val apiService by lazy {
        RetrofitInstance.api
    }

    private val usuarioRepository by lazy {
        UsuarioRepository(apiService)
    }

    private val usuarioViewModel: UsuarioViewModel by viewModels {
        UsuarioViewModelFactory(usuarioRepository)
    }

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

        val formularioViewModel = FormularioViewModel(this)
        formularioViewModel.initDatabase()

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
                        usuarioViewModel = usuarioViewModel
                    )
                }
            }
        }
    }

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