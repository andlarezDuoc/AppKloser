package cl.duoc.amigo.ui.theme

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack // ⬅️ IMPORTACIÓN AGREGADA
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController // ⬅️ IMPORTACIÓN NECESARIA
import coil.compose.rememberAsyncImagePainter
import cl.duoc.amigo.model.Amigo // Paquete corregido
import cl.duoc.amigo.viewModel.AmigoViewModel // Paquete corregido
import java.io.File


// Extensión para obtener una URI temporal de manera segura usando FileProvider
fun File.toUri(context: android.content.Context): Uri {
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        this
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Amigos(
    navController: NavController, // ⬅️ Parámetro NavController añadido
    viewModel: AmigoViewModel
) {

    // Se especifica el tipo (List<Amigo>) para resolver errores de inferencia
    val amigos by viewModel.amigos.collectAsState(initial = emptyList<Amigo>())

    // Se inicializan con tipo String para resolver errores de inferencia
    var nombre by remember { mutableStateOf("")}
    var id by remember { mutableStateOf("")}
    var mostrarDialogo by remember {mutableStateOf(false) }

    // 📸 Lógica de la Cámara
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    // Archivo temporal para guardar la foto
    val cameraFile = remember {
        File(context.externalCacheDir, "temp_image_amigo.jpg")
    }

    // Launcher para abrir la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imagenUri = cameraFile.toUri(context)
        }
    }

    // Launcher para solicitar el permiso
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(cameraFile.toUri(context))
        } else {
            // Manejar permiso denegado
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Lista de Amigos y Cámara") },
                // 🚀 Lógica para el botón de regreso (volver al formulario de registro)
                navigationIcon = {
                    IconButton(onClick = {
                        // Vuelve a la pantalla anterior en la pila de navegación
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver al formulario de registro"
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F9FA))
                    .padding(paddingValues)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 1. SECCIÓN DE CÁMARA
                Text(
                    text = "Acceso a la Cámara",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Vista previa de la foto capturada
                imagenUri?.let { uri ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Foto capturada",
                        modifier = Modifier
                            .size(200.dp)
                            .padding(bottom = 16.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }

                // Botón para Abrir la Cámara
                Button(
                    onClick = {
                        // Comprobar y solicitar permiso
                        when (PackageManager.PERMISSION_GRANTED) {
                            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                                cameraLauncher.launch(cameraFile.toUri(context))
                            }
                            else -> {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = "Cámara")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tomar Foto")
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 2. SECCIÓN DE AGREGAR AMIGO

                Text(
                    text = "Agregar Nuevo Amigo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de tu amigo") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = id,
                    onValueChange = { id = it },
                    label = { Text("ID de tu amigo") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (nombre.isNotBlank() && id.isNotBlank()) {
                            // Llamar a la función del ViewModel con los parámetros
                            viewModel.agregarAmigos(
                                nombre = nombre,
                                idAmigo = id
                            )
                            // Limpiar campos
                            nombre = ""
                            id = ""
                            mostrarDialogo = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Agregar Amigo a la Lista")
                }


                Spacer(modifier = Modifier.height(24.dp))

                // 3. LISTA DE AMIGOS
                Text(
                    text = "Amigos Agregados",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (amigos.isEmpty()) {
                    Text("No hay amigos registradas aún.", color = Color.Gray)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(amigos){ amigo ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = amigo.nombre,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text("ID: ${amigo.autor}")
                                }
                            }
                        }
                    }
                }

                // DIÁLOGO DE CONFIRMACIÓN
                if (mostrarDialogo) {
                    AlertDialog(
                        onDismissRequest = { mostrarDialogo = false },
                        title = { Text("✅ Amigo agregado") },
                        text = { Text("Amigo guardado correctamente en la lista.") },
                        confirmButton = {
                            Button(onClick = { mostrarDialogo = false }) {
                                Text("Aceptar")
                            }
                        }
                    )
                }
            }
        }
    )
}