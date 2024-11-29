package com.example.aplicacionproyecto

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnRegister: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Referencias a los elementos del layout
        edtEmail = findViewById(R.id.edtRegisterEmail)
        edtPassword = findViewById(R.id.edtRegisterPassword)
        btnRegister = findViewById(R.id.btnRegister)

        val tvRegister: TextView = findViewById(R.id.tvGoToLogin)
        val spannableText = SpannableString("¿Ya tienes cuenta? Inicia sesión aquí")

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Navegar a la actividad de registro
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        // Aplicar el ClickableSpan a la palabra "aquí"
        spannableText.setSpan(clickableSpan, 33, 37, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvRegister.text = spannableText
        tvRegister.movementMethod = LinkMovementMethod.getInstance() // Habilitar clicks


        // Listener del botón de registro
        btnRegister.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear cuenta con Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Guardar en Firestore
                        val user = auth.currentUser
                        val userData = hashMapOf(
                            "email" to email,
                            "password" to password
                        )

                        firestore.collection("users")
                            .document(user?.uid ?: "")
                            .set(userData)
                            .addOnSuccessListener {
                                // Crear la subcolección "notes" para el usuario
                                val notesCollection = firestore.collection("users")
                                    .document(user?.uid ?: "")
                                    .collection("notes")

                                // Crear un documento de ejemplo para la subcolección "notes"
                                val exampleNote = hashMapOf(
                                    "id" to "1", // ID de la nota
                                    "title" to "Nota Ejemplo", // Nombre de la nota
                                    "content" to "Este es el contenido de la nota 1." // Contenido de la nota
                                )

                                notesCollection.document("1") // El ID de la nota será "1"
                                    .set(exampleNote)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Registro exitoso y nota de ejemplo agregada", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Error al agregar la nota de ejemplo: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }

                                // Ir al LoginActivity
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish() // Finaliza el RegisterActivity
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al guardar en Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Error en el registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
