package com.proyecto.puntoventa.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.proyecto.puntoventa.model.Producto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProductoRepository {
    private val db = FirebaseFirestore.getInstance()
    private val productosRef = db.collection("productos")

    // Obtiene los productos en tiempo real desde Firestore
    fun getProductos(): Flow<List<Producto>> = callbackFlow {
        val listener = productosRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val lista = snapshot.toObjects(Producto::class.java)
                trySend(lista)
            }
        }
        awaitClose { listener.remove() }
    }

    // Agrega un producto nuevo o actualiza uno existente
    suspend fun guardarProducto(producto: Producto) {
        val docRef = if (producto.id.isEmpty()) {
            productosRef.document() // Genera un ID automático si es nuevo
        } else {
            productosRef.document(producto.id)
        }
        val productoConId = producto.copy(id = docRef.id)
        docRef.set(productoConId).await()
    }

    // Resta las unidades del stock al concretarse una venta
    suspend fun actualizarStock(productoId: String, cantidadVendida: Int) {
        val docRef = productosRef.document(productoId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val stockActual = snapshot.getLong("stock")?.toInt() ?: 0
            val nuevoStock = (stockActual - cantidadVendida).coerceAtLeast(0)
            transaction.update(docRef, "stock", nuevoStock)
        }.await()
    }
    // Elimina un producto de la base de datos
    suspend fun eliminarProducto(productoId: String) {
        productosRef.document(productoId).delete().await()
    }

}