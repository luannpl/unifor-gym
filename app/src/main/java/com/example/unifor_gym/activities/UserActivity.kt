package com.example.unifor_gym.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.unifor_gym.R
import com.example.unifor_gym.fragments.Aulas
import com.example.unifor_gym.fragments.HomeUsuario
import com.example.unifor_gym.fragments.Notificacoes
import com.example.unifor_gym.fragments.PerfilUsuario
import com.example.unifor_gym.fragments.UsuarioTreinos
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class UserActivity : AppCompatActivity() {

    lateinit var buttonUserMenu: BottomNavigationView
    lateinit var fabChat: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        setFragment(HomeUsuario())

        // Configurar o menu de navegação inferior
        buttonUserMenu = findViewById(R.id.bottomNavUser)
        buttonUserMenu.selectedItemId = R.id.nav_home_user

        // Encontrar o FloatingActionButton de chat
        fabChat = findViewById(R.id.fab_chat)

        // Configurar clique no botão de chat para abrir a ChatActivity
        fabChat.setOnClickListener {
            val intent = Intent(this, Chat::class.java)
            startActivity(intent)
        }

        // Configurar o listener do menu inferior
        buttonUserMenu.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_home_user -> HomeUsuario()
                R.id.nav_exercicios_user -> UsuarioTreinos()
                R.id.nav_aulas_user -> Aulas()
                R.id.nav_notificacoes_user -> Notificacoes()
                R.id.nav_usuarios_user -> PerfilUsuario()
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

    // Método para carregar os fragments
    private fun setFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }
}