package com.example.myapplication.component.dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.myapplication.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CustomBottomSheetDialogFragment(resource: Int, rfidText: String) : BottomSheetDialogFragment() {

    private val resourceLayout = resource
    private val rfidTextValue = rfidText

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(resourceLayout, container, false)

        if (resourceLayout == R.layout.activity_valid_rfid) {
            val textView = view.findViewById<TextView>(R.id.textRfid)
            textView.text = rfidTextValue
        }

        if (resourceLayout == R.layout.ler_rfid_layout) {
            val textView = view.findViewById<TextView>(R.id.textViewCount)

            GlobalScope.launch {
                for (i in 5 downTo 1) {
                    textView.text = i.toString()
                    withContext(Dispatchers.IO) {
                        Thread.sleep(1000) // delay de 1 segundo
                    }
                }

            }
        }


        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        // Defina o estado do BottomSheet para STATE_EXPANDED
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED

            // Bloqueia a ação de puxar o BottomSheetDialog para baixo
            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    // Impede que o BottomSheet seja ocultado quando o usuário tenta arrastá-lo para baixo
                    if (newState == BottomSheetBehavior.STATE_HIDDEN || newState == BottomSheetBehavior.STATE_DRAGGING) {
                        behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    // Você pode adicionar lógica personalizada aqui, se necessário
                }
            })
        }


        dialog?.setCanceledOnTouchOutside(false)

        // Bloqueia o fechamento do BottomSheetFragment quando o usuário pressiona o botão "Voltar"
        dialog?.setOnKeyListener { _, keyCode, _ ->
            keyCode == KeyEvent.KEYCODE_BACK
        }
    }



}
