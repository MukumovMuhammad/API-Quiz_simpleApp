package com.example.spaceapp_quizapi

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.spaceapp_quizapi.model.Apod_data

import com.google.android.material.bottomnavigation.BottomNavigationView
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType.Application.Json
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class MainActivity : AppCompatActivity() {
    lateinit var client: HttpClient



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
       enableEdgeToEdge()
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





        replace_fragemnt(NasaNews())
        val navbar = findViewById<BottomNavigationView>(R.id.navBar)

        navbar.setOnItemSelectedListener {

            when(it.itemId){
                R.id.navbtn_home ->replace_fragemnt(NasaNews())
                R.id.navbtn_quiz ->replace_fragemnt(Quiz())

                else ->{

                }
            }
            true
        }
    }


    private fun replace_fragemnt(frag: Fragment){
        GlobalScope.launch {
            var apod_data = getApod().title
            Log.i("API", "Yahoo Gor data -> $apod_data")
        }

        val FragManager = supportFragmentManager;
        val FragTransition = FragManager.beginTransaction()
        FragTransition.replace(R.id.NavFrag, frag)
        FragTransition.commit()
    }


    suspend fun getApod(): Apod_data {

        var  response = client.get("https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY")


        val json = Json { ignoreUnknownKeys = true }

        return json.decodeFromString<Apod_data>(response.bodyAsText())
    }
}