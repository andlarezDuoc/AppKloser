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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
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
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import cl.duoc.amigo.model.Amigo
import cl.duoc.amigo.viewModel.AmigoViewModel
import java.io.File

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
    navController: NavController,
    viewModel: AmigoViewModel,
    onLogout: () -> Unit
) {

    val amigos by viewModel.amigos.collectAsState(initial = emptyList<Amigo>())
    var nombre by remember { mutableStateOf("")}
    var id by remember { mutableStateOf("")}
    var mostrarDialogo by remember {mutableStateOf(false) }
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    var amigoSeleccionado by remember { mutableStateOf<Amigo?>(null) } // Almacena el amigo a eliminar
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val cameraFile = remember {
        File(context.externalCacheDir, "temp_image_amigo.jpg")
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imagenUri = cameraFile.toUri(context)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(cameraFile.toUri(context))
        } else {

        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Lista de Amigos y Cámara") },
                navigationIcon = {
                    TextButton(
                        onClick = onLogout,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = "Cerrar Sesión",
                            color = Color.Red
                        )
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

                // SECCIÓN DE CÁMARA
                Text(
                    text = "Acceso a la Cámara",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
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

                // SECCIÓN DE AGREGAR AMIGO

                Text(
                    text = "Agregar Nuevo Amigo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
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

                // LISTA DE AMIGOS
                Text(
                    text = "Amigos Agregados",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
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
                                colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Row( // Usamos Row para alinear el texto y el botón de eliminar
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f) // Ocupa el espacio restante
                                    ) {
                                        Text(
                                            text = amigo.nombre,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "ID: ${amigo.autor}",
                                            color = Color.LightGray
                                        )
                                    }

                                    // Botón de Eliminar
                                    IconButton(
                                        onClick = {
                                            amigoSeleccionado = amigo
                                            mostrarDialogoEliminar = true
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = "Eliminar Amigo",
                                            tint = Color.Red
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // CONFIRMACIÓN DE AGREGAR AMIGO (sin cambios)
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

                if (mostrarDialogoEliminar && amigoSeleccionado != null) {
                    AlertDialog(
                        onDismissRequest = {
                            mostrarDialogoEliminar = false
                            amigoSeleccionado = null
                        },
                        title = { Text("Confirmar Eliminación") },
                        text = {
                            Text("¿Estás seguro de que quieres eliminar a ${amigoSeleccionado!!.nombre} de tu lista de amigos?")
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    viewModel.eliminarAmigo(amigoSeleccionado!!)
                                    mostrarDialogoEliminar = false
                                    amigoSeleccionado = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text("Eliminar")
                            }
                        },
                        dismissButton = {
                            OutlinedButton(
                                onClick = {
                                    // 4. Cancelar y cerrar el diálogo
                                    mostrarDialogoEliminar = false
                                    amigoSeleccionado = null
                                }
                            ) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }
        }
    )
}