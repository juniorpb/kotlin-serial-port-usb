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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)


        setContentView(binding.root)

        binding.btnAnimal.setOnClickListener {
            val intent = Intent(this, ReaderRFIDActivity::class.java)
            intent.putExtra("tela2", "RfidValid")
            startActivity(intent)
        }

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

        binding.btnSaidaAnimal.setOnClickListener {
            val intent = Intent(this, ReaderRFIDActivity::class.java)
            intent.putExtra("tela3", "remove")
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
    private fun logout() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}
