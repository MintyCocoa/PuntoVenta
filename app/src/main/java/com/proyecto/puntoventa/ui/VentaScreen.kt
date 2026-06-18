package com.proyecto.puntoventa.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.proyecto.puntoventa.viewmodel.VentaViewModel
import com.proyecto.puntoventa.worker.FacturaWorker
import java.util.concurrent.TimeUnit

@Composable
fun VentaScreen(viewModel: VentaViewModel = viewModel()) {
    val disponibles by viewModel.productosDisponibles.collectAsState()
    val carrito by viewModel.carrito.collectAsState()

    // Necesitamos el contexto para ejecutar la notificación en segundo plano
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- SECCIÓN DE PRODUCTOS ---
        Text("Productos Disponibles", style = MaterialTheme.typography.titleLarge)
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(disponibles) { producto ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(producto.nombre, style = MaterialTheme.typography.bodyLarge)
                        Text("Precio: $${producto.precio} | Stock: ${producto.stock}")
                    }
                    Button(onClick = { viewModel.agregarAlCarrito(producto, 1) }) {
                        Text("Añadir")
                    }
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        // --- SECCIÓN DEL CARRITO ---
        Text("Carrito de Compras", style = MaterialTheme.typography.titleLarge)
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(carrito) { item ->
                Text("${item.cantidad}x ${item.nombreProducto} - $${item.subtotal}")
            }
        }

        // Cálculo del total
        val total = carrito.sumOf { it.subtotal }
        Text(
            text = "Total a Pagar: $$total",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // --- BOTÓN DE COBRO ---
        Button(
            onClick = {
                // 1. Respaldamos los datos actuales antes de procesar la venta
                val itemsComprados = carrito.toList()
                val totalPagado = total

                viewModel.procesarVenta { ventaId ->
                    // 2. Generamos y guardamos la imagen del ticket
                    com.proyecto.puntoventa.utils.generarYGuardarTicketImagen(
                        context = context,
                        ventaId = ventaId,
                        items = itemsComprados,
                        total = totalPagado
                    )

                    // 3. Preparamos los datos para el trabajador de la notificación
                    val datos = workDataOf("VENTA_ID" to ventaId)

                    // Se mantiene el retraso de 15 segundos para poder comprobarlo
                    // cómodamente después de cerrar la app.
                    val peticionFactura = OneTimeWorkRequestBuilder<FacturaWorker>()
                        .setInputData(datos)
                        .setInitialDelay(15, TimeUnit.SECONDS)
                        .build()

                    WorkManager.getInstance(context).enqueue(peticionFactura)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = carrito.isNotEmpty()
        ) {
            Text("Cobrar y Generar Factura")
        }
    }
}