package com.example.aplicacionproyecto

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.ActionBarDrawerToggle

data class Note(val title: String, val content: String)

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private val notes = mutableListOf<Note>()  // Lista para almacenar las notas

    companion object {
        const val REQUEST_CODE_ADD_NOTE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar la Toolbar
        val toolbar: Toolbar = findViewById(R.id.tooolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)

        // Configurar el DrawerToggle para abrir y cerrar el menú lateral
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Inflar el menú lateral
        navigationView.inflateMenu(R.menu.drawer_menu)

        // Configurar el título del grupo de notas
        val menu: Menu = navigationView.menu
        menu.findItem(R.id.note_group)?.title = "Notas Guardadas"

        // Manejar el botón de nueva nota
        val newNoteButton: Button = findViewById(R.id.BotonNuevaNota)
        newNoteButton.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_NOTE)
        }
    }

    // Método para agregar una nueva nota al menú lateral
    private fun addNoteToDrawer(noteTitle: String) {
        val menu: Menu = navigationView.menu
        val noteGroup = menu.findItem(R.id.note_group)?.subMenu

        if (noteGroup != null) {
            noteGroup.add(Menu.NONE, Menu.NONE, Menu.NONE, noteTitle)
                ?.setIcon(android.R.drawable.ic_menu_edit)
                ?.setOnMenuItemClickListener { item ->
                    handleNoteClick(item.title.toString())
                    true
                }
            navigationView.invalidate()
        } else {
            Toast.makeText(this, "No se encontró el submenú para agregar notas.", Toast.LENGTH_SHORT).show()
        }
    }

    // Manejar el clic en una nota


    // Controlar el comportamiento al presionar el botón de retroceso
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    // Obtener los resultados al regresar de la actividad secundaria para guardar una nueva nota
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK) {
            val newNote = data?.getStringExtra("nota_texto") ?: ""
            val noteTitle = data?.getStringExtra("nota_titulo") ?: ""
            if (newNote.isNotEmpty()) {
                notes.add(Note(title = noteTitle, content = newNote))  // Guardamos la nueva nota con título y contenido
                addNoteToDrawer(noteTitle)  // Añadimos solo el título al menú lateral
            }
        }
    }
    private fun handleNoteClick(noteTitle: String) {
        // Busca la nota completa por título
        val note = notes.find { it.title == noteTitle }

        // Si la nota existe, abre la actividad VisualizarNota
        note?.let {
            val intent = Intent(this, VisualizarNota::class.java).apply {
                putExtra("noteTitle", it.title)
                putExtra("noteContent", it.content)
            }
            startActivity(intent)
        }
    }
}