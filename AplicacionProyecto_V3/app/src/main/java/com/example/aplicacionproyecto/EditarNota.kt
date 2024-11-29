package com.example.aplicacionproyecto

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditarNota : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_nota)

        firestore = FirebaseFirestore.getInstance()

        // Recuperar los datos enviados desde VisualizarNota
        val noteId = intent.getStringExtra("noteId")
        val noteTitle = intent.getStringExtra("noteTitle")
        val noteContent = intent.getStringExtra("noteContent")

        // Referencias a los campos de texto y botón
        val titleEditText: EditText = findViewById(R.id.editNoteTitle)
        val contentEditText: EditText = findViewById(R.id.editNoteContent)
        val saveButton: Button = findViewById(R.id.saveButton)

        // Configurar los campos con los datos actuales de la nota
        titleEditText.setText(noteTitle)
        contentEditText.setText(noteContent)

        // Botón para guardar cambios
        saveButton.setOnClickListener {
            val updatedTitle = titleEditText.text.toString()
            val updatedContent = contentEditText.text.toString()

            if (noteId != null) {
                updateNoteInFirestore(noteId, updatedTitle, updatedContent)
            } else {
                Toast.makeText(this, "ID de la nota no válido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateNoteInFirestore(noteId: String, title: String, content: String) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            // Mapa con los datos a actualizar
            val noteData: HashMap<String, Any> = hashMapOf(
                "title" to title,
                "content" to content
            )

            firestore.collection("users")
                .document(user.uid)
                .collection("notes")
                .document(noteId)
                .update(noteData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Nota actualizada correctamente", Toast.LENGTH_SHORT).show()
                    finish() // Regresa a la pantalla anterior
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al actualizar la nota: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }
}
