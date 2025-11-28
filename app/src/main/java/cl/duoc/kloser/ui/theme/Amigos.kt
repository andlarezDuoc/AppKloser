package cl.duoc.amigo.ui.theme

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
import androidx.compose.material.icons.filled.Delete
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle // <-- Importación corregida
import coil.compose.rememberAsyncImagePainter
import cl.duoc.amigo.model.Amigo
import cl.duoc.amigo.viewModel.AmigoViewModel
import java.io.File

fun File.toUri(context: android.content.Context) = FileProvider.getUriForFile(
    context,
    "${context.packageName}.provider",
    this
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Amigos(
    viewModel: AmigoViewModel,
    onLogout: () -> Unit
) {
    val context = LocalContext.current

    // ⬇️ CORRECCIÓN 1: Usamos collectAsStateWithLifecycle para evitar errores de inferencia (Línea 47) ⬇️
    val amigos by viewModel.amigos.collectAsStateWithLifecycle(initialValue = emptyList())
    val searchedFriend by viewModel.searchedFriend.collectAsStateWithLifecycle(initialValue = null)
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle(initialValue = false)

    var idBusqueda by remember { mutableStateOf("") } // Usaremos esta variable para buscar
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    // ⬇️ CORRECCIÓN 2: El tipo del amigo seleccionado debe ser Amigo? ⬇️
    var amigoSeleccionado by remember { mutableStateOf<Amigo?>(null) }

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
                title = { Text("Lista de Amigos") },
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

                // Botón de cámara (Se mantiene)
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

                // Vista previa de la foto (Se mantiene)
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

                Text("Buscar Amigo por ID Xano", style = MaterialTheme.typography.titleMedium, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = idBusqueda,
                    onValueChange = { idBusqueda = it },
                    label = { Text("ID del Amigo a buscar (ej: 11)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (idBusqueda.isNotBlank()) {
                            viewModel.searchFriend(idBusqueda)
                        } else {
                            Toast.makeText(context, "Ingresa un ID para buscar", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = !isSearching,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isSearching) "Buscando..." else "Buscar Amigo")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Sección de Resultado de Búsqueda ---
                if (isSearching) {
                    CircularProgressIndicator(color = Color.Cyan)
                } else if (searchedFriend != null) {
                    Text("✅ Amigo encontrado:", color = Color.Green, modifier = Modifier.padding(bottom = 8.dp))
                    AmigoResultCard(searchedFriend!!)
                } else if (idBusqueda.isNotBlank() && !isSearching) {
                    Text("❌ Amigo con ID ${idBusqueda} no encontrado.", color = Color.Red)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Lista de amigos (Todos los amigos de Room) ---
                Text(
                    text = "Amigos Agregados (Caché Local)",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (amigos.isEmpty()) {
                    Text("No hay amigos registrados aún.", color = Color.Gray)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(amigos) { amigo ->
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
                                        // ⬇️ CORRECCIÓN 3: Acceso correcto a las propiedades del objeto 'amigo' ⬇️
                                        Text(
                                            text = amigo.nombre,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "ID Xano: ${amigo.id_xano}",
                                            color = Color.LightGray
                                        )
                                    }

                                    IconButton(onClick = {
                                        amigoSeleccionado = amigo
                                        mostrarDialogoEliminar = true
                                    }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar Amigo", tint = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                }

                // --- Diálogos (Corregidos para usar amigoSeleccionado de tipo Amigo?) ---

                // Diálogo de eliminar amigo
                if (mostrarDialogoEliminar && amigoSeleccionado != null) {
                    AlertDialog(
                        onDismissRequest = {
                            mostrarDialogoEliminar = false
                            amigoSeleccionado = null
                        },
                        title = { Text("Confirmar Eliminación") },
                        text = { Text("¿Estás seguro de eliminar a ${amigoSeleccionado!!.nombre}?") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    // ⬇️ CORRECCIÓN 4: Llamada correcta a eliminarAmigo ⬇️
                                    viewModel.eliminarAmigo(amigoSeleccionado!!)
                                    mostrarDialogoEliminar = false
                                    amigoSeleccionado = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) { Text("Eliminar") }
                        },
                        dismissButton = {
                            OutlinedButton(
                                onClick = {
                                    mostrarDialogoEliminar = false
                                    amigoSeleccionado = null
                                }
                            ) { Text("Cancelar") }
                        }
                    )
                }
            }
        }
    )
}

// Composable para mostrar el resultado de la búsqueda (Se mantiene)
@Composable
fun AmigoResultCard(amigo: Amigo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF005668)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Nombre: ${amigo.nombre}", fontWeight = FontWeight.Bold, color = Color.White)
            Text("ID Xano: ${amigo.id_xano}", color = Color.White)
            Text("Origen: ${amigo.genero}", color = Color.White)
        }
    }
}