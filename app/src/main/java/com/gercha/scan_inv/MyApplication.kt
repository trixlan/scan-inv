package com.gercha.scan_inv

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.gercha.scan_inv.Fragmentos.OnTagReadListener
import com.rscja.deviceapi.RFIDWithUHFUART
import com.rscja.deviceapi.entity.UHFTAGInfo
import com.rscja.deviceapi.interfaces.IUHFInventoryCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// La clase Application es la madre de todos los componentes de Android
// al ser MyApplication una instancia de Application se ejecutara desde el principio de la app
// para inicializar el lector RFID
class MyApplication : Application() {

    // Variable para guardar el fragmento que esté escuchando
    var tagReadListener: OnTagReadListener? = null

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
            // Inicializa el lector de RFID
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

    suspend fun leerVariasEtiquetas() {
        Log.i("ScannerBtn", "Iniciando Scaneo Continuo...")
        mReader?.setInventoryCallback(IUHFInventoryCallback { uhftagInfo ->
            val msg = handler.obtainMessage()
            msg.obj = uhftagInfo
            msg.what = 1
            // Envia el mensaje a nuestro handler.
            handler.sendMessage(msg)
        })
        // 2. ¡ARRANCAR EL HARDWARE!
        val success = mReader?.startInventoryTag() ?: false
        if (success) {
            Log.i("ScannerBtn", "Lector encendido correctamente")
        } else {
            Log.e("ScannerBtn", "Error al encender el hardware del lector")
        }
    }

    fun detenerLectura() {
        mReader?.stopInventory()
        Log.i("ScannerBtn", "Lectura detenida")
    }

    // Se inicializa apuntando al MainLooper para evitar el 'deprecated'
    val handler = Handler(Looper.getMainLooper()) { msg ->
        when (msg.what) {
            1 -> {
                val info = msg.obj as UHFTAGInfo
                val epc = info.epc

                // ¡Aquí está el truco! Le avisamos al que esté escuchando
                tagReadListener?.onTagRead(epc)
                Log.i("ScannerBtn", "MyApplication recibió: $epc")
            }
        }
        true // Indica que el mensaje fue manejado
    }

    suspend fun resetReader(): Boolean {
        val sharedPref = getSharedPreferences("ConfigurationApp", Context.MODE_PRIVATE)
        val power = sharedPref.getInt("power_rfid", 1);
        val frequency = sharedPref.getInt("frequency_rfid", 8);

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
                    mReader?.setPower(power)
                    mReader?.setFrequencyMode(frequency)
                    Log.i("ScannerBtn","Power: " + power + " Frecuendy: " + frequency);
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

    suspend fun saveSettings(power: Int, frequency: Int, url: String) {
        Log.i("ScannerBtn", "Guardando cambios...")

        // Guardamos los parametros de forma permanente
        val sharedPref = getSharedPreferences("ConfigurationApp", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        editor.putInt("power_rfid", power)
        editor.putInt("frequency_rfid", frequency)
        editor.putString("url", url)
        editor.apply() // Guarda de forma asíncrona
    }

}