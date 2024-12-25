package com.example.spaceapp_quizapi


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.spaceapp_quizapi.model.Apod_data


import com.google.android.material.bottomnavigation.BottomNavigationView
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json




class MainActivity : AppCompatActivity() {
    companion object{
        const val TAG_API = "APIs"
    }
    lateinit var client: HttpClient
    private var apod_data : Apod_data = Apod_data("","","","")



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

        GlobalScope.launch {
            apod_data = getApod()
            Log.i(TAG_API, "Response we got -> " + apod_data.title)

            replace_fragemnt(NasaNews())
        }





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
        var bundle = Bundle()
        bundle.putParcelable("apod_data", apod_data)
        frag.arguments = bundle

        val FragManager = supportFragmentManager;
        val FragTransition = FragManager.beginTransaction()
        FragTransition.replace(R.id.NavFrag, frag)
        FragTransition.commit()
    }


    suspend fun getApod(): Apod_data {

        try {
            var response = client.get("https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY")
            val json = Json { ignoreUnknownKeys = true }

            Log.i(TAG_API, "response from get : " + response.bodyAsText())
            return json.decodeFromString<Apod_data>(response.bodyAsText())
        }
        catch (e : Exception){
            return Apod_data("Couldn't Recieve Data", "No internet", "No internet","No internet")
        }



    }



}