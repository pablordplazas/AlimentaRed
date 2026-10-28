package com.example.alimentared.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alimentared.data.model.LoteResponse
import com.example.alimentared.data.model.TipoAlimentoResponse
import com.example.alimentared.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ModoVistaLotes {
    MIS_PUBLICACIONES,
    CATALOGO_GENERAL
}

enum class FiltroEstadoMisLotes(val estadoBackend: String?, val label: String) {
    TODAS(null, "Todas"),
    BORRADORES("BORRADOR", "Borradores"),
    PUBLICADAS("DISPONIBLE", "Publicadas")
}

data class LotesUiState(
    val isLoading: Boolean = false,
    val lotes: List<LoteResponse> = emptyList(),
    val categorias: List<TipoAlimentoResponse> = emptyList(),
    val categoriaSeleccionada: Long? = null,
    val soloUrgentes: Boolean = false,
    val searchQuery: String = "",
    val modoVista: ModoVistaLotes = ModoVistaLotes.CATALOGO_GENERAL,
    val filtroEstadoMisLotes: FiltroEstadoMisLotes = FiltroEstadoMisLotes.TODAS,
    val error: String? = null
)

class LotesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LotesUiState())
    val uiState: StateFlow<LotesUiState> = _uiState.asStateFlow()

    init {
        cargarCategorias()
        cargarLotes()
    }

    fun setModoVista(modo: ModoVistaLotes) {
        if (_uiState.value.modoVista != modo) {
            _uiState.value = _uiState.value.copy(modoVista = modo)
            cargarLotes()
        }
    }

    fun setFiltroEstadoMisLotes(filtro: FiltroEstadoMisLotes) {
        _uiState.value = _uiState.value.copy(filtroEstadoMisLotes = filtro)
        cargarLotes()
    }

    fun cargarCategorias() {
        viewModelScope.launch {
            try {
                val res = RetrofitClient.instance.getCategorias()
                if (res.isSuccessful && res.body() != null) {
                    _uiState.value = _uiState.value.copy(categorias = res.body()!!)
                }
            } catch (ignored: Exception) {}
        }
    }

    fun cargarLotes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                if (_uiState.value.modoVista == ModoVistaLotes.MIS_PUBLICACIONES) {
                    val estadoParam = _uiState.value.filtroEstadoMisLotes.estadoBackend
                    val res = RetrofitClient.instance.getMisLotes(
                        estado = estadoParam,
                        page = 0,
                        size = 50
                    )
                    if (res.isSuccessful && res.body() != null) {
                        var lista = res.body()!!.contenido
                        val query = _uiState.value.searchQuery.trim().lowercase()
                        if (query.isNotBlank()) {
                            lista = lista.filter {
                                it.nombre.lowercase().contains(query) ||
                                it.descripcion.lowercase().contains(query) ||
                                (it.categoriaNombre?.lowercase()?.contains(query) == true)
                            }
                        }
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            lotes = lista
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "No se pudieron obtener tus publicaciones (${res.code()})"
                        )
                    }
                } else {
                    val catId = _uiState.value.categoriaSeleccionada
                    val urgentes = if (_uiState.value.soloUrgentes) true else null
                    val query = if (_uiState.value.searchQuery.isBlank()) null else _uiState.value.searchQuery.trim()

                    val res = RetrofitClient.instance.getLotes(
                        categoriaId = catId,
                        rescateUrgente = urgentes,
                        query = query,
                        page = 0,
                        size = 50
                    )
                    if (res.isSuccessful && res.body() != null) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            lotes = res.body()!!.contenido
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "No se pudieron obtener los lotes disponibles (${res.code()})"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Fallo de conexión: ${e.localizedMessage}"
                )
            }
        }
    }

    fun seleccionarCategoria(categoriaId: Long?) {
        val nueva = if (_uiState.value.categoriaSeleccionada == categoriaId) null else categoriaId
        _uiState.value = _uiState.value.copy(categoriaSeleccionada = nueva)
        cargarLotes()
    }

    fun toggleUrgentes() {
        _uiState.value = _uiState.value.copy(soloUrgentes = !_uiState.value.soloUrgentes)
        cargarLotes()
    }

    fun actualizarBusqueda(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun ejecutarBusqueda() {
        cargarLotes()
    }
}
