package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.databinding.ActivityReaderRfidBinding
import com.example.myapplication.dto.AnimalEntity
import kotlinx.coroutines.CoroutineScope
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
import java.util.UUID

class ReaderRFIDActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReaderRfidBinding

    var CREATE_ANIMAL_RESPONSE = ""
    lateinit var ANIMAL_CREATE: AnimalEntity

    private lateinit var appDb: AppDatabase
    private var animalId: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReaderRfidBinding.inflate(layoutInflater)

        setContentView(binding.root)
        appDb = AppDatabase.getDatabase(this)

        val tatuagemAnimal = intent.getStringExtra("tatuagemAnimal")
        val selectRaceAnimal = intent.getStringExtra("selectRaceAnimal")
        val selectSexAnimal = intent.getStringExtra("selectSexAnimal")

        print(tatuagemAnimal)

        ANIMAL_CREATE = AnimalEntity(
            rfid = UUID.randomUUID().toString(),
            tattoo = tatuagemAnimal,
            race = selectRaceAnimal,
            sex = selectSexAnimal
        )


        val sex = intent.getStringExtra("sex")
        val tattoo = intent.getStringExtra("tattoo")
        val race = intent.getStringExtra("race")
        val type = intent.getStringExtra("type")
        val currentDateTime = intent.getStringExtra("currentDateTime")

        ANIMAL_CREATE = AnimalEntity(
            rfid = UUID.randomUUID().toString(), // LER RFID DA ANTENA
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


    fun redirectAsync(navgation: () -> Unit) {
        val mainScope = CoroutineScope(Dispatchers.Main)
        mainScope.launch(Dispatchers.IO) {
            Thread.sleep(2000)

            launch(Dispatchers.Main) {
                navgation()
            }
        }
    }

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
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        dateFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val currentDateTime = Date(System.currentTimeMillis())
        return dateFormat.format(currentDateTime)
    }
}