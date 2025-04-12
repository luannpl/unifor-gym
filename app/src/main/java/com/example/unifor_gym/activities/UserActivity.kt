package com.example.unifor_gym.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.unifor_gym.R
import com.example.unifor_gym.fragments.Aulas
import com.example.unifor_gym.fragments.HomeAdmin
import com.example.unifor_gym.fragments.HomeUsuario
import com.example.unifor_gym.fragments.Notificacoes
import com.example.unifor_gym.fragments.PerfilUsuario
import com.example.unifor_gym.fragments.Treinos
import com.google.android.material.bottomnavigation.BottomNavigationView

class UserActivity : AppCompatActivity() {

    lateinit var buttonUserMenu : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        setFragment(HomeUsuario())

        buttonUserMenu = findViewById(R.id.bottomNavUser)
        buttonUserMenu.selectedItemId = R.id.nav_home_user

        buttonUserMenu.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId){
                R.id.nav_home_user -> HomeUsuario()
                R.id.nav_exercicios_user -> Treinos()
                R.id.nav_aulas_user -> Aulas()
                R.id.nav_notificacoes_user -> Notificacoes()
                R.id.nav_usuarios_user -> PerfilUsuario()
                else -> null
            }
            return@setOnItemSelectedListener if(fragment !=null){
                setFragment(fragment)
                true
            }else{
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