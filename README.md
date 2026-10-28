# 🍎 AlimentaRed - Plataforma Móvil de Rescate y Distribución Responsable de Alimentos

> **Proyecto Social y Comunitario** — Desarrollado en articulación con la **Universidad de Cundinamarca** para apoyar a ciudadanos y familias en condición de vulnerabilidad mediante la recolección, verificación sanitaria y redistribución transparente de excedentes de alimentos aptos para el consumo humano.

---

## 📌 Tabla de Contenidos
1. [Acerca del Proyecto](#-acerca-del-proyecto)
2. [Objetivos e Impacto Social](#-objetivos-e-impacto-social)
3. [Arquitectura Tecnológica](#-arquitectura-tecnológica)
4. [Estructura del Código (Arquitectura MVVM)](#-estructura-del-código-arquitectura-mvvm)
5. [Módulos y Requerimientos Funcionales (RF)](#-módulos-y-requerimientos-funcionales-rf)
6. [Reglas de Negocio y Control de Calidad (RN)](#-reglas-de-negocio-y-control-de-calidad-rn)
7. [Roles del Sistema y Usuarios de Prueba](#-roles-del-sistema-y-usuarios-de-prueba)
8. [Configuración del Servidor y Red LAN](#-configuración-del-servidor-y-red-lan)
9. [Instalación y Despliegue](#-instalación-y-despliegue)
10. [Licencia y Créditos](#-licencia-y-créditos)

---

## 📖 Acerca del Proyecto

**AlimentaRed** es una solución integral diseñada para conectar a donantes del sector comercial (supermercados, restaurantes, panaderías y plazas de mercado) con organizaciones receptoras sin fines de lucro (fundaciones, comedores comunitarios y hogares de paso). 

La plataforma combate el desperdicio alimentario garantizando la **seguridad alimentaria**, la **trazabilidad punta a punta** y el cumplimiento de rigurosos estándares de inocuidad y compromisos de retiro físico.

---

## 🎯 Objetivos e Impacto Social

* **Reducción del Desperdicio:** Rescate prioritario de lotes próximos a vencer a través del mecanismo de **Rescate Urgente**.
* **Equidad en la Distribución:** Mecanismos de asignación basados en necesidades reales, múltiplos y unidades mínimas establecidas por el donante.
* **Corresponsabilidad y Cumplimiento:** Sistema de **Strikes y Penalizaciones** para evitar inasistencias o acaparamientos injustificados.
* **Control Sanitario:** Protocolo inmediato de reporte y congelación cautelar de lotes ante incidencias de calidad o cadena de frío.
* **Métricas de Impacto:** Seguimiento en tiempo real de kilogramos rescatados, tasa de efectividad y entregas realizadas.

---

## 🛠️ Arquitectura Tecnológica

La aplicación móvil está construida bajo los más altos estándares modernos de desarrollo en **Android Nativo**:

| Componente | Tecnología | Descripción |
| :--- | :--- | :--- |
| **Lenguaje** | **Kotlin 2.0+** | Código conciso, seguro y expresivo con soporte a corrutinas. |
| **Interfaz de Usuario** | **Jetpack Compose & Material 3** | UI declarativa, reactiva, accesible y adaptativa con tema oscuro institucional. |
| **Arquitectura** | **MVVM (Model-View-ViewModel)** | Separación de responsabilidades con `StateFlow` y `viewModelScope`. |
| **Navegación** | **Navigation Compose 2.8+** | Flujo desacoplado entre onboarding, autenticación y pantallas operativas. |
| **Red & API REST** | **Retrofit 2 & OkHttp 3** | Consumo de servicios RESTful con serialización JSON vía Gson y Logging Interceptor. |
| **Seguridad de Sesión** | **JWT & SharedPreferences** | Almacenamiento seguro de credenciales con inyección automática de Bearer Token en `AuthInterceptor`. |
| **Compatibilidad** | **Min SDK 24 (Android 7.0) / Target SDK 37** | Soporte para una amplia base de dispositivos móviles físicos y emuladores. |

---

## 📂 Estructura del Código (Arquitectura MVVM)

```plaintext
app/src/main/java/com/example/alimentared/
├── MainActivity.kt                  # Punto de entrada y orquestador del grafo de navegación
├── data/
│   ├── model/
│   │   └── ApiModels.kt             # DTOs y modelos de datos (Lotes, Solicitudes, Strikes, Impacto, Auth)
│   ├── remote/
│   │   ├── ApiService.kt            # Contrato Retrofit de todos los endpoints de la API REST
│   │   ├── AuthInterceptor.kt      # Interceptor OkHttp que inyecta el token Bearer JWT
│   │   └── RetrofitClient           # Singleton del cliente HTTP con cambio dinámico de Base URL
│   └── session/
│       └── SessionManager.kt        # Gestión persistente del token JWT, usuario actual y host backend
├── ui/
│   ├── theme/                       # Paleta institucional (Azul Marino, Rojo Primario, Tipografía)
│   ├── components/                  # Componentes reutilizables
│   │   └── AlimentaRedBackground.kt # Fondos con degradados y marcas de agua temáticas (corazones, manos, eco, alimentos)
│   ├── screens/                     # Pantallas declarativas en Jetpack Compose
│   │   ├── OnboardingScreen.kt      # Presentación institucional y métricas de impacto iniciales
│   │   ├── LoginScreen.kt           # Autenticación, registro, chips de acceso rápido
│   │   ├── ConfigurarServidorDialog.kt # Diálogo de configuración de red y host del backend para admin
│   │   ├── MainAppScreen.kt         # Contenedor principal con barra de navegación inferior (5 pestañas)
│   │   ├── InicioTabScreen.kt       # Resumen general y bienvenida
│   │   ├── DonacionesTabScreen.kt   # Catálogo interactivo de lotes, filtros y modo Donante/Receptora
│   │   ├── LoteDetailScreen.kt      # Detalle técnico del lote, solicitud de donación y reporte
│   │   ├── PublishLoteScreen.kt     # Formulario de alta de nuevos lotes para donantes
│   │   ├── SolicitudesScreen.kt     # Gestión y resolución de peticiones de donación
│   │   ├── IncidenciasScreen.kt     # Reporte y resolución de alertas sanitarias/calidad
│   │   ├── PerfilScreen.kt          # Estadísticas de impacto, strikes activos y cierre de sesión
│   │   ├── DetalleStrikesScreen.kt  # Detalle de faltas/penalizaciones recibidas
│   │   └── PanelApelacionesScreen.kt# Formulario formal de apelación de sanciones
│   └── viewmodels/                  # ViewModels con lógica de presentación y StateFlows
│       ├── AuthViewModel.kt
│       ├── LotesViewModel.kt
│       ├── LoteDetailViewModel.kt
│       ├── PublishLoteViewModel.kt
│       ├── SolicitudesViewModel.kt
│       ├── IncidenciasViewModel.kt
│       └── PerfilImpactoViewModel.kt
```

---

## ⚙️ Módulos y Requerimientos Funcionales (RF)

| ID | Módulo | Descripción Funcional |
| :--- | :--- | :--- |
| **RF01** | **Autenticación y Registro** | Inicio de sesión mediante JWT, registro de organizaciones (Donante / Receptora) con NIT y datos de contacto. |
| **RF02** | **Gestión de Organizaciones** | Perfiles institucionales, validación de estado activo y consulta de sanciones vigentes. |
| **RF03** | **Publicación de Lotes** | Registro de excedentes alimentarios con categoría, fecha de caducidad, cantidad, múltiplos y reglas de entrega. |
| **RF04** | **Catálogo y Búsqueda** | Exploración pública de donaciones disponibles con filtrado por categoría, término de búsqueda y alerta de **Rescate Urgente**. |
| **RF05** | **Solicitudes de Donación** | Solicitud formal de alimentos por parte de fundaciones y comedores comunitarios con validación de volumen. |
| **RF06** | **Resolución de Peticiones** | Evaluación por parte del donante (aprobación/rechazo) con compromiso y reserva automática de inventario. |
| **RF07** | **Asignaciones y Entregas** | Instrucciones de recogida, confirmación de retiros físicos (parciales/totales) y reporte de no-asistencia. |
| **RF08** | **Control de Inocuidad** | Reporte inmediato de anomalías sanitarias, congelación cautelar del lote e intervención de coordinadores. |
| **RF09** | **Reportes de Impacto** | Estadísticas globales de recuperación, porcentaje de éxito, desperdicio evitado y auditoría de transiciones. |

---

## ⚖️ Reglas de Negocio y Control de Calidad (RN)

* **RN01 / RN04 — Trazabilidad y Auditoría:** Cada cambio de estado en lotes, solicitudes o entregas queda registrado con fecha, hora y responsable.
* **RN02 — Consistencia de Inventario:** No es posible solicitar volúmenes mayores al stock disponible ni comprometer inventario previamente asignado.
* **RN05 — Empaque y Múltiplos:** Las solicitudes deben ajustarse estrictamente a la unidad mínima y a los múltiplos de entrega fijados por el donante.
* **RN07 — Concurrencia de Solicitudes:** Límite máximo de solicitudes pendientes simultáneas por organización para garantizar un reparto equitativo.
* **RN08 — Sistema de 3 Strikes:** Si una organización acumula 3 strikes vigentes por incumplimientos (ej. no recoger donaciones aprobadas), su cuenta queda automáticamente **suspendida** e inhabilitada para operar.
* **RN11 — Franja de Retiro:** Cada asignación cuenta con una fecha y hora límite para el retiro físico en las instalaciones del donante.
* **RN12 — Sanción por Inasistencia:** La falta de retiro sin justificación previa genera automáticamente un strike a la organización receptora.

---

## 👥 Roles del Sistema y Usuarios de Prueba

Para agilizar pruebas y demostraciones, la pantalla inicial cuenta con un botón dedicado **"Cuentas y Modo de Pruebas"** que despliega chips de acceso rápido interactivos, mientras que el botón **"Ingresar al Portal de Acceso"** ofrece la experiencia de autenticación estándar de producción:

| Rol | Correo Electrónico | Contraseña | Propósito en el Flujo |
| :--- | :--- | :--- | :--- |
| **Receptora** | `receptora@pancompartido.org` | `Pass12345` | Explora catálogo, solicita donaciones y consulta retiros asignados. |
| **Donante** | `donante@saborcasero.com` | `Pass12345` | Publica lotes, aprueba solicitudes y valida la entrega física. |
| **Coordinador**| `coordinador@alimentared.com` | `Coord12345` | Resuelve alertas de inocuidad y supervisa el impacto global. |
| **Admin** | `admin@alimentared.com` | `Admin12345` | Gestión global del sistema, servidor y evaluación de apelaciones de strikes. |
| **Panadería Sol** | `suspendido@panaderiasol.com` | `Pass12345` | Cuenta donante suspendida / bloqueada por acumulación de strikes (RN08). |
| **Abuelitos de la Paz**| `suspendido@abuelitospaz.org` | `Pass12345` | Cuenta receptora con 2 strikes para verificar el seguimiento previo a la suspensión. |

---

## 🌐 Configuración del Servidor y Red LAN

La aplicación cuenta con un **Selector Dinámico de Servidor** accesible exclusivamente para el usuario **Administrador** a través del botón con el icono de corazón (❤️) ubicado junto al botón de cerrar sesión en la barra superior:

* **Emulador Oficial de Android:** Utiliza por defecto `http://10.0.2.2:8080/api/`.
* **Dispositivo Físico (vía Wi-Fi):** Configura la IP local de tu máquina de desarrollo (ej. `http://192.168.1.XX:8080/api/`).
* **Test de Conectividad Integrado:** El diálogo incluye un botón **"Probar Conexión"** que verifica el endpoint `/api/health` antes de guardar los cambios.

---

## 🚀 Instalación y Despliegue

### Requisitos Previos
1. **Android Studio** (versión Ladybug | Koala Feature Drop o superior).
2. **JDK 17** o superior configurado en Gradle.
3. Servidor Backend de **AlimentaRed (Spring Boot)** en ejecución y accesible en red.

### Pasos de Ejecución
1. Clonar el repositorio y abrir la carpeta raíz en Android Studio:
   ```bash
   git clone <url-del-repositorio>
   cd AlimentaRed
   ```
2. Sincronizar las dependencias con Gradle (`Sync Project with Gradle Files`).
3. Compilar el proyecto en modo depuración:
   ```bash
   ./gradlew assembleDebug
   ```
4. Ejecutar la aplicación sobre un emulador con Google Play Services o un dispositivo físico Android (Android 7.0+).

---

## 📜 Licencia y Créditos

Proyecto desarrollado como iniciativa de impacto social y comunitario en el marco académico de la **Universidad de Cundinamarca**. Prohibida su comercialización sin previa autorización institucional.
