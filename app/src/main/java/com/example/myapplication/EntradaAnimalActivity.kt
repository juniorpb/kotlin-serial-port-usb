package com.example.myapplication

import android.content.Intent
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.ArrayAdapter
import java.util.UUID
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EntradaAnimalActivity : AppCompatActivity() {

    private lateinit var selectRaceAnimalSpinner: Spinner
    private lateinit var selectSexAnimalSpinner: Spinner
    private lateinit var selectTypeAnimalSpinner: Spinner
    private lateinit var tattooEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var rfidEditText: String

    private var selectedRace: String = ""
    private var selectedSex: String = ""
    private var selectedType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entrada_animal)

        selectRaceAnimalSpinner = findViewById(R.id.select_race_animal)
        selectSexAnimalSpinner = findViewById(R.id.select_sex_animal)
        selectTypeAnimalSpinner = findViewById(R.id.select_type_animal)
        tattooEditText = findViewById(R.id.textTatuagem)
        sendButton = findViewById(R.id.btnLeituraRFID)
        rfidEditText = UUID.randomUUID().toString()

        setupSpinners()

        sendButton.setOnClickListener {
            val sex = selectedSex
            val race = selectedRace
            val type = selectedType
            val tattoo = tattooEditText.text.toString()
            val currentDateTime = getCurrentDate()

            if(selectedSex == "Sexo do Animal" || selectedRace == "Raça do Animal" || selectedType == "Tipo do Animal"){

                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    Toast.makeText(this@EntradaAnimalActivity, "Erro ao cadastrar o animal", Toast.LENGTH_SHORT).show()
                }
            }else{
                goToReanderRfidAndCreateAnimal(sex, race, tattoo, type, currentDateTime)
            }
        }
    }

    private fun goToReanderRfidAndCreateAnimal(
        sex: String?,
        race: String,
        tattoo: String,
        type: String,
        currentDateTime: String
    ) {

        val intent = Intent(this, ReaderRFIDActivity::class.java)
        intent.putExtra("sex", sex)
        intent.putExtra("race", race)
        intent.putExtra("tattoo", tattoo)
        intent.putExtra("type", type)
        intent.putExtra("currentDateTime", currentDateTime)

        startActivity(intent)
    }

    private fun setupSpinners() {
        val selectRaceAnimalList = arrayOf("Raça do Animal","Nelore", "Angus", "Brahman", "Brangus", "Senepol")
        val selectSexAnimalList = arrayOf("Sexo do Animal","Macho", "Femea")
        val selectTypeAnimalList = arrayOf("Tipo do Animal","BEZERRO", "GARROTE", "NOVILHA", "ADULTO")

        val selectRaceAnimalAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, selectRaceAnimalList)
        val selectSexAnimalAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, selectSexAnimalList)
        val selectTypeAnimalAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, selectTypeAnimalList)

        selectRaceAnimalSpinner.adapter = selectRaceAnimalAdapter
        selectSexAnimalSpinner.adapter = selectSexAnimalAdapter
        selectTypeAnimalSpinner.adapter = selectTypeAnimalAdapter

        selectRaceAnimalSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: android.view.View?, position: Int, id: Long) {
                selectedRace = selectRaceAnimalList[position]

            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Não faz nada
            }
        }

        selectSexAnimalSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: android.view.View?, position: Int, id: Long) {
                selectedSex = selectSexAnimalList[position]

                if(selectSexAnimalList[position]==="Macho"){
                    selectedSex = "MALE"
                }
                if(selectSexAnimalList[position]==="Femea"){
                    selectedSex = "FEMALE"
                }

            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Não faz nada
            }
        }

        selectTypeAnimalSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: android.view.View?, position: Int, id: Long) {
                selectedType = selectTypeAnimalList[position]

            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Não faz nada
            }
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }
}
