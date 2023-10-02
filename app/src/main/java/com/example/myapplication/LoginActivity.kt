package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.concurrent.thread

class LoginActivity : AppCompatActivity() {
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (checkUserDataInCache()) {
            startHomeActivity()
        } else {
            setContentView(R.layout.activity_login)
            initializeViews()
            setLoginButtonClickHandler()
        }
    }

    private fun startHomeActivity() {
        val intent = Intent(this, FarmsActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun initializeViews() {
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
    }

    private fun setLoginButtonClickHandler() {
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                performLogin(username, password)
            } else {
                showToast("Preencha todos os campos")
            }
        }
    }

    private fun performLogin(username: String, password: String) {
        val client = OkHttpClient()

        val json = """
            {
                "login": "$username",
                "password": "$password"
            }
        """.trimIndent()

        val mediaType = "application/json; charset=utf-8".toMediaType()

        val requestBody = json.toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://intelicampo-api-stg.vercel.app/auth")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        thread {
            try {
                val response = client.newCall(request).execute()

                if (response.code == 201) {
                    val responseBody = response.body?.string()

                    if (!responseBody.isNullOrEmpty()) {
                        val loginResponse = Gson().fromJson(responseBody, LoginResponse::class.java)
                        handleLoginSuccess(loginResponse)
                    }
                } else {
                    showToast("Erro ao fazer login. Verifique suas credenciais.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Erro ao fazer login. Tente novamente mais tarde.")
            }
        }
    }

    private fun handleLoginSuccess(loginResponse: LoginResponse) {
        val accessToken = loginResponse.accessToken
        val userId = loginResponse.userId
        val accountId = loginResponse.accountId
        val username = loginResponse.name
        val farms = loginResponse.farm

        saveUserDataToCache(username, accessToken, userId, accountId)
        saveFarmsToCache(farms)


        runOnUiThread {
            val toastMessage = "Login bem-sucedido!\n"
            showToast(toastMessage)

            startHomeActivity()
        }
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserDataToCache(username: String, accessToken: String, userId: Int, accountId: Int) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("username", username)
        editor.putString("accessToken", accessToken)
        editor.putInt("userId", userId)
        editor.putInt("accountId", accountId)

        editor.apply()
    }

    private fun checkUserDataInCache(): Boolean {
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)

        return sharedPreferences.contains("username") &&
                sharedPreferences.contains("accessToken") &&
                sharedPreferences.contains("userId") &&
                sharedPreferences.contains("accountId")
    }
    private fun saveFarmsToCache(farms: List<Farm>) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val gson = Gson()
        val farmsJson = gson.toJson(farms)

        editor.putString("farms", farmsJson)

        editor.apply()
    }

}

data class LoginResponse(
    val accessToken: String,
    val userId: Int,
    val accountId: Int,
    val name: String,
    val farm: List<Farm>

)

data class Farm(
    val id: Int,
    val name: String,

    )
