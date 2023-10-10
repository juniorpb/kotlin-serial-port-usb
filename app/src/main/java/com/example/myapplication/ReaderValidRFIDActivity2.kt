package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import java.util.UUID

class ReaderValidRFIDActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader_valid_rfidactivity2)
        var rfid = UUID.randomUUID().toString()

        val handler = Handler(Looper.getMainLooper())


        val removeAnimal = intent.getStringExtra("RemoveAnimal").toString()
        val ValidRFID = intent.getStringExtra("ValidRFID").toString()

        if(removeAnimal == "PageRemove"){
            handler.postDelayed({
                val intent = Intent(this, RemoveAnimalActivity::class.java)
                intent.putExtra("RFID", rfid)
                startActivity(intent)
                finish()
            }, 3000)
        }else {
            handler.postDelayed({
                val intent = Intent(this, ValidRfidActivity::class.java)
                intent.putExtra("RFID", rfid)
                startActivity(intent)
                finish()
            }, 3000)
        }

    }
    }
