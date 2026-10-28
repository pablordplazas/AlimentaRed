package com.example.alimentared.util

/**
 * Remueve visualmente cualquier mención de códigos de requerimientos o reglas de negocio
 * (como (RF08), (RN08), (RN05/RN10), RN08:, etc.) tanto en textos estáticos como dinámicos del backend.
 */
fun String.removerCodigosRegla(): String {
    return this
        .replace(Regex("""\s*\(\s*(?:RF|RN)\d+(?:\s*[/,yey-]\s*(?:RF|RN)\d+)*\s*\)""", RegexOption.IGNORE_CASE), "")
        .replace(Regex("""\b(?:RF|RN)\d+\s*:\s*""", RegexOption.IGNORE_CASE), "")
        .trim()
}
