package cl.duoc.amigo.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.duoc.amigo.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    onLoginSuccess: () -> Unit, // Acción a ejecutar si el login es exitoso (ej: ir a Amigos)
    onRegisterClick: () -> Unit // Acción para volver a la pantalla de Registro
) {
    // ⚠️ NOTA: En una aplicación real, usarías un ViewModel para manejar el estado
    // y la lógica de validación, similar a como lo hiciste en Formulario.
    var nombre by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isLoginEnabled = nombre.isNotBlank() && password.length >= 4 // Validación simple

    // Simulación de un error de credenciales
    var showError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Iniciar Sesión") },
                // Botón de regreso, te lleva de vuelta a la pantalla anterior (Registro)
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver al registro"
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(32.dp))

                // Logo Kloser
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo Kloser",
                    modifier = Modifier.size(200.dp).padding(bottom = 16.dp)
                )

                Text(
                    text = "ACCEDE A TU CUENTA",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Campo Nombre
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de usuario") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo Contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(), // Oculta la contraseña
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Mensaje de Error (si se intenta iniciar sesión con credenciales incorrectas)
                if (showError) {
                    Text(
                        text = "Credenciales incorrectas. Intenta de nuevo.",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botón principal de INICIAR SESIÓN
                Button(
                    // La propiedad 'enabled' ya verifica si los campos no están vacíos
                    enabled = isLoginEnabled,
                    onClick = {
                        // 🚀 MODIFICACIÓN CLAVE: Si el botón está habilitado (campos llenos),
                        // asumimos que el inicio de sesión es exitoso.
                        showError = false // Asegúrate de ocultar cualquier error previo
                        onLoginSuccess()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Iniciar Sesión")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón secundario para volver a REGISTRARSE
                TextButton(
                    onClick = onRegisterClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("¿No tienes cuenta? Regístrate aquí")
                }
            }
        }
    )
}