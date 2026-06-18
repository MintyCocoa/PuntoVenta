package com.proyecto.puntoventa.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class FacturaWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val ventaId = inputData.getString("VENTA_ID") ?: "Desconocida"

        // Emite la notificación de la factura
        mostrarNotificacionFactura(ventaId)

        return Result.success()
    }

    private fun mostrarNotificacionFactura(ventaId: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "canal_facturas_vet"

        // Los teléfonos modernos requieren crear un canal de notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Facturación Veterinaria",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificacion = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_menu_agenda) // Icono del sistema
            .setContentTitle("¡Factura Emitida con Éxito!")
            .setContentText("La factura de la venta $ventaId ya está registrada.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(ventaId.hashCode(), notificacion)
    }
}