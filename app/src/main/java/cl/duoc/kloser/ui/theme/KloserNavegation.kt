package cl.duoc.kloser.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.Composable


@Composable
fun SimpleDrawerContent(onItemClicked: (String) -> Unit) {
    Column {
        TextButton(onClick = { onItemClicked("home") }) { Text("Inicio") }
        TextButton(onClick = { onItemClicked("profile") }) { Text("Perfil") }
        TextButton(onClick = { onItemClicked("settings") }) { Text("Ajustes") }
    }
}