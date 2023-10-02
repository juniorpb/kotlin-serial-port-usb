package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
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
import java.util.UUID

class ReaderRFIDActivity : AppCompatActivity() {

    private lateinit var rfidEditText: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader_rfid)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        rfidEditText = UUID.randomUUID().toString()
        val rfid = rfidEditText
        val handler = Handler(Looper.getMainLooper())
        progressBar.visibility = View.VISIBLE

        val entradaAnimal = intent.getStringExtra("tela").toString()
        val removeAnimal = intent.getStringExtra("tela2").toString()

        if(entradaAnimal == "enterAnimal"){
            handler.postDelayed({
                val intent = Intent(this, EntradaAnimalActivity::class.java)
                intent.putExtra("RFID", rfid )
                startActivity(intent)
                finish()
            }, 3000)
        }else
            handler.postDelayed({
                val intent = Intent(this, RemoveAnimalActivity::class.java)
                intent.putExtra("RFID", rfid )
                startActivity(intent)
                finish()
            }, 3000)






    }

    }

