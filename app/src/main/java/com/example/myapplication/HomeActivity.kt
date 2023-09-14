package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.myapplication.databinding.ActivityHomeBinding
import com.google.android.material.snackbar.Snackbar

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.btnEntradaAnimal.setOnClickListener {
            val intent = Intent(this, EntradaAnimalActivity::class.java)
            startActivity(intent)
        }

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
}