package com.proyecto.puntoventa.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.provider.MediaStore
import android.widget.Toast
import com.proyecto.puntoventa.model.ItemVenta
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun generarYGuardarTicketImagen(context: Context, ventaId: String, items: List<ItemVenta>, total: Double) {
    // 1. Configurar el tamaño del lienzo (Canvas)
    val ancho = 600
    // El alto es dinámico: crece dependiendo de cuántos productos se vendieron
    val alto = 400 + (items.size * 50)
    val bitmap = Bitmap.createBitmap(ancho, alto, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Fondo del ticket en color blanco
    canvas.drawColor(Color.WHITE)

    // 2. Configurar los "pinceles" para el texto
    val paintTitulo = Paint().apply {
        color = Color.BLACK
        textSize = 40f
        isFakeBoldText = true
        textAlign = Paint.Align.CENTER
    }
    val paintTexto = Paint().apply {
        color = Color.BLACK
        textSize = 30f
    }
    val paintTotal = Paint().apply {
        color = Color.BLACK
        textSize = 36f
        isFakeBoldText = true
        textAlign = Paint.Align.RIGHT
    }

    // 3. Dibujar el contenido del ticket
    canvas.drawText("VETERINARIA PLATANIN", ancho / 2f, 80f, paintTitulo)

    val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
    canvas.drawText("Fecha: $fecha", 50f, 150f, paintTexto)
    canvas.drawText("Folio: $ventaId", 50f, 200f, paintTexto)

    // Línea separadora
    canvas.drawLine(50f, 230f, ancho - 50f, 230f, paintTexto)

    // Imprimir cada producto
    var y = 290f
    items.forEach { item ->
        canvas.drawText("${item.cantidad}x ${item.nombreProducto}", 50f, y, paintTexto)

        val subtotalStr = "$${item.subtotal}"
        // Alinear el precio a la derecha
        canvas.drawText(subtotalStr, ancho - 50f - paintTexto.measureText(subtotalStr), y, paintTexto)
        y += 50f
    }

    // Línea separadora final
    canvas.drawLine(50f, y + 10f, ancho - 50f, y + 10f, paintTexto)
    y += 70f

    // Imprimir el Total
    canvas.drawText("TOTAL: $$total", ancho - 50f, y, paintTotal)

    // 4. Guardar la imagen generada en la galería del dispositivo
    guardarEnGaleria(context, bitmap, ventaId)
}

private fun guardarEnGaleria(context: Context, bitmap: Bitmap, ventaId: String) {
    val filename = "Ticket_$ventaId.png"
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        // Se guardará dentro de una carpeta específica en tus imágenes
        put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/TicketsVeterinaria")
    }

    val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    uri?.let {
        context.contentResolver.openOutputStream(it)?.use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            Toast.makeText(context, "Ticket guardado en la Galería", Toast.LENGTH_LONG).show()
        }
    }
}