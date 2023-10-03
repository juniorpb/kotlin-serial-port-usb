package com.example.myapplication
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

class CacheManager(private val context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("cached_data", Context.MODE_PRIVATE)
    private val _requestResult = MutableLiveData<Boolean>()
    val requestResult: LiveData<Boolean> = _requestResult

    fun isNetworkConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }


    fun cacheDataLocally(data: String) {
        val editor = sharedPreferences.edit()
        editor.putString("cached_data", data)
        editor.apply()
    }

    fun getCachedData(): String? {
        return sharedPreferences.all.toString()
    }

    fun sendCachedDataToApi() {
        val apiUrl = "https://intelicampo-api-stg.vercel.app/animal"

        GlobalScope.launch(Dispatchers.IO) {
            val cachedData = getCachedData()
            val url = URL(apiUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            try {
                val jsonObject = JSONObject(cachedData)
                val dataToSend = jsonObject.getJSONObject("cached_data")
                val outputStreamWriter = OutputStreamWriter(connection.outputStream)
                outputStreamWriter.write(dataToSend.toString())
                outputStreamWriter.flush()
                outputStreamWriter.close()

                val responseCode = connection.responseCode
                val success = responseCode == 201

                if (success) {
                    clearCache()
                    _requestResult.postValue(success)
                }

                connection.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun clearCache() {
        val editor = sharedPreferences.edit()
        editor.remove("cached_data")
        editor.apply()
    }
    fun hasCache(): Boolean {
        return sharedPreferences.contains("cached_data")
    }

}
