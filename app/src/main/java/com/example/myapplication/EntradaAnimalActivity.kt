package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.databinding.ActivityEntradaAnimalBinding
import com.example.myapplication.databinding.ActivityHomeBinding

class EntradaAnimalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEntradaAnimalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEntradaAnimalBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.btnLeituraRFID.setOnClickListener {
            val tatuagemAnimal = binding.textTatuagem.text.toString()

            print(tatuagemAnimal)
            val intent = Intent(this, ReaderRFIDActivity::class.java)
            intent.putExtra("tatuagemAnimal", tatuagemAnimal)
            startActivity(intent)
        }
    }
}