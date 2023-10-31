package com.example.myapplication

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.myapplication.component.dialog.CustomBottomSheetDialogFragment
import com.example.myapplication.databinding.ActivityHomeBinding
import com.example.myapplication.databinding.ActivityReaderValidRfidactivity2Binding
import com.example.myapplication.databinding.ActivityValidRfidBinding
import com.example.myapplication.databinding.LerRfidLayoutBinding
import com.example.myapplication.dto.DialogFragmentFields
import com.felhr.usbserial.UsbSerialDevice
import com.felhr.usbserial.UsbSerialInterface
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.UUID

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var appDb: AppDatabase
    private lateinit var dialogFragmentFields: DialogFragmentFields

    private val handler = Handler(Looper.getMainLooper())

    // USB
    lateinit var usbManager: UsbManager
    var usbDevice: UsbDevice? = null
    var usbSerialDevice: UsbSerialDevice? = null
    var usbDeviceConnection: UsbDeviceConnection? = null
    val ACTION_USB_PERMISSION = "permission"
    private lateinit var statusConnectionTextView: TextView
    private var usbDevices: HashMap<String, UsbDevice>? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        appDb = AppDatabase.getDatabase(this)



        dialogFragmentFields = DialogFragmentFields(rfid = "")

        binding.btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            logout()
        }

        binding.btnEntradaAnimal.setOnClickListener {

            for (i in 1..5) {
                antennaConfiguration(i)
                //sendData("a")
                Thread.sleep(5) // Atraso de 1 segundo
            }

            val readRfidDialog =
                CustomBottomSheetDialogFragment(R.layout.ler_rfid_layout, dialogFragmentFields, "")
            readRfidDialog.show(supportFragmentManager, readRfidDialog.tag)

            handler.postDelayed({
//                readRfidDialog.dismiss()
//                val intent = Intent(this, EntradaAnimalActivity::class.java)
//                intent.putExtra("rfid", dialogFragmentFields.rfid)
//
//                startActivity(intent)
            }, 3000)


        }

        binding.btnSaidaAnimal.setOnClickListener {
            val intent = Intent(this, ReaderValidRFIDActivity2::class.java)
            intent.putExtra("RemoveAnimal", "PageRemove")
            startActivity(intent)
        }

        statusConnectionTextView = binding.textUsbStatus

        binding.btnValidRFID.setOnClickListener {
            for (i in 1..5) {
                antennaConfiguration(i)
                //sendData("a")
                Thread.sleep(5) // Atraso de 1 segundo
            }

            val readRfidDialog =
                CustomBottomSheetDialogFragment(R.layout.ler_rfid_layout, dialogFragmentFields, "")
            readRfidDialog.show(supportFragmentManager, readRfidDialog.tag)

            val handler = Handler(Looper.getMainLooper())

            //dialogFragmentFields.rfid = "RFID: ${UUID.randomUUID()}"

//            val validRfidDialog =
//                CustomBottomSheetDialogFragment(R.layout.activity_valid_rfid, dialogFragmentFields)


            handler.postDelayed({
                readRfidDialog.dismiss()

//                validRfidDialog.show(supportFragmentManager, validRfidDialog.tag)
//
//                handler.postDelayed({
//                    validRfidDialog.dismiss()
//                }, 2000)


            }, 3000)


        }


        startUsbService()
        startUsbConnecting()
        startJob()




        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)

        val username = sharedPreferences.getString("username", "")
        val selectedFarmName = sharedPreferences.getString("selectedFarmName", "")

        val textView = findViewById<TextView>(R.id.decription)
        val titlelView = findViewById<TextView>(R.id.title)


        titlelView.text = "Bem vindo a ${selectedFarmName}"
        textView.text = "Olá, $username! aqui você faz o manejo dos seus animais "


        GlobalScope.launch {
            val animalsToSync = appDb.animalDao().getAllAnimals()
            if (animalsToSync.isNotEmpty()) {
                binding.btnSincronizar.backgroundTintList = getColorStateList(R.color.red)
            } else {
                binding.btnSincronizar.backgroundTintList = getColorStateList(R.color.disabled)
            }
        }

        val CREATE_ANIMAL_RESPONSE = intent.getStringExtra("CREATE_ANIMAL_RESPONSE")

        if (CREATE_ANIMAL_RESPONSE == "SUCCESS") {
            showToastSuccess()
        } else if (CREATE_ANIMAL_RESPONSE == "ERROR") {
            showToastError()
        }
    }

    private fun showToastSuccess() {
        val mensagem = "Sucesso ao criar animal!"
        val snackbar = Snackbar.make(binding.root, mensagem, Snackbar.LENGTH_LONG)
        snackbar.setBackgroundTint(resources.getColor(android.R.color.holo_green_dark))
        snackbar.show()
    }

    private fun showToastError() {
        val mensagem = "Erro ao criar animal"
        val snackbar = Snackbar.make(binding.root, mensagem, Snackbar.LENGTH_LONG)
        snackbar.setBackgroundTint(resources.getColor(android.R.color.holo_red_dark))
        snackbar.show()
    }

    private fun logout() {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }


    // USB =======
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

        usbDevices = usbManager.deviceList

        if (!usbDevices?.isEmpty()!!) {
            usbDevices!!.forEach { entry ->

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

                val granted: Boolean =
                    intent.extras!!.getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED)

                if (granted) {

                    usbDeviceConnection = usbManager.openDevice(usbDevice)
                    usbSerialDevice =
                        UsbSerialDevice.createUsbSerialDevice(usbDevice, usbDeviceConnection)
                    if (usbSerialDevice != null) {
                        if (usbSerialDevice!!.open()) {
                            //usbSerialDevice!!.setBaudRate(9600)
                            usbSerialDevice!!.setBaudRate(57600)
                            usbSerialDevice!!.setDataBits(UsbSerialInterface.DATA_BITS_8)
                            usbSerialDevice!!.setStopBits(UsbSerialInterface.STOP_BITS_1)
                            usbSerialDevice!!.setParity(UsbSerialInterface.PARITY_NONE)
                            usbSerialDevice!!.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF)
                            usbSerialDevice!!.read(usbSerialListener)

                            statusConnectionTextView.text = "USB Conectado"

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
                startUsbConnecting()
            } else if (intent.action == UsbManager.ACTION_USB_DEVICE_DETACHED) {
                statusConnectionTextView.text = "USB Desconectado"
                disconnect()

            }
        }
    }


    private val usbSerialListener = object : UsbSerialInterface.UsbReadCallback {
        fun ByteArray.toHexString() = joinToString(",") { "%02X".format(it) }

        override fun onReceivedData(data: ByteArray) {
            try {

                val receivedString = data.toHexString()

                val RFIDArray = receivedString.split(",").toTypedArray()


                Log.i("======RECEIVE", receivedString)

                if (receivedString.isNotEmpty()) {

                    var rfidText = ""
                    for ((i, value) in data.withIndex()) {
                        println("=== tagBuffer[$i] = ${value}")

                        if (value.toInt() == 21) {
                            println("=== RFID =================")

                            for (i in 7..18) {
                                print(RFIDArray[i])
                                rfidText += RFIDArray[i]
                            }
                            dialogFragmentFields.rfid = rfidText


                            println(dialogFragmentFields)
                            val validRfidDialog =
                                CustomBottomSheetDialogFragment(R.layout.activity_valid_rfid, dialogFragmentFields, rfidText)

                            validRfidDialog.show(supportFragmentManager, validRfidDialog.tag)

                            handler.postDelayed({
                                validRfidDialog.dismiss()
                            }, 5000)

//
//                            val intent = Intent(this@HomeActivity, EntradaAnimalActivity::class.java)
//                            intent.putExtra("rfid", rfidText)
//
//                            startActivity(intent)

                        } else {
                            dialogFragmentFields.rfid = RFIDArray.toString()
                        }
                    }
                }

            } catch (e: Exception) {
                dialogFragmentFields.rfid = e.message
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
        if (power > 30) {
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

    private val jobRunnable = object : Runnable {
        override fun run() {

            val usbDevices: HashMap<String, UsbDevice>? = usbManager.deviceList

            if (!usbDevices?.isEmpty()!!) {

            } else {
                Log.i("serial", "no usb device connected")
                statusConnectionTextView.text = "USB Desconectado"

            }

            handler.postDelayed(this, 5000)
        }
    }

    fun startJob() {
        // Inicie a tarefa pela primeira vez
        handler.post(jobRunnable)
    }

    // USB FINALY =======
}

