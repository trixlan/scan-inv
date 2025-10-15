package com.gercha.scan_inv.Fragmentos

import android.os.Bundle
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
import kotlinx.coroutines.launch

class FragmentSettings : Fragment() {

    private lateinit var etPower: EditText
    private lateinit var etFrequency: EditText
    private lateinit var etProtocol: EditText
    private lateinit var etRF: EditText
    private lateinit var btnSave: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Inflate the layout for this fragment
        etPower = view.findViewById<EditText>(R.id.et_Power)
        etFrequency = view.findViewById<EditText>(R.id.et_Frequency)
        etProtocol = view.findViewById<EditText>(R.id.et_Protocol)
        etRF = view.findViewById<EditText>(R.id.et_RF)
        btnSave = view.findViewById<Button>(R.id.btn_Save)

        btnSave.setOnClickListener {
            saveSettings()
        }
    }

    fun toastMessage(msg: String?) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun saveSettings() {

        val myApp = requireActivity().application as MyApplication

        // 2. Usamos nuestro nuevo 'applicationScope' que es un CoroutineScope personalizado.
        val powerText = etPower.text.toString().trim()
        val frequencyText = etFrequency.text.toString().trim()
        val protocolText = etProtocol.text.toString().trim()
        val rfText = etRF.text.toString().trim()

        val powerInt: Int = powerText.toIntOrNull() ?: 0
        val frequencyInt: Int = frequencyText.toIntOrNull() ?: 0
        val protocolInt: Int = protocolText.toIntOrNull() ?: 0
        val rfInt: Int = rfText.toIntOrNull() ?: 0

        lifecycleScope.launch {
            myApp.saveSettings(powerInt, frequencyInt, protocolInt, rfInt)

            toastMessage("Guardando cambios..." + powerText + frequencyText + protocolText + rfText)
        }
    }
}