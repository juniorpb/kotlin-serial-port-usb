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
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class LoginActivity : AppCompatActivity() {
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verifique se os dados estão em cache
        if (checkUserDataInCache()) {
            // Se os dados estiverem em cache, vá para a tela principal diretamente
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // Encerre a atividade atual para impedir o retorno à tela de login
        } else {
            // Se os dados não estiverem em cache, continue carregando a tela de login
            setContentView(R.layout.activity_login)

            etUsername = findViewById(R.id.etUsername)
            etPassword = findViewById(R.id.etPassword)
            btnLogin = findViewById(R.id.btnLogin)

            btnLogin.setOnClickListener {
                val username = etUsername.text.toString()
                val password = etPassword.text.toString()

                // Verifique se o CPF e a senha foram preenchidos
                if (username.isNotEmpty() && password.isNotEmpty()) {
                    // Se os dados não estiverem em cache, faça a solicitação de login
                    performLogin(username, password)
                } else {
                    Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                }
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

        Thread {
            try {
                val response = client.newCall(request).execute()

                if (response.code == 201) {
                    val responseBody = response.body?.string()

                    if (!responseBody.isNullOrEmpty()) {
                        val gson = Gson()
                        val loginResponse = gson.fromJson(responseBody, LoginResponse::class.java)

                        // Preencha as variáveis com os dados da resposta
                        val accessToken = loginResponse.accessToken
                        val userId = loginResponse.userId
                        val accountId = loginResponse.accountId
                        val username = loginResponse.name

                        // Salve os dados em cache
                        saveUserDataToCache(username, accessToken, userId, accountId)

                        runOnUiThread {
                            // Exiba um Toast com os dados obtidos
                            val toastMessage = "Login bem-sucedido!\n" +
                                    "Access Token: $accessToken\n" +
                                    "User ID: $userId\n" +
                                    "Account ID: $accountId"
                            Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show()

                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Erro ao fazer login. Verifique suas credenciais.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Erro ao fazer login. Tente novamente mais tarde.", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun saveUserDataToCache(username: String, accessToken: String, userId: Int, accountId: Int) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Salvar os dados em cache
        editor.putString("username", username)
        editor.putString("accessToken", accessToken)
        editor.putInt("userId", userId)
        editor.putInt("accountId", accountId)

        editor.apply()
    }

    private fun checkUserDataInCache(): Boolean {
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)

        // Verificar se os dados existem em cache
        return sharedPreferences.contains("username") &&
                sharedPreferences.contains("accessToken") &&
                sharedPreferences.contains("userId") &&
                sharedPreferences.contains("accountId")
    }
}

data class LoginResponse(
    val accessToken: String,
    val userId: Int,
    val accountId: Int,
    val name: String
    // Adicione outros campos da resposta aqui
)
