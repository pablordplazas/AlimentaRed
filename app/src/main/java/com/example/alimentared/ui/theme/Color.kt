package com.example.alimentared.ui.theme

import androidx.compose.ui.graphics.Color

// Paleta Oficial Eco Abasto / AlimentaRed
val GreenSilver = Color(0xFF8EA89D)         // Verde silver (Silver Green) para el isotipo AlimentaRed
val EcoForestGreen = Color(0xFF02542D)       // Verde bosque intenso (Botones principales, FAB, acciones clave)
val EcoForestDark = Color(0xFF013B1F)        // Verde bosque profundo
val EcoOliveGreen = Color(0xFF42552C)        // Verde oliva institucional (Arco de bienvenida, títulos)
val EcoSageGreen = Color(0xFFBEC69D)         // Verde salvia terroso (Botones secundarios, editar, cerrar sesión)
val EcoSageDark = Color(0xFF8A9A68)          // Salvia oscuro bordes
val EcoMintGreen = Color(0xFFCFF7D3)         // Verde menta suave (Píldora activa navbar, banners de éxito, chips)
val EcoMintLight = Color(0xFFEAF9ED)         // Fondo menta tenue
val EcoAmber = Color(0xFFF7A503)             // Ámbar / naranja cálido (Vencimiento, distancia, alertas)
val EcoAmberDark = Color(0xFFB45309)         // Ámbar oscuro
val EcoBackground = Color(0xFFF9FAFB)        // Fondo general limpio
val EcoSurface = Color(0xFFFFFFFF)           // Superficie de tarjetas e inputs
val EcoBorder = Color(0xFFE5E7EB)            // Bordes y separadores sutiles
val EcoTextPrimary = Color(0xFF111827)       // Texto negro carbón principal
val EcoTextMuted = Color(0xFF6B7280)         // Texto secundario grisáceo
val EcoRed = Color(0xFFDC2626)               // Rojo de error / strike
val EcoRedLight = Color(0xFFFEE2E2)          // Fondo rojo suave avisos

// Mapeos de compatibilidad con el sistema anterior (AlimentaRed -> Eco Abasto)
val RedPrimary = EcoForestGreen              // Acción principal ahora es Verde Bosque
val RedDark = EcoOliveGreen                  // Acento secundario es Verde Oliva
val BlueDark = EcoOliveGreen                 // Encabezados institucionales en Verde Oliva
val BlueCard = Color(0xFF2C3E2D)             // Superficie oscura institucional
val TextMuted = EcoTextMuted                 // Gris lectura
val BackgroundLight = EcoBackground          // Fondo claro
