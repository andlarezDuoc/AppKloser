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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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
    val amigos by viewModel.amigos.collectAsState(initial = emptyList())

    var nombre by remember { mutableStateOf("") }
    var id by remember { mutableStateOf("") }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    var amigoSeleccionado by remember { mutableStateOf<Amigo?>(null) }
    var imagenUri by remember { mutableStateOf<android.net.Uri?>(null) }

    // Archivo temporal para la foto
    val cameraFile = remember {
        File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp_photo.jpg")
    }

    // Launcher para tomar foto
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imagenUri = cameraFile.toUri(context)
        } else {
            Toast.makeText(context, "No se tomó la foto", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher para pedir permiso
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

                Spacer(modifier = Modifier.height(16.dp))

                // agregar nuevo amigo
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
                            viewModel.agregarAmigos(nombre, id)
                            nombre = ""
                            id = ""
                            mostrarDialogo = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Agregar Amigo a la Lista")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de amigos
                Text(
                    text = "Amigos Agregados",
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

                // Diálogo de amigo agregado
                if (mostrarDialogo) {
                    AlertDialog(
                        onDismissRequest = { mostrarDialogo = false },
                        title = { Text("✅ Amigo agregado") },
                        text = { Text("Amigo guardado correctamente en la lista.") },
                        confirmButton = {
                            Button(onClick = { mostrarDialogo = false }) { Text("Aceptar") }
                        }
                    )
                }

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
