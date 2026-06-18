package com.proyecto.puntoventa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.proyecto.puntoventa.ui.InventarioScreen
import com.proyecto.puntoventa.ui.ReportesScreen
import com.proyecto.puntoventa.ui.VentaScreen
import com.proyecto.puntoventa.ui.theme.PuntoVentaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PuntoVentaTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Text("📦") },
                    label = { Text("Inventario") },
                    selected = false,
                    onClick = { navController.navigate("inventario") }
                )
                NavigationBarItem(
                    icon = { Text("🛒") },
                    label = { Text("Ventas") },
                    selected = false,
                    onClick = { navController.navigate("ventas") }
                )
                // NUEVO BOTÓN DE REPORTES
                NavigationBarItem(
                    icon = { Text("📊") },
                    label = { Text("Reportes") },
                    selected = false,
                    onClick = { navController.navigate("reportes") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "inventario",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("inventario") { InventarioScreen() }
            composable("ventas") { VentaScreen() }
            // NUEVA RUTA DE REPORTES
            composable("reportes") { ReportesScreen() }
        }
    }
}