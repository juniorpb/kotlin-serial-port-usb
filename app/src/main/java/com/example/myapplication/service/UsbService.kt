package com.example.myapplication.service

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.util.Log
import com.felhr.usbserial.UsbSerialDevice
import com.felhr.usbserial.UsbSerialInterface

class UsbService(m_usbManager: UsbManager, contextClass: Context?) {

    var context = contextClass
    var m_usbManager = m_usbManager
    var m_device: UsbDevice? = null
    var m_serial: UsbSerialDevice? = null
    var m_connection: UsbDeviceConnection? = null

    val ACTION_USB_PERMISSION = "permission"

     fun startUsbConnecting() {
        Log.i("serial", "starting connections")

        val usbDevices: HashMap<String, UsbDevice>? = m_usbManager.deviceList

        if (!usbDevices?.isEmpty()!!) {
            usbDevices.forEach { entry ->

                try {
                    m_device = entry.value
                    val deviceVendorId: Int? = m_device?.vendorId
                    Log.i("serial", "vendorId: " + deviceVendorId)

                    val intent: PendingIntent = PendingIntent.getBroadcast(
                        context,
                        0,
                        Intent(ACTION_USB_PERMISSION),
                        PendingIntent.FLAG_MUTABLE
                    )

                    m_usbManager.requestPermission(m_device, intent)

                    val al2 = AlertDialog.Builder(context)
                    al2.setTitle("id")
                    al2.setMessage("m_device ${m_device!!.manufacturerName}")
                    al2.show()


                } catch (e: Exception) {
                    val alertError = AlertDialog.Builder(context)
                    alertError.setTitle("error")
                    alertError.setMessage("message: ${e.message}")
                    alertError.show()
                }

            }
        } else {
            Log.i("serial", "no usb device connected")

            val alertError = AlertDialog.Builder(context)
            alertError.setTitle("info")
            alertError.setMessage("no usb device connected")
            alertError.show()
        }
    }

     fun sendData(input: String) {
        m_serial?.write(input.toByteArray())
        Log.i("serial", "sending data: " + input.toByteArray())
    }

     fun disconnect() {
        m_serial?.close()
    }

     val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action!! == ACTION_USB_PERMISSION) {

                val granted: Boolean = intent.extras!!.getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED)

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
                            m_serial!!.read(usbSerialListener)

                            //statusConnectionTextView.text = "Connected Success!"

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
                //statusConnectionTextView.text = "No Connected"
                disconnect()

            }
        }
    }


    private val usbSerialListener = object : UsbSerialInterface.UsbReadCallback {
        override fun onReceivedData(data: ByteArray) {
            try {
                val receivedString = String(data)

                if (receivedString == "A") {
                    //myTextView.text = "A"
                }

                if (receivedString == "B") {
                    //myTextView.text = "B"
                }

            } catch (e: Exception) {
                //myTextView.text = e.message
            }
        }
    }



}