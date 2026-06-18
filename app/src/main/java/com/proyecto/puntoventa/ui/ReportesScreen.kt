package com.proyecto.puntoventa.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.proyecto.puntoventa.viewmodel.ReportesViewModel

@Composable
fun ReportesScreen(viewModel: ReportesViewModel = viewModel()) {
    val ventasTotales by viewModel.ventasTotales.collectAsState()
    val productosTop by viewModel.productosMasVendidos.collectAsState()

    // Cargar o actualizar los datos automáticamente cada vez que se entra a esta pantalla
    LaunchedEffect(Unit) {
        viewModel.cargarReportes()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 32.dp)
    ) {
        Text("Reportes de la Veterinaria", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        // Tarjeta destacada con los ingresos totales
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Ingresos Totales", style = MaterialTheme.typography.titleMedium)
                Text("$$ventasTotales", style = MaterialTheme.typography.displayMedium)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Productos Más Vendidos (Top 5)", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        // Lista de los artículos de mayor demanda
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(productosTop) { (nombre, cantidad) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(nombre, style = MaterialTheme.typography.bodyLarge)
                    Text("$cantidad unds.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                }
                HorizontalDivider()
            }
        }
    }
}