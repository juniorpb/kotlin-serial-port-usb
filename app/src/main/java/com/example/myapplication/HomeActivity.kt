package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.databinding.ActivityHomeBinding
import com.google.android.material.snackbar.Snackbar
import java.io.File
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var cacheManager: CacheManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)

        cacheManager = CacheManager(this)

        setContentView(binding.root)

        // Verifique se o arquivo de cache existe
        val hasCache = cacheManager.hasCache()

        // Defina as cores do botão com base na existência do cache
        if (hasCache) {
            binding.btnSincronizar.setBackgroundColor(ContextCompat.getColor(this, R.color.blue))
        } else {
            binding.btnSincronizar.setBackgroundColor(ContextCompat.getColor(this, R.color.disabled))
        }

        binding.btnSincronizar.setOnClickListener {
            cacheManager.requestResult.observe(this, Observer { isSuccess ->
                if (isSuccess) {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    showToast("Request bem-sucedida")
                } else {
                    showToast("Falha na request")
                }
            })
            sendRequest()
        }

        binding.btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            logout()
            cacheManager.clearCache()
        }

        binding.btnEntradaAnimal.setOnClickListener {
            val intent = Intent(this, ReaderRFIDActivity::class.java)
            intent.putExtra("tela", "enterAnimal")
            startActivity(intent)
        }

        binding.btnSaidaAnimal.setOnClickListener {
            val intent = Intent(this, ReaderRFIDActivity::class.java)
            intent.putExtra("tela1", "removeAnimal")
            startActivity(intent)
        }



        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "")
        val selectedFarmName = sharedPreferences.getString("selectedFarmName", "")

        val textView = findViewById<TextView>(R.id.decription)
        val titlelView = findViewById<TextView>(R.id.title)

        titlelView.text = "Bem-vindo a $selectedFarmName"

        textView.text = "Olá, $username! Aqui você faz o manejo dos seus animais"

    }
    private fun sendRequest() {
        // Chamando a função suspensa para enviar a request
        GlobalScope.launch(Dispatchers.Main) {
            cacheManager.sendCachedDataToApi()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun logout() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }


}
