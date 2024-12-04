package com.example.spaceapp_quizapi

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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
        val FragManager = supportFragmentManager;
        val FragTransition = FragManager.beginTransaction()
        FragTransition.replace(R.id.NavFrag, frag)
        FragTransition.commit()
    }
}