package com.example.myapplication

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.felhr.usbserial.UsbSerialDevice
import com.felhr.usbserial.UsbSerialInterface
import java.lang.Exception


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }
}

class MainActivity : AppCompatActivity() {

    lateinit var m_usbManager: UsbManager
    var m_device: UsbDevice? = null
    var m_serial: UsbSerialDevice? = null
    var m_connection: UsbDeviceConnection? = null

    val ACTION_USB_PERMISSION = "permission"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        m_usbManager = getSystemService(Context.USB_SERVICE) as UsbManager

        val btnGoToLogin: Button = findViewById(R.id.btnGoToLogin)
        btnGoToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        val on = findViewById<Button>(R.id.on)
        val off = findViewById<Button>(R.id.off)
        val disconnect = findViewById<Button>(R.id.disconnect)
        val connect = findViewById<Button>(R.id.connect)

        val filter = IntentFilter()
        filter.addAction(ACTION_USB_PERMISSION)
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        registerReceiver(broadcastReceiver, filter)

        on.setOnClickListener { sendData("a") } //97
        off.setOnClickListener { sendData("b") }
        disconnect.setOnClickListener { disconnect() }
        connect.setOnClickListener { startUsbConnecting() }
    }


    private fun startUsbConnecting1() {
        Log.i("serial", "starting connections")

        val usbDevices: HashMap<String, UsbDevice>? = m_usbManager.deviceList
        if (!usbDevices?.isEmpty()!!) {
            var keep = true
            usbDevices.forEach { entry ->
                m_device = entry.value
                val deviceVendorId: Int? = m_device?.vendorId
                Log.i("serial", "vendorId: " + deviceVendorId)
                if (deviceVendorId == 1027) {
                    val intent: PendingIntent = PendingIntent.getBroadcast(
                        this,
                        0,
                        Intent(ACTION_USB_PERMISSION),
                        PendingIntent.FLAG_IMMUTABLE
                    )
                    m_usbManager.requestPermission(m_device, intent)
                    keep = false
                    Log.i("serial", "connection successful")
                } else {
                    m_connection = null
                    m_device = null
                    Log.i("serial", "unable to connect")
                }
                if (!keep) {
                    return
                }
            }
        } else {
            Log.i("serial", "no usb device connected")
        }
    }

    private fun startUsbConnecting() {
        Log.i("serial", "starting connections")

        val usbDevices: HashMap<String, UsbDevice>? = m_usbManager.deviceList

        if (!usbDevices?.isEmpty()!!) {
            var keep = true
            usbDevices.forEach { entry ->

                try {
                    val al = AlertDialog.Builder(this)
                    al.setTitle("abs")
                    al.setMessage("devices ${usbDevices}")
                    al.show()

                    m_device = entry.value
                    val deviceVendorId: Int? = m_device?.vendorId
                    Log.i("serial", "vendorId: " + deviceVendorId)

                    val al2 = AlertDialog.Builder(this)
                    al2.setTitle("id")
                    al2.setMessage("m_device ${m_device}")
                    al2.show()

                    val intent: PendingIntent = PendingIntent.getBroadcast(
                        this,
                        0,
                        Intent(ACTION_USB_PERMISSION),
                        PendingIntent.FLAG_MUTABLE
                    )

                    m_usbManager.requestPermission(m_device, intent)

                    keep = false
                    Log.i("serial", "connection successful")

                    if (!keep) {
                        return
                    }


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
        m_serial?.write(input.toByteArray())
        Log.i("serial", "sending data: " + input.toByteArray())
    }

    private fun disconnect() {
        m_serial?.close()
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action!! == ACTION_USB_PERMISSION) {
                val granted: Boolean =
                    intent.extras!!.getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED)
                if (granted) {
                    m_connection = m_usbManager.openDevice(m_device)
                    m_serial = UsbSerialDevice.createUsbSerialDevice(m_device, m_connection)
                    if (m_serial != null) {
                        if (m_serial!!.open()) {
                            m_serial!!.setBaudRate(9600)
                            m_serial!!.setDataBits(UsbSerialInterface.DATA_BITS_8)
                            m_serial!!.setStopBits(UsbSerialInterface.STOP_BITS_1)
                            m_serial!!.setParity(UsbSerialInterface.PARITY_NONE)
                            m_serial!!.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF)
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
                startUsbConnecting()
            } else if (intent.action == UsbManager.ACTION_USB_DEVICE_DETACHED) {
                disconnect()
            }
        }
    }


}