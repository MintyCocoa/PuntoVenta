package com.proyecto.puntoventa.model

import com.google.firebase.Timestamp

// Representa un artículo en el inventario veterinario
data class Producto(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val stock: Int = 0,
    val categoria: String = "",
    val imagenBase64: String = "" // NUEVO CAMPO PARA LA IMAGEN
)

// Representa una línea dentro de la factura
data class ItemVenta(
    val productoId: String = "",
    val nombreProducto: String = "",
    val cantidad: Int = 0,
    val precioUnitario: Double = 0.0
) {
    val subtotal: Double get() = cantidad * precioUnitario
}

// Representa la factura o ticket de venta final
data class Venta(
    val id: String = "",
    val fecha: Timestamp = Timestamp.now(),
    val items: List<ItemVenta> = emptyList(),
    val total: Double = 0.0,
    val metodoPago: String = "Efectivo"
)