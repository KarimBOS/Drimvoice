package com.example.aplicacionproyecto

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class VisualizarNota : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualizar_nota)

        // Recuperar los datos enviados
        val noteTitle = intent.getStringExtra("noteTitle")
        val noteContent = intent.getStringExtra("noteContent")

        // Configurar la vista con los datos
        val titleTextView: TextView = findViewById(R.id.noteTitleTextView)
        val contentTextView: TextView = findViewById(R.id.noteContentTextView)

        titleTextView.text = noteTitle
        contentTextView.text = noteContent ?: "No hay contenido disponible"
    }
}
