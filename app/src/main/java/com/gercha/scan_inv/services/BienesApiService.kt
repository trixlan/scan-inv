package com.gercha.scan_inv.services

import com.gercha.scan_inv.clases.Bien
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT

interface BienesApiService {

    @PUT("bien/obtener_bienesByRfid")
    suspend fun enviarListaBienes(@Body lista: ArrayList<Bien>): Response<List<Bien>>
}