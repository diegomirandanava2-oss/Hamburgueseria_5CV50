package com.burger.ordenes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun VistaTomarOrden(
    onOrdenEnviada: () -> Unit
) {
    var mesaOCliente by remember { mutableStateOf("") }
    var productoSeleccionado by remember { mutableStateOf(MENU_HAMBURGUESERIA.first()) }
    var cantidad by remember { mutableIntStateOf(1) }
    val notasSeleccionadas = remember { mutableStateListOf<String>() }
    var notaLibre by remember { mutableStateOf("") }
    val carritoActual = remember { mutableStateListOf<ItemOrden>() }
    var mensajeConfirmacion by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            OutlinedTextField(
                value = mesaOCliente,
                onValueChange = { mesaOCliente = it },
                label = { Text("Mesa o nombre del cliente") },
                placeholder = { Text("Ej. Mesa 4 o Carlos") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Selección de producto
        item {
            Text(
                text = "1. Elige el producto",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(MENU_HAMBURGUESERIA) { producto ->
                    val seleccionado = producto.id == productoSeleccionado.id
                    FilterChip(
                        selected = seleccionado,
                        onClick = { productoSeleccionado = producto },
                        label = {
                            Text("${producto.nombre} ($${producto.precio.toInt()})")
                        }
                    )
                }
            }
            Text(
                text = productoSeleccionado.descripcion,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Cantidad y notas rápidas
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "2. Cantidad y notas para cocina",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { if (cantidad > 1) cantidad-- }
                            ) { Text("-", style = MaterialTheme.typography.titleLarge) }

                            Text(
                                text = cantidad.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            IconButton(onClick = { cantidad++ }) {
                                Text("+", style = MaterialTheme.typography.titleLarge)
                            }
                        }
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        NOTAS_RAPIDAS.forEach { nota ->
                            val activa = notasSeleccionadas.contains(nota)
                            FilterChip(
                                selected = activa,
                                onClick = {
                                    if (activa) notasSeleccionadas.remove(nota)
                                    else notasSeleccionadas.add(nota)
                                },
                                label = { Text(nota) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = notaLibre,
                        onValueChange = { notaLibre = it },
                        label = { Text("Otra nota especial (opcional)") },
                        placeholder = { Text("Ej. Aderezo aparte, sin sal...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            val notasFinales = notasSeleccionadas.toList() +
                                    notaLibre.trim().takeIf { it.isNotEmpty() }.let { listOfNotNull(it) }

                            carritoActual.add(
                                ItemOrden(
                                    producto = productoSeleccionado,
                                    cantidad = cantidad,
                                    notas = notasFinales
                                )
                            )

                            // Reiniciar selección del ítem para seguir agregando rápido
                            cantidad = 1
                            notasSeleccionadas.clear()
                            notaLibre = ""
                            mensajeConfirmacion = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Agregar a la orden")
                    }
                }
            }
        }

        // Resumen de la orden actual antes de enviarla al chef
        item {
            Text(
                text = "Orden actual (${carritoActual.size} productos)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (carritoActual.isEmpty()) {
            item {
                Text(
                    text = "Aún no has agregado productos a esta orden.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(carritoActual.indices.toList()) { index ->
                val item = carritoActual[index]
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${item.cantidad}x ${item.producto.nombre}",
                                fontWeight = FontWeight.Bold
                            )
                            if (item.notas.isNotEmpty()) {
                                Text(
                                    text = "Notas: ${item.notas.joinToString(", ")}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        IconButton(onClick = { carritoActual.removeAt(index) }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Quitar",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = {
                    OrdenesRepository.agregarOrden(
                        mesaOCliente = mesaOCliente,
                        items = carritoActual.toList()
                    )
                    carritoActual.clear()
                    mesaOCliente = ""
                    mensajeConfirmacion = "¡Orden enviada a cocina!"
                    onOrdenEnviada()
                },
                enabled = carritoActual.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("ENVIAR ORDEN AL CHEF", fontWeight = FontWeight.Bold)
            }

            if (mensajeConfirmacion != null) {
                Text(
                    text = mensajeConfirmacion!!,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}