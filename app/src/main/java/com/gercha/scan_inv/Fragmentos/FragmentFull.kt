package com.gercha.scan_inv.Fragmentos

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.gercha.scan_inv.MainActivity
import com.gercha.scan_inv.MyApplication
import com.gercha.scan_inv.R
import com.gercha.scan_inv.TagAdapter
import com.gercha.scan_inv.clases.Bien
import com.gercha.scan_inv.interfaces.KeyEventListener
import com.gercha.scan_inv.interfaces.OnTagReadListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class FragmentFull : Fragment(R.layout.fragment_full), KeyEventListener, OnTagReadListener {

    private lateinit var btnGet: Button
    private lateinit var btnClean: Button
    private var scan: Boolean = false
    private lateinit var lvTags: ListView
    private val listaBienes = ArrayList<Bien>()
    private lateinit var tagAdapter: TagAdapter

    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_full, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnGet = view.findViewById(R.id.btn_Get)
        btnClean = view.findViewById(R.id.btn_Clean)
        lvTags = view.findViewById(R.id.LvTags)

        // Inicializamos el adaptador
        tagAdapter = TagAdapter(requireContext(), listaBienes)
        // Asignamos el adaptador al ListView
        lvTags.adapter = tagAdapter

        // Registrarse en MyApplication
        (requireActivity().application as MyApplication).tagReadListener = this

        sharedPref = requireActivity().getSharedPreferences("ConfigurationApp", Context.MODE_PRIVATE)

        btnGet.setOnClickListener {
            getBienes()
        }

        btnClean.setOnClickListener {
            listaBienes.clear()
            tagAdapter.notifyDataSetChanged()
        }
    }

    fun toastMessage(msg: String?) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            293 -> {
                val myApp = requireActivity().application as MyApplication
                lifecycleScope.launch {
                    if (!scan) {
                        (activity as? MainActivity)?.bloquearPantalla()
                        myApp.leerVariasEtiquetas()
                        toastMessage("Iniciando escaneo...")
                        scan = true
                    } else {
                        (activity as? MainActivity)?.desbloquearPantalla()
                        myApp.detenerLectura()
                        toastMessage("Deteniendo escaneo...")
                        scan = false
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
        return false
    }

    // Este método se ejecutará automáticamente cuando el Handler de MyApplication reciba algo
    override fun onTagRead(epc: String) {
        // Evitar duplicados
        if (listaBienes.none { it.epc == epc }) {
            // AQUÍ: Deberías buscar en tu base de datos los datos reales.
            // Por ahora, pondremos datos de prueba:
            val nuevoProducto = Bien(
                epc = epc,
                noInventario = "",
                resguardante = "",
                descripcion = ""
            )
            listaBienes.add(0, nuevoProducto)
            // Actualizar la UI en el hilo principal
            tagAdapter.notifyDataSetChanged()
        }
    }

    private fun getBienes() {
        // Enviamos la lista completa de golpe
        val myApp = requireActivity().application as MyApplication
        val apiService = myApp.service
        if (apiService == null) {
            toastMessage("Error: URL no configurada correctamente")
            return
        }

        // Mostramos la capa de bloqueo que creamos antes
        (activity as? MainActivity)?.bloquearPantalla()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.enviarListaBienes(listaBienes)
                withContext(Dispatchers.Main) {
                    (activity as? MainActivity)?.desbloquearPantalla()

                    if (response.isSuccessful) {
                        val listaRecibida = response.body()
                        if (!listaRecibida.isNullOrEmpty()) {
                            // Actualizamos nuestra lista local con los datos que mandó el servidor
                            listaBienes.clear()
                            listaBienes.addAll(listaRecibida)
                            tagAdapter.notifyDataSetChanged()
                            toastMessage("Sincronización exitosa")
                        } else {
                            toastMessage("No se encontraron los registros")
                        }
                    } else {
                        toastMessage("Error del servidor: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    (activity as? MainActivity)?.desbloquearPantalla()
                    Log.e("ScannerBtn", "Error: ${e.message}")
                    toastMessage("Fallo de conexión")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Limpiamos la referencia para evitar fugas de memoria
        val myApp = requireActivity().application as MyApplication
        myApp.tagReadListener = null
    }

}