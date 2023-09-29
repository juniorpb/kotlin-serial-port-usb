package com.example.myapplication

import android.content.Intent
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.myapplication.databinding.ActivityEntradaAnimalBinding
import com.example.myapplication.databinding.ActivityHomeBinding
import java.util.UUID
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

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
            val rfid = rfidEditText.toString()

            sendDataToApi(sex, race, tattoo, type, rfid)
        }
    }

    private fun setupSpinners() {
        val selectRaceAnimalList = arrayOf("Nelore", "Angus", "Brahman", "Brangus", "Senepol")
        val selectSexAnimalList = arrayOf("MALE", "FEMALE")
        val selectTypeAnimalList = arrayOf("BEZERRO", "GARROTE", "NOVILHA", "ADULTO")

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

    private fun sendDataToApi(sex: String, race: String, tattoo: String, type: String, rfid: String) {
        val apiUrl = "https://intelicampo-api-stg.vercel.app/animal"  // Substitua pela URL correta da sua API

        GlobalScope.launch(Dispatchers.IO) {
            val url = URL(apiUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            try {
                val jsonParam = JSONObject()
                jsonParam.put("animalType", type)
                jsonParam.put("race", race)
                jsonParam.put("sex", sex)
                jsonParam.put("tattoo", tattoo)
                jsonParam.put("picketId", 30)
                jsonParam.put("rfid", rfid)

                val outputStreamWriter = OutputStreamWriter(connection.outputStream)
                outputStreamWriter.write(jsonParam.toString())
                outputStreamWriter.flush()
                outputStreamWriter.close()

                val responseCode = connection.responseCode
                if (responseCode == 201) {
                    // A API retornou um código 201 (Created)
                    // Exibe um toast informando que a operação foi bem-sucedida
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@EntradaAnimalActivity, "Animal cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Se a resposta não for 201, algo deu errado na API
                    // Exiba uma mensagem de erro, se desejar
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@EntradaAnimalActivity, "Erro ao cadastrar o animal", Toast.LENGTH_SHORT).show()
                    }
                }

                connection.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
