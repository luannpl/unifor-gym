package com.example.unifor_gym.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.unifor_gym.fragments.GestaoAparelhos
import com.example.unifor_gym.fragments.GestaoAulas
import com.example.unifor_gym.fragments.GestaoExercicios
import com.example.unifor_gym.fragments.GestaoUsuarios
import com.example.unifor_gym.fragments.HomeAdmin
import com.example.unifor_gym.R
import com.example.unifor_gym.utils.VLibrasHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AdminActivity : AppCompatActivity() {
    lateinit var bottomNavMenu: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        setFragment(HomeAdmin())

        bottomNavMenu = findViewById(R.id.bottomNavAdmin)

        bottomNavMenu.selectedItemId = R.id.nav_home_admin
    }

    override fun onStart() {
        super.onStart()

        val fabChat = findViewById<FloatingActionButton>(R.id.fab_chat)
        fabChat.setOnClickListener {
            val intent = Intent(this, Chat::class.java)
            startActivity(intent)
        }

        val fabAccess = findViewById<FloatingActionButton>(R.id.fab_access)
        fabAccess.setOnClickListener {
            VLibrasHelper.openVLibrasWithScreenContent(this)
        }

        bottomNavMenu.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_home_admin -> HomeAdmin()
                R.id.nav_usuarios_admin -> GestaoUsuarios()
                R.id.nav_aulas_admin -> GestaoAulas()
                R.id.nav_exercicios_admin -> GestaoExercicios()
                R.id.nav_aparelhos_admin -> GestaoAparelhos()
                else -> null
            }

            return@setOnItemSelectedListener if (fragment != null) {
                setFragment(fragment)
                true
            } else {
                false
            }
        }
    }

    private fun setFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }
}