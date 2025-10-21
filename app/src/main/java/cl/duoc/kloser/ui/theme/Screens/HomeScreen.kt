package cl.duoc.kloser.ui.theme.Screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cl.duoc.kloser.viewmodel.AuthViewModel
import java.io.File
import java.io.FileOutputStream

@Composable
fun HomeScreen(viewModel: AuthViewModel, onLogout: () -> Unit) {
    val context = LocalContext.current
    var images by remember { mutableStateOf<List<Bitmap>>(emptyList()) }

    // Cargar imágenes guardadas al iniciar
    LaunchedEffect(Unit) {
        images = loadSavedImages(context)
        viewModel.setImages(images)
    }

    // launcher to take a preview image with the camera
    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        bitmap?.let {
            // Guardar en memoria
            images = images + it
            viewModel.setImages(images)

            // Guardar en almacenamiento interno
            saveBitmapToInternalStorage(context, it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Kloser - Momentos") }, actions = {
                Button(onClick = { onLogout() }) { Text("Salir") }
            })
        },
        bottomBar = {
            BottomAppBar {
                Text("© Kloser", modifier = Modifier.padding(12.dp))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(text = "Captura y comparte momentos instantáneos", style = MaterialTheme.typography.h6)
            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = { takePicture.launch(null) }) {
                    Text("Abrir cámara")
                }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { /* placeholder: abrir galería local */ }) {
                    Text("Galería")
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("Últimas capturas:")
            Spacer(Modifier.height(8.dp))

            if (images.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("No hay imágenes aún. Usa 'Abrir cámara' para capturar una.")
                }
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(images) { bmp ->
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = "captura",
                            modifier = Modifier
                                .size(150.dp)
                                .clickable { /* abrir detalle o compartir */ }
                        )
                    }
                }
            }
        }
    }
}

private fun AuthViewModel.setImages(images: kotlin.collections.List<android.graphics.Bitmap>) {}


// Función para guardar el bitmap en almacenamiento interno
private fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap) {
    val fileName = "foto_${System.currentTimeMillis()}.png"
    val file = File(context.filesDir, fileName)
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }
}

// Función para cargar todos los bitmaps guardados
private fun loadSavedImages(context: Context): List<Bitmap> {
    val dir = context.filesDir
    val files = dir.listFiles() ?: return emptyList()
    return files.mapNotNull { file ->
        BitmapFactory.decodeFile(file.absolutePath)
    }
}
