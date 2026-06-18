package com.proyecto.puntoventa.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.proyecto.puntoventa.model.Producto
import com.proyecto.puntoventa.utils.base64ABitmap
import com.proyecto.puntoventa.utils.uriABase64
import com.proyecto.puntoventa.viewmodel.InventarioViewModel

@Composable
fun InventarioScreen(viewModel: InventarioViewModel = viewModel()) {
    val productos by viewModel.productos.collectAsState()
    val context = LocalContext.current

    // Estados para el formulario
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var imagenBase64 by remember { mutableStateOf("") } // Estado para la imagen
    var productoEnEdicion by remember { mutableStateOf<Producto?>(null) }

    // Lanzador para abrir la galería de fotos
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val base64String = uriABase64(context, it)
            if (base64String != null) {
                imagenBase64 = base64String
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 32.dp)
    ) {
        Text(
            text = if (productoEnEdicion != null) "Editar Producto" else "Nuevo Producto Veterinario",
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre (ej. Vacuna Rabia)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = precio,
            onValueChange = { precio = it },
            label = { Text("Precio") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = stock,
            onValueChange = { stock = it },
            label = { Text("Cantidad en Stock") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // --- SECCIÓN DE IMAGEN ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                Text("Seleccionar Imagen")
            }

            // Si hay un Base64 guardado en el estado, lo mostramos
            if (imagenBase64.isNotEmpty()) {
                val bitmap = base64ABitmap(imagenBase64)
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Vista previa",
                        modifier = Modifier.size(60.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Botón Principal
            Button(
                onClick = {
                    val precioDouble = precio.toDoubleOrNull() ?: 0.0
                    val stockInt = stock.toIntOrNull() ?: 0

                    if (productoEnEdicion != null) {
                        viewModel.actualizarProducto(
                            id = productoEnEdicion!!.id,
                            nombre = nombre,
                            descripcion = "Actualizado desde app",
                            precio = precioDouble,
                            stock = stockInt,
                            categoria = "General",
                            imagenBase64 = imagenBase64
                        )
                    } else {
                        viewModel.agregarProducto(
                            nombre = nombre,
                            descripcion = "Añadido desde app",
                            precio = precioDouble,
                            stock = stockInt,
                            categoria = "General",
                            imagenBase64 = imagenBase64
                        )
                    }

                    // Limpiamos los campos
                    nombre = ""
                    precio = ""
                    stock = ""
                    imagenBase64 = ""
                    productoEnEdicion = null
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(if (productoEnEdicion != null) "Actualizar" else "Guardar")
            }

            // Botón Cancelar (Modo Edición)
            if (productoEnEdicion != null) {
                OutlinedButton(
                    onClick = {
                        nombre = ""
                        precio = ""
                        stock = ""
                        imagenBase64 = ""
                        productoEnEdicion = null
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Inventario Actual", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        // Lista de Productos
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(productos) { producto ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // Miniatura en la lista
                        if (producto.imagenBase64.isNotEmpty()) {
                            val bitmap = base64ABitmap(producto.imagenBase64)
                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Imagen de ${producto.nombre}",
                                    modifier = Modifier
                                        .size(50.dp)
                                        .padding(end = 8.dp)
                                )
                            }
                        }

                        // Datos del producto
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = producto.nombre, style = MaterialTheme.typography.titleMedium)
                            Text(text = "Precio: $${producto.precio} | Stock: ${producto.stock}")
                        }

                        // Botones de acción
                        Row {
                            IconButton(onClick = {
                                nombre = producto.nombre
                                precio = producto.precio.toString()
                                stock = producto.stock.toString()
                                imagenBase64 = producto.imagenBase64 // Cargar imagen al editar
                                productoEnEdicion = producto
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                            }

                            IconButton(onClick = {
                                viewModel.eliminarProducto(producto.id)
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}