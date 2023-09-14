package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.databinding.ActivityEntradaAnimalBinding
import com.example.myapplication.databinding.ActivityReaderRfidBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReaderRFIDActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReaderRfidBinding

    var CREATE_ANIMAL_RESPONSE = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReaderRfidBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val tatuagemAnimal = intent.getStringExtra("tatuagemAnimal")

        print(tatuagemAnimal)


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