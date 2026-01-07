package com.gercha.scan_inv

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gercha.scan_inv.Fragmentos.FragmentSettings
import com.gercha.scan_inv.Fragmentos.FragmentScan
import com.gercha.scan_inv.Fragmentos.FragmentFull
import com.gercha.scan_inv.Fragmentos.KeyEventListener
import com.gercha.scan_inv.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var layoutBloqueo: View

    // Solo se ejecuta una vez cuando se inicia la app
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        layoutBloqueo = findViewById(R.id.layout_bloqueo)

        // Fragmento por defecto
        verFragmentoScan()
        // Se agregan los iconos debajo de la pantalla para cada funcion
        binding.bottomNV.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_scan -> {
                    //Visualizar el fragmento Perfil
                    verFragmentoScan()
                    true
                }

                R.id.item_full -> {
                    //Visualizar el fragmento Usuarios
                    verFragmentoFull()
                    true
                }

                R.id.item_settings -> {
                    //Visualizar el fragmento Chats
                    verFragmentoSettings()
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    fun bloquearPantalla() {
        layoutBloqueo.visibility = View.VISIBLE
    }

    fun desbloquearPantalla() {
        layoutBloqueo.visibility = View.GONE
    }

    private fun irOpcionesLogin() {
        startActivity(Intent(applicationContext, OpcionesLoginActivity::class.java))
    }


    private fun verFragmentoScan() {
        binding.tvTitulo.text = "Scan"

        val fragment = FragmentScan()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentoFL.id, fragment, "Fragmento Scan")
        fragmentTransaction.commit()
    }

    private fun verFragmentoFull() {
        binding.tvTitulo.text = "Full"

        val fragment = FragmentFull()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentoFL.id, fragment, "Fragmento Full")
        fragmentTransaction.commit()

    }

    private fun verFragmentoSettings() {
        binding.tvTitulo.text = "Settings"

        val fragment = FragmentSettings()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentoFL.id, fragment, "Fragmento Settings")
        fragmentTransaction.commit()
    }

    fun toastMessage(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

//    Escucha el keyCode para ejecutar una accion
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        // Obtener el fragmento visible
        val fragment = supportFragmentManager
            .findFragmentById(R.id.fragmentoFL)

        if (fragment is KeyEventListener) {
            if (fragment.onKeyDown(keyCode, event)) {
                return true // el fragmento lo manejó
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    // Se ejecuta cada cuando inicia la app y tambien cada que regresas a la app
    // despues de que se bloqueo la pantalla o despues de una llamada o despues de estar en otra app
    override fun onResume() {
        val sharedPref = getSharedPreferences("ConfigurationApp", Context.MODE_PRIVATE)
        val power = sharedPref.getInt("power_rfid", 1);
        val frequency = sharedPref.getInt("frequency_rfid", 8);

        super.onResume()
        val myApp = application as MyApplication
        lifecycleScope.launch {
            toastMessage("Iniciando lector...")
            val start = myApp.resetReader()
            if (start) {
                toastMessage(
                    """
                    Lector iniciado con éxito 
                    Power: $power 
                    Frequency: $frequency
                    """.trimIndent())
            } else {
                toastMessage("No se pudo iniciar el lector")
            }
        }
    }

    override fun onDestroy() {
        val myApp = application as MyApplication
        myApp.mReader?.free()
        super.onDestroy()
    }
}