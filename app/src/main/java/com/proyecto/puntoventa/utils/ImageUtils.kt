package com.proyecto.puntoventa.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream

// Convierte la URI de la galería a un String Base64 comprimido
fun uriABase64(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)

        val outputStream = ByteArrayOutputStream()
        // COMPRESIÓN CRÍTICA: Reducimos la calidad al 20% para que quepa en Firestore
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outputStream)

        val bytes = outputStream.toByteArray()
        Base64.encodeToString(bytes, Base64.DEFAULT)
    } catch (e: Exception) {
        null
    }
}

// Convierte el String Base64 de vuelta a un Bitmap para que Compose lo dibuje
fun base64ABitmap(base64Str: String): Bitmap? {
    return try {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        null
    }
}