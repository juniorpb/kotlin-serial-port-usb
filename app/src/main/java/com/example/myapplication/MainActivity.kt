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
//
//    lateinit var usbManager: UsbManager
//    var usbDevice: UsbDevice? = null
//    var usbSerialDevice: UsbSerialDevice? = null
//    var usbDeviceConnection: UsbDeviceConnection? = null
//
//    val ACTION_USB_PERMISSION = "permission"


    private lateinit var myTextView: TextView

    private lateinit var statusConnectionTextView: TextView


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.btnGoTologin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnAntennaConfig.setOnClickListener {
            Log.i("TEST","====== ANTENNA CONFIGURATION ======")

            //antennaConfiguration(1)

//
//            for (i in 1..5) {
//                antennaConfiguration(i)
//                Thread.sleep(5) // Atraso de 1 segundo
//            }

        }

        setContentView(binding.root)

        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)

        // view elements
        val on = findViewById<Button>(R.id.on)
        val off = findViewById<Button>(R.id.off)
        val disconnect = findViewById<Button>(R.id.disconnect)
        val connect = findViewById<Button>(R.id.connect)

        myTextView = findViewById(R.id.textView)

        statusConnectionTextView = findViewById(R.id.statusConnectedTextViewId)

//        startUsbService()
//
//        on.setOnClickListener { sendData("a") } //97
//        off.setOnClickListener { sendData("b") }
//        disconnect.setOnClickListener { disconnect() }
//        connect.setOnClickListener { startUsbConnecting() }
    }

    /*
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
                    al2.setMessage("usbDevice ${usbDevice.toString()}")
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

    private fun sendData(input: String) {
        usbSerialDevice?.write(input.toByteArray())
        Log.i("serial", "sending data: " + input.toByteArray())
    }

    private fun sendDataByte(input: UByteArray) {
        usbSerialDevice?.write(input.toByteArray())
        Log.i("serial", "sending BYTE data: " + input.toByteArray())
    }

    private fun sendDataUByte(input: UByte) {
        usbSerialDevice?.write(byteArrayOf(input.toByte()))
        Log.i("serial", "sending BYTE data: " + input.toInt().toByte())
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
                            //usbSerialDevice!!.setBaudRate(57600)
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
        fun ByteArray.toHexString() = joinToString(",") { "%02X".format(it) }

        override fun onReceivedData(data: ByteArray) {
            try {
                Log.i("======RECEIVE", data.toString())

                val receivedString = data.toHexString()

                val RFIDArray = receivedString.split(",").toTypedArray()


                Log.i("======RECEIVE", receivedString)

                if (receivedString.isNotEmpty()){

                    for ((i, value) in data.withIndex()) {
                        println("=== tagBuffer[$i] = ${value}")

                        if(value.toInt() == 21) {

                            println("=== RFID =================")

                            var rfidText = ""
                            for (i in 7..18) {
                                print(RFIDArray[i])
                                rfidText += RFIDArray[i]
                            }

                            myTextView.text = rfidText

                        }
                    }
                }

            } catch (e: Exception) {
                myTextView.text = e.message
            }
        }
    }



    // CONFIGURAR ANTENA =====================

    fun uiCrc16Cal(tagBuffer: UByteArray): Long {
        val polynomial: Long = 0x8408
        var crc: Long = 0xFFFF

        for (b in tagBuffer) {
            crc = crc xor b.toLong()
            for (i in 8 downTo 1) {
                if ((crc and 0x0001) != 0L) {
                    crc = (crc ushr 1) xor polynomial
                } else {
                    crc = crc ushr 1
                }
            }
        }

        return crc
    }


    fun writeCommand2(address: UByte, cmd: UByte) {

        val tagBuffer: UByteArray = ubyteArrayOf(
            4u, // len
            address, // address
            cmd, // cmd
        )

        val crc = uiCrc16Cal(tagBuffer)
        val msb = (crc ushr 8).toUByte()
        val lsb = (crc and 0xFF).toUByte()

        val tagBufferWithCrc = tagBuffer + ubyteArrayOf(lsb, msb)

        println("CRC = $crc")

        for ((i, value) in tagBufferWithCrc.withIndex()) {
            println("tagBuffer[$i] = $value")
            sendDataUByte(value)

        }

        //sendDataByte(tagBufferWithCrc)

    }

    fun writeCommand3(address: UByte, cmd: UByte, data: UByteArray) {

        val dimData = data.size.toUByte()

        val commandInit: UByteArray = ubyteArrayOf(
            (4u + dimData).toUByte(), // len
            address, // address
            cmd, // cmd
        )

        val commandInitWithData = commandInit + data

        val crc = uiCrc16Cal(commandInitWithData)

        val msb = (crc ushr 8).toUByte()
        val lsb = (crc and 0xFF).toUByte()

        val tagBufferWithCrc = commandInitWithData + ubyteArrayOf(lsb, msb)

        for ((i, value) in tagBufferWithCrc.withIndex()) {
            println("tagBuffer[$i] = $value")
            sendDataUByte(value)

        }

        //for ((i, value) in tagBufferWithCrc.withIndex()) {
          //  println("tagBuffer[$i] = $value")
        //}

        //sendDataByte(tagBufferWithCrc)

    }

    fun writeCommand31(address: UByte, cmd: UByte, data: UByte) {

        val tagBuffer: UByteArray = ubyteArrayOf(
            4u, // len
            address, // address
            cmd, // cmd
            data,
        )

        val crc = uiCrc16Cal(tagBuffer)
        val msb = (crc ushr 8).toUByte()
        val lsb = (crc and 0xFF).toUByte()

        val tagBufferWithCrc = tagBuffer + ubyteArrayOf(lsb, msb)

        println("CRC = $crc")

        for ((i, value) in tagBufferWithCrc.withIndex()) {
            println("tagBuffer[$i] = $value")
            sendDataUByte(value)

        }

        //sendDataByte(tagBufferWithCrc)

    }


    fun getSerialNumber() {
        println("starting getSerialNumber()")
        writeCommand2(0u, 76u)
    }

    fun setRegionRFID() {
        println("starting setRegionRFID()")

        val data: UByteArray = ubyteArrayOf(0x31.toUByte(), 0x80.toUByte())
        writeCommand3(0u, 34u, data)
    }

    fun setPower(power: Int) {
        println("starting setPower()")

        var powerData = power
        if(power > 30) {
            powerData = 30
        }

        writeCommand31(0x00.toUByte(), 0x2F.toUByte(), powerData.toUByte())
    }

    fun setScanTime() {
        println("starting setScanTime()")

        writeCommand31(0x00.toUByte(), 0x25.toUByte(), 0x03.toUByte())
    }

    fun getSingleTag() {
        println("starting getSingleTag()")

        writeCommand2(0x00.toUByte(), 0x0F.toUByte())
    }

    fun antennaConfiguration(command: Int) {

        try {
            if (command == 1) {
                getSerialNumber()
            }

            if (command == 2) {
                setRegionRFID()
            }

            if (command == 3) {
                setPower(2)
            }

            if (command == 4) {
                setScanTime()
            }

            if (command == 5) {
                getSingleTag()
            }

        } catch (e: Exception) {
            Log.i("error", "error to config antenna ${e.message}")
        }

    }


*/
}