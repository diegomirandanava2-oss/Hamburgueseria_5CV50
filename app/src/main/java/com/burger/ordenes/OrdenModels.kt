package com.burger.ordenes

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger

data class ProductoMenu(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double
)

data class ItemOrden(
    val producto: ProductoMenu,
    val cantidad: Int,
    val notas: List<String>
)

data class OrdenCocina(
    val numeroOrden: Int,
    val mesaOCliente: String,
    val horaCreacion: String,
    val items: List<ItemOrden>,
    val completada: Boolean = false
)

val MENU_HAMBURGUESERIA = listOf(
    ProductoMenu(1, "Clásica Burger", "Res 180g, queso cheddar, lechuga, jitomate", 95.0),
    ProductoMenu(2, "Doble Bacon BBQ", "Doble res, tocino ahumado, aros de cebolla, BBQ", 135.0),
    ProductoMenu(3, "Jalapeño Crunch", "Res 180g, jalapeños frescos, pepper jack, chipotle", 115.0),
    ProductoMenu(4, "Papas Gajo", "Sazonadas con especias de la casa", 55.0),
    ProductoMenu(5, "Refresco / Bebida", "Lata o vaso 355 ml", 35.0)
)

val NOTAS_RAPIDAS = listOf(
    "Sin picante",
    "Con papas",
    "Sin cebolla",
    "Sin pepinillos",
    "Extra queso",
    "Bien cocida",
    "Término medio"
)

object OrdenesRepository {
    private val contadorOrdenes = AtomicInteger(1)
    private val _ordenes = MutableStateFlow<List<OrdenCocina>>(emptyList())
    val ordenes: StateFlow<List<OrdenCocina>> = _ordenes.asStateFlow()

    fun agregarOrden(mesaOCliente: String, items: List<ItemOrden>) {
        if (items.isEmpty()) return

        val formatoHora = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val nuevaOrden = OrdenCocina(
            numeroOrden = contadorOrdenes.getAndIncrement(),
            mesaOCliente = mesaOCliente.trim().ifEmpty { "Para llevar" },
            horaCreacion = formatoHora.format(Date()),
            items = items,
            completada = false
        )

        // Se agrega al final para mantener el orden cronológico de llegada (FIFO)
        _ordenes.value = _ordenes.value + nuevaOrden
    }

    fun marcarCompletada(numeroOrden: Int) {
        _ordenes.value = _ordenes.value.map { orden ->
            if (orden.numeroOrden == numeroOrden) orden.copy(completada = true) else orden
        }
    }
}