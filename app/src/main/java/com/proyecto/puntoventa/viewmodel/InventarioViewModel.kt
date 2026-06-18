package com.proyecto.puntoventa.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.puntoventa.model.Producto
import com.proyecto.puntoventa.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InventarioViewModel : ViewModel() {
    private val repository = ProductoRepository()

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    init {
        cargarProductos()
    }

    private fun cargarProductos() {
        viewModelScope.launch {
            repository.getProductos().collect { lista ->
                _productos.value = lista
            }
        }
    }

    fun agregarProducto(nombre: String, descripcion: String, precio: Double, stock: Int, categoria: String, imagenBase64: String) {
        viewModelScope.launch {
            val nuevoProducto = Producto(
                nombre = nombre,
                descripcion = descripcion,
                precio = precio,
                stock = stock,
                categoria = categoria,
                imagenBase64 = imagenBase64 // Pasamos el nuevo parámetro
            )
            repository.guardarProducto(nuevoProducto)
        }
    }

    fun eliminarProducto(productoId: String) {
        viewModelScope.launch {
            repository.eliminarProducto(productoId)
        }
    }

    // Usaremos esta función cuando editemos un producto existente
    // Modifica la función actualizarProducto
    fun actualizarProducto(id: String, nombre: String, descripcion: String, precio: Double, stock: Int, categoria: String, imagenBase64: String) {
        viewModelScope.launch {
            val productoActualizado = Producto(
                id = id,
                nombre = nombre,
                descripcion = descripcion,
                precio = precio,
                stock = stock,
                categoria = categoria,
                imagenBase64 = imagenBase64 // Pasamos el nuevo parámetro
            )
            repository.guardarProducto(productoActualizado)
        }
    }
}

