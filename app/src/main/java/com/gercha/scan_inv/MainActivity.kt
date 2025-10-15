package com.gercha.scan_inv

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        firebaseAuth = FirebaseAuth.getInstance()
//
//        if (firebaseAuth.currentUser == null) {
//            irOpcionesLogin()
//        }
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//        Fragmento por defecto
        verFragmentoScan()

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

//    Codigo para reconocer teclas del dispositivo Android
//    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
//        if (event.action == KeyEvent.ACTION_DOWN || event.action == KeyEvent.ACTION_UP) {
//            Log.d("ScannerBtn", "action=${event.action} keyCode=${event.keyCode} scanCode=${event.scanCode} deviceId=${event.deviceId}")
//        }
//        return super.dispatchKeyEvent(event)
//    }

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

    override fun onResume() {
        super.onResume()
        val myApp = application as MyApplication
        lifecycleScope.launch {
            toastMessage("Iniciando lector...")
            val start = myApp.resetReader()
            if (start) {
                toastMessage("Lector reiniciado con éxito")
            } else {
                toastMessage("No se pudo reiniciar el lector")
            }
        }
    }

    override fun onDestroy() {
        val myApp = application as MyApplication
        myApp.mReader?.free()
        super.onDestroy()
    }
}