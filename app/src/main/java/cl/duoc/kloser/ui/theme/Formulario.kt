package cl.duoc.amigo.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cl.duoc.amigo.R
import cl.duoc.amigo.viewModel.FormularioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Formulario(
    viewModel: FormularioViewModel,
    onFormularioEnviado: () -> Unit,
    onLoginClick: () -> Unit
) {

    var abrirModal by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.kloser_logo),
                    contentDescription = "Logo Kloser",
                    modifier = Modifier
                        .size(300.dp)
                        .padding(bottom = 8.dp)
                )

                Text(
                    text = "APP KLOSER",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "Regístrate",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Nombre
                OutlinedTextField(
                    value = viewModel.formulario.nombre,
                    onValueChange = {
                        viewModel.formulario = viewModel.formulario.copy(nombre = it)
                        viewModel.verificarNombre()
                    },
                    label = { Text("Nombre") },
                    isError = viewModel.mensajesError.nombre.isNotEmpty(),
                    supportingText = {
                        if (viewModel.mensajesError.nombre.isNotEmpty()) {
                            Text(viewModel.mensajesError.nombre, color = Color.Red)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Correo
                OutlinedTextField(
                    value = viewModel.formulario.correo,
                    onValueChange = {
                        viewModel.formulario = viewModel.formulario.copy(correo = it)
                        viewModel.verificarCorreo()
                    },
                    label = { Text("Correo Electrónico") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = viewModel.mensajesError.correo.isNotEmpty(),
                    supportingText = {
                        if (viewModel.mensajesError.correo.isNotEmpty()) {
                            Text(viewModel.mensajesError.correo, color = Color.Red)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Contraseña
                OutlinedTextField(
                    value = viewModel.formulario.contrasena,
                    onValueChange = {
                        viewModel.formulario = viewModel.formulario.copy(contrasena = it)
                        viewModel.verificarContrasena()
                    },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = viewModel.mensajesError.contrasena.isNotEmpty(),
                    supportingText = {
                        if (viewModel.mensajesError.contrasena.isNotEmpty()) {
                            Text(viewModel.mensajesError.contrasena, color = Color.Red)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Términos
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = viewModel.formulario.terminos,
                        onCheckedChange = {
                            viewModel.formulario = viewModel.formulario.copy(terminos = it)
                            viewModel.verificarTerminos()
                        }
                    )
                    Text("Acepto los términos y condiciones")
                }

                if (viewModel.mensajesError.terminos.isNotEmpty()) {
                    Text(viewModel.mensajesError.terminos, color = Color.Red)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    enabled = viewModel.verificarFormulario(),
                    onClick = {
                        if (viewModel.verificarFormulario()) {
                            viewModel.registrarUsuario() // Guardar en la base de datos
                            abrirModal = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrarse")
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("¿Ya tienes cuenta? Iniciar Sesión")
                }

                if (abrirModal) {
                    AlertDialog(
                        onDismissRequest = { abrirModal = false },
                        title = { Text("✅ Datos ingresados con éxito") },
                        text = { Text("¡Gracias por escogernos!") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    abrirModal = false
                                    onFormularioEnviado()
                                }
                            ) {
                                Text("Continuar")
                            }
                        }
                    )
                }
            }
        }
    )
}
