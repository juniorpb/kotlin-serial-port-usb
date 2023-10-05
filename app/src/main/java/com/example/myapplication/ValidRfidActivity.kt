package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView

class ValidRfidActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_valid_rfid)

        val rfid = intent.getStringExtra("RFID").toString()
        val handler = Handler(Looper.getMainLooper())
        val textView = findViewById<TextView>(R.id.textView4)
        textView.text = "RFID: ${rfid}"

        handler.postDelayed({
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }, 1000)
    }
}