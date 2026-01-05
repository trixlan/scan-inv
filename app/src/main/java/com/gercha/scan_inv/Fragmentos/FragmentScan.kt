package com.gercha.scan_inv.Fragmentos

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.gercha.scan_inv.MyApplication
import com.gercha.scan_inv.R
import com.rscja.deviceapi.entity.UHFTAGInfo
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class FragmentScan : Fragment(R.layout.fragment_scan), KeyEventListener {

    private lateinit var etEPC: EditText
    private lateinit var etNoInv: EditText
    private lateinit var btnSend: Button
    private lateinit var btnCheck: Button

    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inflate the layout for this fragment
        etEPC = view.findViewById<EditText>(R.id.et_EPC)
        etNoInv = view.findViewById<EditText>(R.id.et_NoInv)
        btnSend = view.findViewById<Button>(R.id.btn_Send)
        btnCheck = view.findViewById<Button>(R.id.btn_Check)

        sharedPref = requireActivity().getSharedPreferences("ConfigurationApp", Context.MODE_PRIVATE)

        btnSend.setOnClickListener {
            sendRFID()
        }

        btnCheck.setOnClickListener {
            validateRFID()
        }
    }

    fun toastMessage(msg: String?) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        var success = false

        when (keyCode) {
            293 -> {
                val myApp = requireActivity().application as MyApplication
                lifecycleScope.launch {
                    val etiquetaInfo: UHFTAGInfo? = myApp.leerUnaEtiqueta()

                    // El resultado se procesa de vuelta en el hilo principal
                    if (etiquetaInfo != null) {
                        // ¡Éxito! Encontramos una etiqueta.
                        etEPC.setText(etiquetaInfo.epc)
                        toastMessage("Lectura exitosa")
                    } else {
                        // No se encontró ninguna etiqueta o hubo un error.
                        Log.w("ScannerBtn", "No se encontró ninguna etiqueta.")
                        toastMessage("Fallo al leer la etiqueta")
                    }
                }
                // por ejemplo: escaneo terminado
                return true
            }
            else -> {
                // también puedes usar event.scanCode para mapeos específicos del hardware
                Log.d("ScannerBtn", "keyCode=$keyCode scan=${event.scanCode}")
            }
        }
        return success
    }

    private fun sendRFID() {
        val norfid: String = etEPC.getText().toString()
        val noInv = etNoInv.text.toString()
        val payload = "{\"noInventario\":\"$noInv\", \"norfid\":\"$norfid\"}"
        val url = sharedPref.getString("url", "http://201.96.185.X:3100")

        //UIHelper.ToastMessage(mContext, payload);
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        try {
            val url = URL("$url/bien_rfid")
            val httpCon = url.openConnection() as HttpURLConnection
            httpCon.doOutput = true
            httpCon.requestMethod = "PUT"
            httpCon.setRequestProperty("Content-Type", "application/json")
            httpCon.setRequestProperty("Accept", "application/json")
            httpCon.outputStream.use { os ->
                val input = payload.toByteArray(StandardCharsets.UTF_8)
                os.write(input, 0, input.size)
            }
            val responseCode = httpCon.responseCode
            if (responseCode == 200) {
                toastMessage("OK")
            } else {
                toastMessage("Error")
            }
        } catch (e: Exception) {
            toastMessage(
                e.message + " " + e.toString() + " " + e.localizedMessage
            )
            Log.d("ScannerBtn", e.message + " " + e.toString() + " " + e.localizedMessage)
        }
    }

    private fun validateRFID() {
        val norfid: String = etEPC.getText().toString()
        val url = sharedPref.getString("url", "http://201.96.185.X:3100")

        if(norfid != "") {
            val payload = "{\"norfid\":\"$norfid\"}"

            // UIHelper.ToastMessage(mContext, payload);
            val policy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            try {
                val url = URL("$url/bien_validar_rfid")
                val httpCon = url.openConnection() as HttpURLConnection
                httpCon.doOutput = true
                httpCon.requestMethod = "PUT"
                httpCon.setRequestProperty("Content-Type", "application/json")
                httpCon.setRequestProperty("Accept", "application/json")
                httpCon.outputStream.use { os ->
                    val input = payload.toByteArray(StandardCharsets.UTF_8)
                    os.write(input, 0, input.size)
                }
                val responseCode = httpCon.responseCode
                val responseJson: String
                if (responseCode == 200) {
                    responseJson = httpCon.inputStream.bufferedReader().use { it.readText() }
                    val jsonObject = org.json.JSONObject(responseJson)
                    val response = jsonObject.getString("respuesta")
                    toastMessage(response)
                } else {
                    toastMessage("Error")
                }
            } catch (e: Exception) {
                toastMessage(
                    e.message + " " + e.toString() + " " + e.localizedMessage
                )
                Log.d("ScannerBtn", e.message + " " + e.toString() + " " + e.localizedMessage)
            }
        }
    }
}