package com.gercha.scan_inv.clases

data class Bien (
    var epc: String? = null,
    val noInventario: String? = null,
    val descripcion: String? = null,
    val resguardante: String? = null
)