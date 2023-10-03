package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import com.example.myapplication.databinding.ActivityReaderRfidBinding
import com.example.myapplication.dto.AnimalEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.UUID

class ReaderRFIDActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReaderRfidBinding

    var CREATE_ANIMAL_RESPONSE = ""
    lateinit var ANIMAL_CREATE: AnimalEntity

    private lateinit var appDb: AppDatabase

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

        GlobalScope.launch {
            appDb.animalDao().insertAnimal(ANIMAL_CREATE)
        }

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
            CREATE_ANIMAL_RESPONSE = "SUCCESS"

            launch(Dispatchers.Main) {
                navgation()
            }
        }
    }

}