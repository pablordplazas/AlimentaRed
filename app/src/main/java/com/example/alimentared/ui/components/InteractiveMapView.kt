package com.example.alimentared.ui.components

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.alimentared.ui.theme.EcoBackground
import com.example.alimentared.ui.theme.EcoForestGreen
import java.util.Locale

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun InteractiveMapView(
    latitud: Double,
    longitud: Double,
    modifier: Modifier = Modifier,
    zoom: Int = 16,
    esModoSeleccion: Boolean = false,
    tituloMarcador: String = "Punto de Entrega",
    subtituloMarcador: String = "",
    onCoordenadasCambiadas: ((Double, Double) -> Unit)? = null
) {
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var estaCargando by remember { mutableStateOf(true) }
    var mapaListo by remember { mutableStateOf(false) }

    // Actualizar posición en JavaScript cuando cambian coordenadas en Compose
    LaunchedEffect(latitud, longitud, mapaListo) {
        if (mapaListo && webViewRef != null) {
            val jsCall = String.format(
                Locale.US,
                "if (window.updatePosition) { window.updatePosition(%.5f, %.5f, '%s', '%s'); }",
                latitud,
                longitud,
                tituloMarcador.replace("'", "\\'"),
                subtituloMarcador.replace("'", "\\'")
            )
            webViewRef?.evaluateJavascript(jsCall, null)
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(EcoBackground)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        allowFileAccess = true
                        allowContentAccess = true
                        databaseEnabled = true
                        setSupportZoom(true)
                        builtInZoomControls = false
                        displayZoomControls = false
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        userAgentString = "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Mobile Safari/537.36 AlimentaRed/1.0"
                    }

                    val bridge = object {
                        val handler = Handler(Looper.getMainLooper())

                        @JavascriptInterface
                        fun onLocationMoved(lat: Double, lng: Double) {
                            handler.post {
                                onCoordenadasCambiadas?.invoke(lat, lng)
                            }
                        }

                        @JavascriptInterface
                        fun onMapReady() {
                            handler.post {
                                mapaListo = true
                                estaCargando = false
                            }
                        }
                    }

                    addJavascriptInterface(bridge, "AndroidMapBridge")

                    webChromeClient = object : WebChromeClient() {
                        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                            Log.d("AlimentaRedMap", "${consoleMessage?.message()} [${consoleMessage?.sourceId()}:${consoleMessage?.lineNumber()}]")
                            return true
                        }
                    }

                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            // Inicializar mapa con las coordenadas proporcionadas
                            val jsCall = String.format(
                                Locale.US,
                                "initMap(%.5f, %.5f, %d, %b, '%s', '%s');",
                                latitud,
                                longitud,
                                zoom,
                                esModoSeleccion,
                                tituloMarcador.replace("'", "\\'"),
                                subtituloMarcador.replace("'", "\\'")
                            )
                            view?.evaluateJavascript(jsCall) {
                                estaCargando = false
                                mapaListo = true
                            }
                        }
                    }

                    loadUrl("file:///android_asset/leaflet/mapa.html")
                    webViewRef = this
                }
            },
            update = { webView ->
                // Mantener referencia activa
            }
        )

        if (estaCargando) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(EcoBackground.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = EcoForestGreen,
                    modifier = Modifier.size(32.dp),
                    strokeWidth = 3.dp
                )
            }
        }

        // Badge informativo en modo selección
        if (esModoSeleccion) {
            Surface(
                shape = RoundedCornerShape(50),
                color = EcoForestGreen.copy(alpha = 0.92f),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.TouchApp, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Toca o arrastra el pin", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
