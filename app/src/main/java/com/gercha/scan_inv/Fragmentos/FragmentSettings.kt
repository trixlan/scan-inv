package com.gercha.scan_inv.Fragmentos

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.gercha.scan_inv.MyApplication
import com.gercha.scan_inv.R
import kotlinx.coroutines.launch
import com.gercha.scan_inv.BuildConfig

class FragmentSettings : Fragment() {

    private lateinit var etPower: EditText
    private lateinit var etFrequency: EditText
    private lateinit var etUrl: EditText
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
        // Usa requireActivity() para asegurar que el contexto no sea nulo
        val sharedPref = requireActivity().getSharedPreferences("ConfigurationApp", Context.MODE_PRIVATE)

        val power = sharedPref.getInt("power_rfid", 1)
        val frequency = sharedPref.getInt("frequency_rfid", 8)
        val url = sharedPref.getString("url", "http://201.96.185.X:3100")

        // Inflate the layout for this fragment
        etPower = view.findViewById<EditText>(R.id.et_Power)
        etFrequency = view.findViewById<EditText>(R.id.et_Frequency)
        etUrl = view.findViewById<EditText>(R.id.et_Url)
        btnSave = view.findViewById<Button>(R.id.btn_Save)

        Log.i("ScannerBtn", "Settings Power: $power Frequency: $frequency")
        etPower.setText(power.toString())
        etFrequency.setText(frequency.toString())
        etUrl.setText(url.toString())

        // Agregamos la version y el versionCode
        val versionTextView = view.findViewById<TextView>(R.id.tv_Version)
        val versionName: String = BuildConfig.VERSION_NAME
        val versionCode: Int = BuildConfig.VERSION_CODE

        versionTextView.text = "Version: $versionName ($versionCode)"

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
        val urlText = etUrl.text.toString().trim()

        val powerInt: Int = powerText.toIntOrNull() ?: 0
        val frequencyInt: Int = frequencyText.toIntOrNull() ?: 0
        val url: String = urlText

        lifecycleScope.launch {
            // Guadamos la configuracion
            myApp.saveSettings(powerInt, frequencyInt, url)

            toastMessage(
                """
                Guardando cambios... 
                Power: $powerText 
                Frequency: $frequencyText 
                Url: $url
                """.trimIndent())

        }
    }
}