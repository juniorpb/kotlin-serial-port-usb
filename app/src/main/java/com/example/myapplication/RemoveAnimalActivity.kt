package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class RemoveAnimalActivity : AppCompatActivity() {

    private lateinit var cancellButton: Button
    private lateinit var removeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remove_animal)

        cancellButton = findViewById(R.id.btnCancelar)
        removeButton = findViewById(R.id.btnRemover)


        val rfid = intent.getStringExtra("RFID").toString()

        val textView = findViewById<TextView>(R.id.rfidText)
        textView.text = "RFID: ${rfid}"

        cancellButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        removeButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            val handler = Handler(Looper.getMainLooper())
            handler.post {
                Toast.makeText(this@RemoveAnimalActivity, "Animal Removido Com Sucesso", Toast.LENGTH_SHORT).show()
            }
        }

    }
}