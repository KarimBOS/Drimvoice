package com.example.aplicacionproyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class VisualizarNota : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualizar_nota)

        firestore = FirebaseFirestore.getInstance()

        val noteId = intent.getStringExtra("noteId")
        val noteTitle = intent.getStringExtra("noteTitle")
        val noteContent = intent.getStringExtra("noteContent")

        val titleTextView: TextView = findViewById(R.id.noteTitleTextView)
        val contentTextView: TextView = findViewById(R.id.noteContentTextView)
        val deleteButton: Button = findViewById(R.id.deleteButton)
        val editButton: Button = findViewById(R.id.editButton)

        titleTextView.text = noteTitle
        contentTextView.text = noteContent ?: "No hay contenido disponible"

        // Botón para eliminar nota
        deleteButton.setOnClickListener {
            if (noteId != null) {
                deleteNoteFromFirestore(noteId)
            } else {
                Toast.makeText(this, "ID de la nota no válido", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón para editar nota
        editButton.setOnClickListener {
            val intent = Intent(this, EditarNota::class.java)
            intent.putExtra("noteId", noteId)
            intent.putExtra("noteTitle", noteTitle)
            intent.putExtra("noteContent", noteContent)
            startActivity(intent)
        }
    }

    private fun deleteNoteFromFirestore(noteId: String) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            firestore.collection("users")
                .document(user.uid)
                .collection("notes")
                .document(noteId)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Nota eliminada correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al eliminar la nota: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }
}
