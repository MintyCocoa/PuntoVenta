package com.proyecto.puntoventa.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.puntoventa.model.Venta
import com.proyecto.puntoventa.repository.VentaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReportesViewModel : ViewModel() {
    private val ventaRepo = VentaRepository()

    // Guardará la suma total de ingresos
    private val _ventasTotales = MutableStateFlow(0.0)
    val ventasTotales: StateFlow<Double> = _ventasTotales.asStateFlow()

    // Guardará el top de productos (Nombre del producto y cantidad vendida)
    private val _productosMasVendidos = MutableStateFlow<List<Pair<String, Int>>>(emptyList())
    val productosMasVendidos: StateFlow<List<Pair<String, Int>>> = _productosMasVendidos.asStateFlow()

    fun cargarReportes() {
        viewModelScope.launch {
            val listaVentas = ventaRepo.obtenerTodasLasVentas()

            // 1. Calcular las ventas totales sumando los tickets
            _ventasTotales.value = listaVentas.sumOf { it.total }

            // 2. Calcular los productos más vendidos
            val conteoProductos = mutableMapOf<String, Int>()
            listaVentas.forEach { venta ->
                venta.items.forEach { item ->
                    val conteoActual = conteoProductos[item.nombreProducto] ?: 0
                    conteoProductos[item.nombreProducto] = conteoActual + item.cantidad
                }
            }

            // Ordenar de mayor a menor y tomar el Top 5
            _productosMasVendidos.value = conteoProductos.toList()
                .sortedByDescending { it.second }
                .take(5)
        }
    }
}