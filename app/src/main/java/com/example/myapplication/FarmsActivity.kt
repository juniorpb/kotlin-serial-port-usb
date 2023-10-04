package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FarmsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var farms: List<Farm> // Lista de fazendas em cache
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_farms)

        recyclerView = findViewById(R.id.recycler_view)


        recyclerView.layoutManager = LinearLayoutManager(this)

        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)

        // Carregue os dados do JSON em cache para a lista farmList
        farms = loadFarmsFromCache()

        // Configure o adaptador
        val adapter = FarmsAdapter()
        recyclerView.adapter = adapter
    }

    private fun loadFarmsFromCache(): List<Farm> {
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val farmsJson = sharedPreferences.getString("farms", null)

        val gson = Gson()
        return gson.fromJson(farmsJson, object : TypeToken<List<Farm>>() {}.type)
            ?: emptyList()
    }

    private inner class FarmsAdapter : RecyclerView.Adapter<FarmsAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.card_farm, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val farm = farms[position]
            holder.textViewFarmName.text = farm.name

            holder.itemView.setOnClickListener {

                val editor = sharedPreferences.edit()
                editor.putInt("selectedFarmId", farm.id)
                editor.putString("selectedFarmName", farm.name)
                editor.apply()

                // Navegue para a tela de home
                val intent = Intent(this@FarmsActivity, HomeActivity::class.java)
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return farms.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var textViewFarmName: TextView = itemView.findViewById(R.id.text_view_farm_name)
        }
    }
}

