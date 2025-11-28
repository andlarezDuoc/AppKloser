package cl.duoc.kloser.ui.theme // Asumiendo este paquete por tu error anterior

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.duoc.amigo.model.Amigo // Importación del modelo de datos
import cl.duoc.amigo.viewModel.AmigoViewModel // Importación del ViewModel

// ⚠️ Nota: Revisa que el paquete de este archivo (cl.duoc.kloser.ui.theme) sea el correcto
// y que el nombre de la función sea 'Amigos' si ese es el nombre del archivo.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Amigos(amigoViewModel: AmigoViewModel) {

    // 1. Observar la lista de amigos del ViewModel
    val amigosList by amigoViewModel.amigos.collectAsStateWithLifecycle()

    // Estados locales para el formulario de entrada
    var nombreInput by remember { mutableStateOf("") }
    var idAmigoInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Lista de Amigos (API/Room)") })
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // --- Sección de Formulario (Entradas) ---
                Text("Agregar Nuevo Amigo", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = nombreInput,
                    onValueChange = { nombreInput = it },
                    label = { Text("Nombre de tu amigo") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Nota: El ID del amigo (ID de Xano) se autogenera en la API,
                // por lo que este campo de ID no debería ser necesario para agregar.
                OutlinedTextField(
                    value = idAmigoInput,
                    onValueChange = { idAmigoInput = it },
                    label = { Text("ID de tu amigo (ej: 123)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (nombreInput.isNotBlank()) {
                            // 2. 🟢 Corregido: Llamada a la función agregarAmigo (sin 's')
                            amigoViewModel.agregarAmigo(nombreInput)
                            nombreInput = ""
                            idAmigoInput = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Agregar Amigo a la Lista")
                }

                Spacer(Modifier.height(16.dp))

                // --- Sección de Lista de Amigos ---
                Text("Amigos Agregados (${amigosList.size})", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(amigosList, key = { it.id_xano }) { amigo -> // Usamos id_xano como clave
                        AmigoListItem(
                            amigo = amigo,
                            onDeleteClick = {
                                // 3. 🔴 Corregido: Llamada a la función eliminarAmigo
                                amigoViewModel.eliminarAmigo(it)
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    )
}

@Composable
fun AmigoListItem(amigo: Amigo, onDeleteClick: (Amigo) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(amigo.nombre, style = MaterialTheme.typography.bodyLarge)
            Text("ID Xano: ${amigo.id_xano}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Botón para eliminar
        IconButton(
            onClick = { onDeleteClick(amigo) },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                Icons.Filled.Delete,
                contentDescription = "Eliminar",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}