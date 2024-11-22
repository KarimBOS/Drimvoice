package com.example.aplicacionproyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.app.ActionBarDrawerToggle

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar el Toolbar
        val tooolbar: Toolbar = findViewById(R.id.tooolbar)

        // Establecer el Toolbar como ActionBar
        setSupportActionBar(tooolbar)

        // Eliminar el logo del Toolbar si no lo quieres mostrar
        supportActionBar?.setDisplayShowHomeEnabled(false)

        // Maneja los insets para evitar superposición con los system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configura la navegación a otra actividad
        val botonNavegar: Button = findViewById(R.id.BotonNuevaNota)
        botonNavegar.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

        // Configura la Toolbar
        val toolbar: Toolbar = findViewById(R.id.tooolbar)
        setSupportActionBar(toolbar)

        // Configura el DrawerLayout y el botón de menú hamburguesa
        drawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Configura los eventos del NavigationView
        val navigationView: NavigationView = findViewById(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // Acción para la opción "Inicio"
                }
                R.id.nav_profile -> {
                    // Acción para la opción "Perfil"
                }
                R.id.nav_settings -> {
                    // Acción para la opción "Configuración"
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


}
