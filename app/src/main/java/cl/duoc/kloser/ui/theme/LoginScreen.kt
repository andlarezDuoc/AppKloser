package cl.duoc.amigo.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
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
import cl.duoc.amigo.viewModel.FormularioViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: FormularioViewModel,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isLoginEnabled = correo.isNotBlank() && password.length >= 4

    var showError by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Iniciar Sesión") },
                navigationIcon = {
                    IconButton(onClick = onRegisterClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, // ⬅️ ÍCONO CORREGIDO
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
                    .background(Color.Black)
                    .padding(paddingValues)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(32.dp))

                // Logo
                Image(
                    painter = painterResource(id = R.drawable.kloser_logo),
                    contentDescription = "Logo Kloser",
                    modifier = Modifier.size(200.dp).padding(bottom = 16.dp)
                )

                Text(
                    text = "ACCEDE A TU CUENTA",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Correo Electrónico
                OutlinedTextField(
                    value = correo,
                    onValueChange = {
                        correo = it
                        showError = false
                    },
                    label = { Text("Correo Electrónico") }, // ⬅️ CAMBIADO A CORREO
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                //Contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        showError = false
                    },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Mensaje de Error
                if (showError) {
                    Text(
                        text = "Credenciales o correo incorrectos.",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    enabled = isLoginEnabled,
                    onClick = {
                        showError = false
                        scope.launch {
                            val success = viewModel.iniciarSesion(correo, password)
                            if (success) {
                                onLoginSuccess()
                            } else {
                                showError = true
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Iniciar Sesión")
                }

                Spacer(modifier = Modifier.height(16.dp))

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