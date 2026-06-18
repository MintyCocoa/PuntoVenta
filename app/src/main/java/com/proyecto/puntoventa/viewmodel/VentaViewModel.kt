package com.proyecto.puntoventa.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.puntoventa.model.ItemVenta
import com.proyecto.puntoventa.model.Producto
import com.proyecto.puntoventa.model.Venta
import com.proyecto.puntoventa.repository.ProductoRepository
import com.proyecto.puntoventa.repository.VentaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VentaViewModel : ViewModel() {
    private val productoRepo = ProductoRepository()
    private val ventaRepo = VentaRepository()

    // Controla los productos añadidos a la venta actual
    private val _carrito = MutableStateFlow<List<ItemVenta>>(emptyList())
    val carrito: StateFlow<List<ItemVenta>> = _carrito.asStateFlow()

    // Solo muestra productos que tengan existencias (stock > 0)
    private val _productosDisponibles = MutableStateFlow<List<Producto>>(emptyList())
    val productosDisponibles: StateFlow<List<Producto>> = _productosDisponibles.asStateFlow()

    init {
        cargarProductosDisponibles()
    }

    private fun cargarProductosDisponibles() {
        viewModelScope.launch {
            productoRepo.getProductos().collect { lista ->
                _productosDisponibles.value = lista.filter { it.stock > 0 }
            }
        }
    }

    fun agregarAlCarrito(producto: Producto, cantidad: Int) {
        if (cantidad <= producto.stock) {
            val item = ItemVenta(
                productoId = producto.id,
                nombreProducto = producto.nombre,
                cantidad = cantidad,
                precioUnitario = producto.precio
            )
            _carrito.value = _carrito.value + item
        }
    }

    fun procesarVenta(onVentaExitosa: (String) -> Unit) {
        viewModelScope.launch {
            val itemsActuales = _carrito.value
            if (itemsActuales.isEmpty()) return@launch

            val totalPagar = itemsActuales.sumOf { it.subtotal }
            val nuevaVenta = Venta(items = itemsActuales, total = totalPagar)

            // 1. Guardar la factura en la base de datos de Firebase
            val ventaId = ventaRepo.registrarVenta(nuevaVenta)

            // 2. Descontar las unidades vendidas del inventario
            itemsActuales.forEach { item ->
                productoRepo.actualizarStock(item.productoId, item.cantidad)
            }

            // 3. Vaciar el carrito y ejecutar la acción de éxito (Notificación)
            _carrito.value = emptyList()
            onVentaExitosa(ventaId)
        }
    }
}