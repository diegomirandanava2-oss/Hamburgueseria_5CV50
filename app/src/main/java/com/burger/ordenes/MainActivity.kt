package com.burger.ordenes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                var vistaActual by remember { mutableIntStateOf(0) } // 0 = Tomar Orden, 1 = Chef

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = if (vistaActual == 0) "Hamburguesería • Órdenes" else "Hamburguesería • Cocina (Chef)"
                                )
                            }
                        )
                    },
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = vistaActual == 0,
                                onClick = { vistaActual = 0 },
                                icon = { Icon(Icons.Default.Restaurant, contentDescription = "Tomar Orden") },
                                label = { Text("Tomar Orden") }
                            )
                            NavigationBarItem(
                                selected = vistaActual == 1,
                                onClick = { vistaActual = 1 },
                                icon = { Icon(Icons.Default.LocalFireDepartment, contentDescription = "Chef") },
                                label = { Text("Chef") }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        if (vistaActual == 0) {
                            VistaTomarOrden(onOrdenEnviada = {})
                        } else {
                            VistaChef()
                        }
                    }
                }
            }
        }
    }
}