package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.databinding.ActivityHomeBinding
import com.google.android.material.snackbar.Snackbar

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)



        setContentView(binding.root)

        binding.btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            logout()

        }

        binding.btnEntradaAnimal.setOnClickListener {
            val intent = Intent(this, EntradaAnimalActivity::class.java)
            startActivity(intent)
        }
        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)

// Ler dados em cache
        val username = sharedPreferences.getString("username", "")
        val selectedFarmName = sharedPreferences.getString("selectedFarmName", "")

        val textView = findViewById<TextView>(R.id.decription)
        val titlelView = findViewById<TextView>(R.id.title)


        titlelView.text = "Bem vindo a ${selectedFarmName}"
        textView.text = "Olá, $username! aqui você faz o manejo dos seus animais "

        val CREATE_ANIMAL_RESPONSE = intent.getStringExtra("CREATE_ANIMAL_RESPONSE")

        if (CREATE_ANIMAL_RESPONSE == "SUCCESS") {
            showToastSuccess()
        } else if(CREATE_ANIMAL_RESPONSE == "ERROR") {
            showToastError()
        }


    }


    private fun showToastSuccess() {
        val mensagem = "Sucesso ao criar animal!"

        val snackbar = Snackbar.make(binding.root, mensagem, Snackbar.LENGTH_LONG)
        snackbar.setBackgroundTint(resources.getColor(android.R.color.holo_green_dark))
        snackbar.show()
    }

    private fun showToastError() {
        val mensagem = "Erro ao criar animal"

        val snackbar = Snackbar.make(binding.root, mensagem, Snackbar.LENGTH_LONG)
        snackbar.setBackgroundTint(resources.getColor(android.R.color.holo_red_dark))
        snackbar.show()
    }

    private fun logout(){
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

    }
}