package com.example.spaceapp_quizapi

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.spaceapp_quizapi.model.Apod_data

import com.google.android.material.bottomnavigation.BottomNavigationView
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.IOException


class MainActivity : AppCompatActivity() {
    companion object{
        const val TAG_API = "APIs"
    }
    lateinit var client: HttpClient
    private var apodData : Apod_data = Apod_data("","","","")



    override fun onCreate(savedInstanceState: Bundle?) {



        super.onCreate(savedInstanceState)
       //enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        

        client = HttpClient(Android){
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                json()
            }
        }

        lifecycleScope.launch {
            apodData = getApod()
            Log.i(TAG_API, "Response we got -> " + apodData.title)

            replaceFragment(NasaNews())
        }





        val navbar = findViewById<BottomNavigationView>(R.id.navBar)

        navbar.setOnItemSelectedListener {

            when(it.itemId){
                R.id.navbtn_home ->replaceFragment(NasaNews())
                R.id.navbtn_quiz ->replaceFragment(Quiz())

                else ->{
                }
            }
            true
        }
    }


    private fun replaceFragment(frag: Fragment){
        var bundle = Bundle().apply {
            putParcelable("apodData", apodData)
        }
        frag.arguments = bundle

        val FragManager = supportFragmentManager;
        val FragTransition = FragManager.beginTransaction()
        FragTransition.replace(R.id.NavFrag, frag)
        FragTransition.commit()
    }


    suspend fun getApod(): Apod_data {
        try {
            val response = client.get("https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY")
            val json = Json { ignoreUnknownKeys = true }
            Log.i(TAG_API, "response from get : " + response.bodyAsText())
            return json.decodeFromString<Apod_data>(response.bodyAsText())
        } catch (e: IOException) {
            Log.e(TAG_API, "Network error: ${e.message}")
            return Apod_data("Network Error", "Please check your internet connection", "","")
        } catch (e: SerializationException) {
            Log.e(TAG_API, "Serialization error: ${e.message}")
            return Apod_data("Data Error", "Failed to parse data", "","")
        } catch (e: Exception) {
            Log.e(TAG_API, "Unknown error: ${e.message}")
            return Apod_data("Unknown Error", "Something went wrong", "","")
        }
    }



}