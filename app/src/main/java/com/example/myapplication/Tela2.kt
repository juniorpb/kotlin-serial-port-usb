package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.felhr.usbserial.UsbSerialDevice

class Tela2(usbSerialDevice: UsbSerialDevice) : AppCompatActivity() {
    val usbSerialDevice2 = usbSerialDevice
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela2)

        // view elements
        val on = findViewById<Button>(R.id.on_tela_2)
        val off = findViewById<Button>(R.id.off_tela_2)

        var mainActivity = MainActivity()
        on.setOnClickListener { usbSerialDevice2?.write("a".toByteArray()) } //97
        off.setOnClickListener { usbSerialDevice2?.write("b".toByteArray()) }
    }
}