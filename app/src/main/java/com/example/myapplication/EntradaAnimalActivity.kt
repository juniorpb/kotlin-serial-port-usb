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
import com.example.myapplication.dto.AnimalEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EntradaAnimalActivity : AppCompatActivity() {

    private lateinit var selectRaceAnimalSpinner: Spinner
    private lateinit var selectSexAnimalSpinner: Spinner
    private lateinit var selectTypeAnimalSpinner: Spinner
    private lateinit var tattooEditText: EditText
    private lateinit var sendButton: Button

    private var selectedRace: String = ""
    private var selectedSex: String = ""
    private var selectedType: String = ""

    var CREATE_ANIMAL_RESPONSE = "ERROR"
    lateinit var ANIMAL_CREATE: AnimalEntity

    private lateinit var appDb: AppDatabase
    private var animalId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entrada_animal)

        appDb = AppDatabase.getDatabase(this)


        selectRaceAnimalSpinner = findViewById(R.id.select_race_animal)
        selectSexAnimalSpinner = findViewById(R.id.select_sex_animal)
        selectTypeAnimalSpinner = findViewById(R.id.select_type_animal)
        tattooEditText = findViewById(R.id.textTatuagem)
        sendButton = findViewById(R.id.btnLeituraRFID)

        var rfid = intent.getStringExtra("rfid").toString()

        if (rfid.isEmpty()) {
            rfid = UUID.randomUUID().toString()
        }

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
                ANIMAL_CREATE = AnimalEntity(
                    rfid = rfid,
                    tattoo = tattoo,
                    race = race,
                    sex = sex,
                    type = type,
                    currentDateTime = currentDateTime
                )

                sendDataToApi(ANIMAL_CREATE)

                fun navgation() {
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("CREATE_ANIMAL_RESPONSE", CREATE_ANIMAL_RESPONSE)
                    startActivity(intent)
                }

                redirectAsync(::navgation)
            }
        }
    }

    private fun goToReanderRfidAndCreateAnimal(
        sex: String?,
        race: String,
        tattoo: String,
        type: String,
        currentDateTime: String,
        rfid: String
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


    private fun logDate(id: Int){
        val apiUrl = "https://intelicampo-api-stg.vercel.app/animal-record"

        GlobalScope.launch(Dispatchers.IO) {
            val url = URL(apiUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            try {
                val jsonParam = JSONObject()

                jsonParam.put("status", "Entrada de Animal")
                jsonParam.put("comment", "Animal Entrou na Fazenda")
                jsonParam.put("animalId", id)
                jsonParam.put("date", getCurrentDate())

                val outputStreamWriter = OutputStreamWriter(connection.outputStream)
                outputStreamWriter.write(jsonParam.toString())
                outputStreamWriter.flush()
                outputStreamWriter.close()



                connection.disconnect()
            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }


    @OptIn(DelicateCoroutinesApi::class)
    private fun sendDataToApi(animalEntity: AnimalEntity) {
        val apiUrl = "https://intelicampo-api-stg.vercel.app/animal"

        GlobalScope.launch(Dispatchers.IO) {
            val url = URL(apiUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            try {
                val jsonParam = JSONObject()
                jsonParam.put("animalType", animalEntity.type)
                jsonParam.put("race", animalEntity.race)
                jsonParam.put("sex", animalEntity.sex)
                jsonParam.put("tattoo", animalEntity.tattoo)
                jsonParam.put("picketId", 30) // TODO: mudar para picket padrao 999
                jsonParam.put("rfid", animalEntity.rfid)
                jsonParam.put("birth", animalEntity.currentDateTime)

                val outputStreamWriter = OutputStreamWriter(connection.outputStream)
                outputStreamWriter.write(jsonParam.toString())
                outputStreamWriter.flush()
                outputStreamWriter.close()

                val responseCode = connection.responseCode


                if (responseCode == 201) {
                    val inputStream = connection.inputStream
                    val responseString = inputStream.bufferedReader().use { it.readText() }

                    // Parse the response JSON to get the "id"
                    val responseObject = JSONObject(responseString)
                    animalId = responseObject.getInt("id")  // Armazena o ID do animal criado

                    CREATE_ANIMAL_RESPONSE = "SUCCESS"

                } else {

                    CREATE_ANIMAL_RESPONSE = "ERROR"

                    GlobalScope.launch {
                        appDb.animalDao().insertAnimal(ANIMAL_CREATE)
                    }

                }
                connection.disconnect()
            } catch (e: Exception) {

                GlobalScope.launch {
                    appDb.animalDao().insertAnimal(ANIMAL_CREATE)
                }

                e.printStackTrace()
            }
            logDate(animalId)
        }
    }


    fun redirectAsync(navgation: () -> Unit) {
        val mainScope = CoroutineScope(Dispatchers.Main)
        mainScope.launch(Dispatchers.IO) {
            Thread.sleep(2000)

            launch(Dispatchers.Main) {
                navgation()
            }
        }
    }
}
