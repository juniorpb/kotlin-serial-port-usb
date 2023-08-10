package com.example.myapplication

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding
import com.felhr.usbserial.UsbSerialDevice
import com.felhr.usbserial.UsbSerialInterface
import com.felhr.usbserial.UsbSerialInterface.UsbReadCallback


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var usbManager: UsbManager
    var usbDevice: UsbDevice? = null
    var usbSerialDevice: UsbSerialDevice? = null
    var usbDeviceConnection: UsbDeviceConnection? = null

    val ACTION_USB_PERMISSION = "permission"

    private lateinit var myTextView: TextView

    private lateinit var statusConnectionTextView: TextView


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.tela2Btn.setOnClickListener {
            val navegarSegundaTEla = Intent(this, Tela2::class.java)
            startActivity(navegarSegundaTEla)
        }

        // view elements
        val on = findViewById<Button>(R.id.on)
        val off = findViewById<Button>(R.id.off)
        val disconnect = findViewById<Button>(R.id.disconnect)
        val connect = findViewById<Button>(R.id.connect)

        myTextView = findViewById(R.id.textView)

        statusConnectionTextView = findViewById(R.id.statusConnectedTextViewId)

        startUsbService()

        on.setOnClickListener { sendData("a") } //97
        off.setOnClickListener { sendData("b") }
        disconnect.setOnClickListener { disconnect() }
        connect.setOnClickListener { startUsbConnecting() }
    }

    private fun startUsbService() {
        usbManager = getSystemService(Context.USB_SERVICE) as UsbManager

        val filter = IntentFilter()
        filter.addAction(ACTION_USB_PERMISSION)
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        registerReceiver(broadcastReceiver, filter)
    }


    private fun startUsbConnecting() {
        Log.i("serial", "starting connections")

        val usbDevices: HashMap<String, UsbDevice>? = usbManager.deviceList

        if (!usbDevices?.isEmpty()!!) {
            usbDevices.forEach { entry ->

                try {
                    usbDevice = entry.value
                    val deviceVendorId: Int? = usbDevice?.vendorId
                    Log.i("serial", "vendorId: " + deviceVendorId)

                    val intent: PendingIntent = PendingIntent.getBroadcast(
                        this,
                        0,
                        Intent(ACTION_USB_PERMISSION),
                        PendingIntent.FLAG_MUTABLE
                    )

                    usbManager.requestPermission(usbDevice, intent)

                    val al2 = AlertDialog.Builder(this)
                    al2.setTitle("id")
                    al2.setMessage("usbDevice ${usbDevice!!.manufacturerName}")
                    al2.show()


                } catch (e: Exception) {
                    val alertError = AlertDialog.Builder(this)
                    alertError.setTitle("error")
                    alertError.setMessage("message: ${e.message}")
                    alertError.show()
                }

            }
        } else {
            Log.i("serial", "no usb device connected")

            val alertError = AlertDialog.Builder(this)
            alertError.setTitle("info")
            alertError.setMessage("no usb device connected")
            alertError.show()
        }
    }

    fun sendData(input: String) {
        usbSerialDevice?.write(input.toByteArray())
        Log.i("serial", "sending data: " + input.toByteArray())
    }

    private fun disconnect() {
        usbSerialDevice?.close()
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action!! == ACTION_USB_PERMISSION) {

                val granted: Boolean = intent.extras!!.getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED)

                if (granted) {

                    usbDeviceConnection = usbManager.openDevice(usbDevice)
                    usbSerialDevice = UsbSerialDevice.createUsbSerialDevice(usbDevice, usbDeviceConnection)
                    if (usbSerialDevice != null) {
                        if (usbSerialDevice!!.open()) {
                            usbSerialDevice!!.setBaudRate(9600)
                            usbSerialDevice!!.setDataBits(UsbSerialInterface.DATA_BITS_8)
                            usbSerialDevice!!.setStopBits(UsbSerialInterface.STOP_BITS_1)
                            usbSerialDevice!!.setParity(UsbSerialInterface.PARITY_NONE)
                            usbSerialDevice!!.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF)
                            usbSerialDevice!!.read(usbSerialListener)

                            statusConnectionTextView.text = "Connected Success!"

                        } else {
                            Log.i("Serial", "port not open")
                        }
                    } else {
                        Log.i("Serial", "port is null")
                    }
                } else {
                    Log.i("Serial", "permission not granted")
                }
            } else if (intent.action == UsbManager.ACTION_USB_DEVICE_ATTACHED) {
                // abre coenxao assim que insere o cabo USB
                //startUsbConnecting()
            } else if (intent.action == UsbManager.ACTION_USB_DEVICE_DETACHED) {
                statusConnectionTextView.text = "No Connected"
                disconnect()

            }
        }
    }


    private val usbSerialListener = object : UsbReadCallback {
        override fun onReceivedData(data: ByteArray) {
            try {

                val receivedString = String(data)
                Log.i("======RECEIVE", receivedString)

                if (receivedString.isNotEmpty()){

                    myTextView.text = receivedString
                }

            } catch (e: Exception) {
                myTextView.text = e.message
            }
        }
    }



}