package com.gercha.scan_inv.Fragmentos

import android.os.Bundle
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
import com.rscja.deviceapi.entity.UHFTAGInfo
import kotlinx.coroutines.launch

class FragmentFull : Fragment(R.layout.fragment_full), KeyEventListener, OnTagReadListener {

    private lateinit var btnGet: Button
    private lateinit var btnClean: Button
    private var scan: Boolean = false
    private lateinit var lvTags: ListView
    private val listaEpc = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>

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
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, listaEpc)
        // Asignamos el adaptador al ListView
        lvTags.adapter = adapter

        // IMPORTANTE: Decirle a MyApplication que este fragmento quiere los datos
        val myApp = requireActivity().application as MyApplication
        myApp.tagReadListener = this

        btnGet.setOnClickListener {
            // Llamar al sistema para traer los datos de los bienes
        }

        btnClean.setOnClickListener {
            listaEpc.clear()
            adapter.notifyDataSetChanged()
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
        if (!listaEpc.contains(epc)) {
            listaEpc.add(0, epc)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Limpiamos la referencia para evitar fugas de memoria
        val myApp = requireActivity().application as MyApplication
        myApp.tagReadListener = null
    }

}