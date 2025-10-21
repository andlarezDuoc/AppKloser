package cl.duoc.kloser.ui.theme.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.kloser.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoggedIn: () -> Unit,
    onRegisterClick: (() -> Unit)? = null
) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var edad by rememberSaveable { mutableStateOf("") }
    var aceptaTerminos by rememberSaveable { mutableStateOf(false) }

    fun validacionCorreo(c: String): Boolean {
        return c.matches(Regex("^[\\w.-]+@[\\w.-]+\\.\\w+$"))
    }
    val isButtonEnabled = nombre.isNotBlank() &&
            correo.isNotBlank() &&
            edad.isNotBlank() &&
            aceptaTerminos

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Campos de texto
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = edad,
            onValueChange = { input -> if (input.all { it.isDigit() }) edad = input },
            label = { Text("Edad") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        // Checkbox de términos
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Acepta los términos")
            Checkbox(
                checked = aceptaTerminos,
                onCheckedChange = { aceptaTerminos = it }
            )
        }
        Spacer(Modifier.height(20.dp))

        // Botón Enviar
        Button(
            onClick = { onLoggedIn() },
            enabled = isButtonEnabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar")
        }

        // Botón opcional para ir a registro
        if (onRegisterClick != null) {
            Spacer(Modifier.height(12.dp))
            Button(onClick = onRegisterClick, modifier = Modifier.fillMaxWidth()) {
                Text("Ir a Registro")
            }
        }
    }
}
