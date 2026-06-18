package com.proyecto.puntoventa.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.proyecto.puntoventa.model.Venta
import kotlinx.coroutines.tasks.await

class VentaRepository {
    private val db = FirebaseFirestore.getInstance()
    private val ventasRef = db.collection("ventas")

    // Registra la venta y devuelve el ID generado para la factura
    suspend fun registrarVenta(venta: Venta): String {
        val docRef = ventasRef.document()
        val ventaConId = venta.copy(id = docRef.id)
        docRef.set(ventaConId).await()
        return docRef.id
    }

    // Recupera todas las ventas de la base de datos para los reportes
    suspend fun obtenerTodasLasVentas(): List<Venta> {
        val snapshot = ventasRef.get().await()
        return snapshot.toObjects(Venta::class.java)
    }
}