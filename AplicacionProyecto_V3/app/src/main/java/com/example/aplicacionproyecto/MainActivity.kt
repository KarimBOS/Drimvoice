package com.example.aplicacionproyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private val notes = mutableListOf<Note>()

    companion object {
        const val REQUEST_CODE_ADD_NOTE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Inicializar el DrawerLayout y la NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)

        // Configurar el Toolbar y el ActionBarDrawerToggle
        val toolbar: Toolbar = findViewById(R.id.tooolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Configurar el botón para agregar nuevas notas
        findViewById<FloatingActionButton>(R.id.fabAddNote).setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            intent.putStringArrayListExtra("existing_titles", ArrayList(notes.map { it.title }))
            startActivityForResult(intent, REQUEST_CODE_ADD_NOTE)
        }

        // Cargar notas desde Firebase
        loadUserNotes()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK) {
            val newNoteContent = data?.getStringExtra("nota_texto") ?: ""
            val newNoteTitle = data?.getStringExtra("nota_titulo") ?: ""

            if (newNoteContent.isNotEmpty() && newNoteTitle.isNotEmpty()) {
                val note = Note(title = newNoteTitle, content = newNoteContent, id = "")
                notes.add(note) // Agregar la nueva nota a la lista
                saveNoteToFirestore(note) // Guardar en Firestore
                addNoteToDrawer(newNoteTitle) // Agregar al menú lateral
            }
        }
    }

    private fun loadUserNotes() {
        val user = auth.currentUser

        if (user != null) {
            firestore.collection("users")
                .document(user.uid)
                .collection("notes")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        val noteId = document.id // Usamos el ID del documento
                        val noteTitle = document.getString("title") ?: "Sin título"
                        val noteContent = document.getString("content") ?: "Sin contenido"
                        val note = Note(title = noteTitle, content = noteContent, id = noteId)
                        notes.add(note) // Agregar a la lista local
                        addNoteToDrawer(noteTitle) // Agregar al menú lateral
                    }
                    Toast.makeText(this, "Notas cargadas correctamente", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al cargar notas: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveNoteToFirestore(note: Note) {
        val user = auth.currentUser

        if (user != null) {
            val noteData = hashMapOf(
                "title" to note.title,
                "content" to note.content
            )
            firestore.collection("users")
                .document(user.uid)
                .collection("notes")
                .document() // Firestore asignará un ID automáticamente
                .set(noteData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Nota guardada en Firebase", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al guardar nota: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun addNoteToDrawer(noteTitle: String) {
        val menu = navigationView.menu
        menu.add(noteTitle).setOnMenuItemClickListener {
            val note = notes.find { it.title == noteTitle }
            if (note != null) {
                val intent = Intent(this, VisualizarNota::class.java)
                intent.putExtra("noteTitle", note.title)
                intent.putExtra("noteContent", note.content)
                intent.putExtra("noteId", note.id)
                startActivity(intent)
            }
            true
        }
    }
}
