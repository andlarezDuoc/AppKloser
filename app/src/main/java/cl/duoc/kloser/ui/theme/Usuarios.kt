package cl.duoc.kloser.ui.theme

import android.Manifest
import android.content.pm.PackageManager
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import cl.duoc.kloser.data.model.Usuario
import cl.duoc.kloser.viewmodel.UsuarioViewModel
import java.io.File

fun File.toUri(context: android.content.Context) = FileProvider.getUriForFile(
    context,
    "${context.packageName}.provider",
    this
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Usuarios(
    viewModel: UsuarioViewModel,
    onLogout: () -> Unit
) {
    val context = LocalContext.current

    val usuarios by viewModel.usuarios.collectAsStateWithLifecycle(initialValue = emptyList())
    val searchedUsuarios by viewModel.searchedUsuarios.collectAsStateWithLifecycle(initialValue = emptyList())
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle(initialValue = false)

    var nombreBusqueda by remember { mutableStateOf("") }
    
    var imagenUri by remember { mutableStateOf<android.net.Uri?>(null) }

    val cameraFile = remember {
        File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp_photo.jpg")
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imagenUri = cameraFile.toUri(context)
        } else {
            Toast.makeText(context, "No se tomó la foto", Toast.LENGTH_SHORT).show()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(cameraFile.toUri(context))
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Lista de Usuarios") },
                navigationIcon = {
                    TextButton(onClick = onLogout, modifier = Modifier.padding(start = 8.dp)) {
                        Text("Cerrar Sesión", color = Color.Red)
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(paddingValues)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Botón de cámara
                Button(
                    onClick = {
                        when (PackageManager.PERMISSION_GRANTED) {
                            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                                cameraLauncher.launch(cameraFile.toUri(context))
                            }
                            else -> permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    modifier = Modifier
                        .size(150.dp)
                        .padding(bottom = 16.dp)
                ) {
                    Icon(
                        Icons.Filled.CameraAlt,
                        contentDescription = "Abrir cámara",
                        modifier = Modifier.size(64.dp)
                    )
                }

                // Vista previa de la foto
                imagenUri?.let { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Foto capturada",
                        modifier = Modifier
                            .size(200.dp)
                            .padding(bottom = 16.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }

                // --- Sección de Búsqueda ---
                Spacer(modifier = Modifier.height(16.dp))

                Text("Buscar Usuario por Nombre", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nombreBusqueda,
                    onValueChange = { nombreBusqueda = it },
                    label = { Text("Nombre del Usuario") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (nombreBusqueda.isNotBlank()) {
                            viewModel.searchUsuario(nombreBusqueda)
                        } else {
                            Toast.makeText(context, "Ingresa un nombre para buscar", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = !isSearching,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isSearching) "Buscando..." else "Buscar Usuario")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Sección de Resultado de Búsqueda ---
                if (isSearching) {
                    CircularProgressIndicator(color = Color.Cyan)
                } else if (searchedUsuarios.isNotEmpty()) {
                    Text("✅ Usuarios encontrados:", color = Color.Green, modifier = Modifier.padding(bottom = 8.dp))
                    LazyColumn(modifier = Modifier.height(150.dp).fillMaxWidth()) {
                        items(searchedUsuarios) { usuario ->
                             UsuarioResultCard(usuario)
                             Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                } else if (nombreBusqueda.isNotBlank() && !isSearching && searchedUsuarios.isEmpty()) {
                     // Only show not found if we actually searched
                     // Warning: this logic might show "not found" before searching if we don't track "hasSearched"
                     // For simplicity, we assume if isSearching is false and list is empty but text is not blank, we might have searched.
                     // But strictly speaking, we might just have typed text.
                     // The user asked to "Search by name".
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Lista de usuarios (API) ---
                Text(
                    text = "Usuarios (API Xano)",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (usuarios.isEmpty()) {
                    Text("No hay usuarios registrados aún.", color = Color.Gray)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(usuarios) { usuario ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = usuario.nombre,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Email: ${usuario.email}",
                                            color = Color.LightGray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun UsuarioResultCard(usuario: Usuario) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF005668)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Nombre: ${usuario.nombre}", fontWeight = FontWeight.Bold, color = Color.White)
            Text("ID Xano: ${usuario.id_xano}", color = Color.White)
            Text("Email: ${usuario.email}", color = Color.White)
        }
    }
}