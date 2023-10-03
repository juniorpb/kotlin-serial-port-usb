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
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import androidx.appcompat.widget.Toolbar
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

class EntradaAnimalActivity : AppCompatActivity() {

    private lateinit var selectRaceAnimalSpinner: Spinner
    private lateinit var selectSexAnimalSpinner: Spinner
    private lateinit var selectTypeAnimalSpinner: Spinner
    private lateinit var tattooEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var cacheManager: CacheManager


    private var selectedRace: String = ""
    private var selectedSex: String = ""
    private var selectedType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entrada_animal)

        cacheManager = CacheManager(this)
        selectRaceAnimalSpinner = findViewById(R.id.select_race_animal)
        selectSexAnimalSpinner = findViewById(R.id.select_sex_animal)
        selectTypeAnimalSpinner = findViewById(R.id.select_type_animal)
        tattooEditText = findViewById(R.id.textTatuagem)
        sendButton = findViewById(R.id.btnLeituraRFID)
        val rfid = intent.getStringExtra("RFID").toString()

        val textView = findViewById<TextView>(R.id.rfidText)
        textView.text = "RFID: ${rfid}"

        setupSpinners()

        sendButton.setOnClickListener {
            val sex = selectedSex
            val race = selectedRace
            val type = selectedType
            val tattoo = tattooEditText.text.toString()
            val currentDateTime = getCurrentDate()

            if (selectedSex == "Sexo do Animal" || selectedRace == "Raça do Animal" || selectedType == "Tipo do Animal") {
                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    Toast.makeText(this@EntradaAnimalActivity, "Erro ao cadastrar o animal", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (cacheManager.isNetworkConnected()) {
                    sendDataToApi(sex, race, tattoo, type, rfid, currentDateTime)
                } else {
                    cacheDataLocally(sex, race, tattoo, type, rfid, currentDateTime)
                    val handler = Handler(Looper.getMainLooper())
                    handler.post {
                        val intent = Intent(this@EntradaAnimalActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                        Toast.makeText(this@EntradaAnimalActivity, "Dados armazenados em cache.", Toast.LENGTH_SHORT).show()

                    }
                }
            }
        }
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
            }
        }

        selectTypeAnimalSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: android.view.View?, position: Int, id: Long) {
                selectedType = selectTypeAnimalList[position]
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }
    }

    private fun sendDataToApi(sex: String?, race: String, tattoo: String, type: String, rfid: String, currentDateTime:String) {
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
                jsonParam.put("birth", currentDateTime)

                val outputStreamWriter = OutputStreamWriter(connection.outputStream)
                outputStreamWriter.write(jsonParam.toString())
                outputStreamWriter.flush()
                outputStreamWriter.close()

                val responseCode = connection.responseCode
                if (responseCode == 201) {

                    val handler = Handler(Looper.getMainLooper())
                    handler.post {
                        Toast.makeText(this@EntradaAnimalActivity, "Animal cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                    }
                    handler.postDelayed({
                        val intent = Intent(this@EntradaAnimalActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, 100)
                } else {

                    val handler = Handler(Looper.getMainLooper())
                    handler.post {
                        Toast.makeText(this@EntradaAnimalActivity, "Erro ao cadastrar o animal", Toast.LENGTH_SHORT).show()
                    }
                }
                connection.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }
    private fun cacheDataLocally(
        sex: String?,
        race: String,
        tattoo: String,
        type: String,
        rfid: String,
        currentDateTime: String
    ) {
        try {
            val jsonParam = JSONObject()
            jsonParam.put("animalType", type)
            jsonParam.put("race", race)
            jsonParam.put("sex", sex)
            jsonParam.put("tattoo", tattoo)
            jsonParam.put("picketId", 30)
            jsonParam.put("rfid", rfid)
            jsonParam.put("birth", currentDateTime)

            cacheManager.cacheDataLocally(jsonParam.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

