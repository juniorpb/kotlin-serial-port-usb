package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.example.myapplication.databinding.ActivityEntradaAnimalBinding
import com.example.myapplication.databinding.ActivityHomeBinding

class EntradaAnimalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEntradaAnimalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEntradaAnimalBinding.inflate(layoutInflater)

        setContentView(binding.root)


        val selectRaceAnimalList = arrayOf("Nelore", "Angus", "Brahman", "Brangus", "Senepol")
        val selectRaceAnimalAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, selectRaceAnimalList)
        binding.selectRaceAnimal.setAdapter(selectRaceAnimalAdapter)


        val selectSexAnimalList = arrayOf("Macho", "Fêmea")
        val selectSexAnimalAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, selectSexAnimalList)
        binding.selectSexAnimal.setAdapter(selectSexAnimalAdapter)


        binding.btnLeituraRFID.setOnClickListener {
            val tatuagemAnimal = binding.textTatuagem.text.toString()

            print(tatuagemAnimal)
            val intent = Intent(this, ReaderRFIDActivity::class.java)
            intent.putExtra("tatuagemAnimal", tatuagemAnimal)
            startActivity(intent)
        }
    }
}