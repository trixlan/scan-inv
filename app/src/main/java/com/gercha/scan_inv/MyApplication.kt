package com.gercha.scan_inv

import android.app.Application
import android.util.Log
import com.rscja.deviceapi.RFIDWithUHFUART
import com.rscja.deviceapi.entity.UHFTAGInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyApplication : Application() {

    // 1. Creamos un CoroutineScope personalizado para toda la aplicación.
    // SupervisorJob() asegura que si una tarea falla, no cancelará todo el scope.
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // La instancia del lector será accesible desde toda la app.
    var mReader: RFIDWithUHFUART? = null
        private set // 'private set' evita que se pueda modificar desde fuera.

    override fun onCreate() {
        super.onCreate()
        Log.d("ScannerBtn", "¡La clase MyApplication se ha iniciado correctamente!")

        // Inicializamos el lector aquí, una única vez en la vida de la app.
        initUHF()
    }

    var isReaderStuck = false
        private set // Solo se puede modificar desde dentro de la clase.

    private fun initUHF() {
        try {
            mReader = RFIDWithUHFUART.getInstance()
        } catch (ex: Exception) {
            // Manejar error si el hardware no está disponible.
            Log.d("ScannerBtn", "Fallo al instanciarse", ex)
            return
        }

        // 2. Usamos nuestro nuevo 'applicationScope' que es un CoroutineScope personalizado.
        applicationScope.launch(Dispatchers.IO) {
            var success = false;
            if(mReader != null) {
                success = mReader!!.init()
            }

            if (success == true) {
                // El lector se inicializó correctamente en segundo plano.
                Log.i("ScannerBtn", "El lector inicio correctamente")

                // Establecemos la potencia por defecto
                Log.i("ScannerBtn", "Power " + mReader?.getPower().toString())
                val powerSet = mReader?.setPower(30)
                if (powerSet == true) {
                    Log.i("ScannerBtn", "Potencia por defecto establecida a 30.")
                } else {
                    Log.e("ScannerBtn", "Falló al establecer la potencia por defecto.")
                }
                // Establecemos la fecuencia por defecto
                Log.i("ScannerBtn", "Frequency " + mReader?.getFrequencyMode().toString())
                val freqSet = mReader?.setFrequencyMode(0x08)
                if (freqSet == true) {
                    Log.i("ScannerBtn", "Frecuencia por defecto establecida a United States Standard(902~928MHz)")
                } else {
                    Log.e("ScannerBtn", "Falló al establecer la frecuencia por defecto.")
                }
                // Establecemos el protocolo por defecto
                Log.i("ScannerBtn", "Protocol " + mReader?.getProtocol().toString())
                val protSet = mReader?.setProtocol(0)
                if (protSet == true) {
                    Log.i("ScannerBtn", "Protocolo por defecto establecido ISO 18000-6C")
                } else {
                    Log.e("ScannerBtn", "Falló al establecer el protocolo por defecto.")
                }
                // Establecemos el RF por defecto
                Log.i("ScannerBtn", "RF " + mReader?.getRFLink().toString())
                val rfSet = mReader?.setRFLink(0)
                if (rfSet == true) {
                    Log.i("ScannerBtn", "RF por defecto establecido DSB_ASK/FM0/40KHz")
                } else {
                    Log.e("ScannerBtn", "Falló al establecer el RF por defecto.")
                }
            } else {
                // El lector falló al inicializarse..
                Log.d("ScannerBtn", "Fallo al iniciarse")
            }
        }
    }

    suspend fun leerUnaEtiqueta(): UHFTAGInfo? {
        val uhfTagInfo = withContext(Dispatchers.IO) {
            mReader?.inventorySingleTag()
        }

        // Si la lectura falló (devolvió null), llamamos a nuestra nueva función de reseteo.
        if (uhfTagInfo == null) {
            Log.w("SccannerBtn", "Lectura fallida.")
        }

        return uhfTagInfo
    }

    suspend fun resetReader(): Boolean {
        return withContext(Dispatchers.IO) {
            // Paso 1: Liberar el lector si ya existe.
            mReader?.free()
            // Paso 2: Volver a inicializar.
            var start: Boolean = false
            try{
                mReader = RFIDWithUHFUART.getInstance()
                val success = mReader?.init()
                start = success == true
                if (success == true) {
                    // Aquí podrías volver a aplicar tus configuraciones por defecto si es necesario
                    mReader?.setPower(30)
                    mReader?.setFrequencyMode(0x08)
                } else {
                    Log.e("ScannerBtn", "Falló la reinicialización del lector.")
                }
            } catch (ex: Exception) {
                Log.d("ScannerBtn", "Fallo al instanciarse", ex)
                return@withContext false
            }
            start
        }
    }

    suspend fun saveSettings(power: Int, frequency: Int, protocol: Int, rf: Int) {
        Log.i("ScannerBtn", "Guardando cambios...")
        // 2. Usamos nuestro nuevo 'applicationScope' que es un CoroutineScope personalizado.
        applicationScope.launch(Dispatchers.IO) {
            var success = false;
            if(mReader != null) {
                success = mReader!!.init()
            }

            if (success == true) {
                // Establecemos la potencia por defecto
                Log.i("ScannerBtn", "Power " + mReader?.getPower().toString() + " " + power.toString())
                val powerSet = mReader?.setPower(power)
                if (powerSet == true) {
                    Log.i("ScannerBtn", "Potencia por defecto establecida a 30.")
                } else {
                    Log.e("ScannerBtn", "Falló al establecer la potencia por defecto.")
                }
                // Establecemos la fecuencia por defecto
                Log.i("ScannerBtn", "Frequency " + mReader?.getFrequencyMode().toString() + " " + frequency.toString())
                val freqSet = mReader?.setFrequencyMode(frequency)
                if (freqSet == true) {
                    Log.i("ScannerBtn", "Frecuencia por defecto establecida a United States Standard(902~928MHz)")
                } else {
                    Log.e("ScannerBtn", "Falló al establecer la frecuencia por defecto.")
                }
                // Establecemos el protocolo por defecto
                Log.i("ScannerBtn", "Protocol " + mReader?.getProtocol().toString() + " " + protocol.toString())
                val protSet = mReader?.setProtocol(protocol)
                if (protSet == true) {
                    Log.i("ScannerBtn", "Protocolo por defecto establecido ISO 18000-6C")
                } else {
                    Log.e("ScannerBtn", "Falló al establecer el protocolo por defecto.")
                }
                // Establecemos el RF por defecto
                Log.i("ScannerBtn", "RF " + mReader?.getRFLink().toString() + " " + rf.toString())
                val rfSet = mReader?.setRFLink(rf)
                if (rfSet == true) {
                    Log.i("ScannerBtn", "RF por defecto establecido DSB_ASK/FM0/40KHz")
                } else {
                    Log.e("ScannerBtn", "Falló al establecer el RF por defecto.")
                }
            } else {
                // El lector falló al inicializarse..
                Log.d("ScannerBtn", "Fallo al iniciarse")
            }
        }
    }
}