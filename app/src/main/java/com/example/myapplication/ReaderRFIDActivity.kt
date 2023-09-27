package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.databinding.ActivityEntradaAnimalBinding
import com.example.myapplication.databinding.ActivityReaderRfidBinding
import com.example.myapplication.dto.CreateAnimalDTO
import com.example.myapplication.client.IntelicampoClient
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpGet
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPatch
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.HttpClientBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class ReaderRFIDActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReaderRfidBinding

    var CREATE_ANIMAL_RESPONSE = ""

    lateinit var createAnimalDTO: CreateAnimalDTO

    val retrofit = Retrofit.Builder()
        .baseUrl("https://intelicampo-api-stg.vercel.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(IntelicampoClient::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReaderRfidBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val tatuagemAnimal = intent.getStringExtra("tatuagemAnimal")

        print(tatuagemAnimal)
         createAnimalDTO = CreateAnimalDTO(
            tattoo = tatuagemAnimal
        )

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