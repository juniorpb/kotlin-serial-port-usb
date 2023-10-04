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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var appDb: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appDb = AppDatabase.getDatabase(this)

        binding.btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            logout()
        }

        binding.btnEntradaAnimal.setOnClickListener {
            val intent = Intent(this, ReaderRFIDActivity::class.java)
            intent.putExtra("tela", "enterAnimal")
            startActivity(intent)
        }

        // Ler dados em cache
        val username = sharedPreferences.getString("username", "")
        val selectedFarmName = sharedPreferences.getString("selectedFarmName", "")

        val textView = findViewById<TextView>(R.id.decription)
        val titlelView = findViewById<TextView>(R.id.title)

        titlelView.text = "Bem-vindo a $selectedFarmName"

        titlelView.text = "Bem vindo a ${selectedFarmName}"
        textView.text = "Olá, $username! aqui você faz o manejo dos seus animais "

        GlobalScope.launch {
            val animalsToSync = appDb.animalDao().getAllAnimals()
            if (animalsToSync.isNotEmpty()) {
                binding.btnSincronizar.backgroundTintList = getColorStateList(R.color.red)
            } else {
                binding.btnSincronizar.backgroundTintList = getColorStateList(R.color.blue)
            }
        }

        val CREATE_ANIMAL_RESPONSE = intent.getStringExtra("CREATE_ANIMAL_RESPONSE")

        if (CREATE_ANIMAL_RESPONSE == "SUCCESS") {
            showToastSuccess()
        } else if (CREATE_ANIMAL_RESPONSE == "ERROR") {
            showToastError()
        }


    }
    private fun logout() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}
