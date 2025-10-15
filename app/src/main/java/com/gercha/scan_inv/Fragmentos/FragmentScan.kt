package com.gercha.scan_inv.Fragmentos

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.gercha.scan_inv.MyApplication
import com.gercha.scan_inv.R
import com.rscja.deviceapi.entity.UHFTAGInfo
import kotlinx.coroutines.launch

class FragmentScan : Fragment(R.layout.fragment_scan), KeyEventListener {

    private lateinit var etEPC: EditText
    private lateinit var etNoInv: EditText
    private lateinit var btnSend: Button

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

        btnSend.setOnClickListener {
            //
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

}