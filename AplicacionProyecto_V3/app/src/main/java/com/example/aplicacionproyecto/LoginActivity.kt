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

class LoginActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button


    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Referencias a los elementos del layout
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        btnLogin = findViewById(R.id.btnLogin)


        val tvRegister: TextView = findViewById(R.id.tvRegister)
        val spannableText = SpannableString("¿No tienes cuenta? Regístrate aquí")

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Navegar a la actividad de registro
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
        }
        // Aplicar el ClickableSpan a la palabra "aquí"
        spannableText.setSpan(clickableSpan, 30, 34, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvRegister.text = spannableText
        tvRegister.movementMethod = LinkMovementMethod.getInstance() // Habilitar clicks




        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Iniciar sesión con Firebase Authentication
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Verificar si el usuario existe en Firestore
                        val userId = auth.currentUser?.uid
                        if (userId != null) {
                            firestore.collection("users")
                                .document(userId)
                                .get()
                                .addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        // Usuario válido, redirigir a MainActivity
                                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                        finish() // Finaliza el LoginActivity
                                    } else {
                                        // Usuario no encontrado en Firestore
                                        Toast.makeText(this, "Usuario no encontrado en la base de datos", Toast.LENGTH_SHORT).show()
                                        auth.signOut() // Cierra sesión si no está en Firestore
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Error al verificar usuario: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Error al iniciar sesión: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
