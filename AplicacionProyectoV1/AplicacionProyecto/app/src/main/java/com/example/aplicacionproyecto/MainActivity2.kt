package com.example.aplicacionproyecto

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity2 : AppCompatActivity() {
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var textView: TextView
    private lateinit var button: Button
    private lateinit var editText: EditText
    private val REQUEST_CODE = 123 // Código de solicitud para permisos
    private var isListening = false // Controla si el botón está presionado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // Ajustar márgenes para barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar vistas
        textView = findViewById(R.id.transcriptionOutput)
        button = findViewById(R.id.button2)
        editText = findViewById(R.id.editTextText)

        // Inicializar SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(
                this,
                "El reconocimiento de voz no está disponible en este dispositivo",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Configurar reconocimiento de voz
        speechRecognizer.setRecognitionListener(object : android.speech.RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { }
            override fun onBeginningOfSpeech() {
                textView.text = "Escuchando..."
            }

            override fun onRmsChanged(rmsdB: Float) { }
            override fun onBufferReceived(buffer: ByteArray?) { }
            override fun onEndOfSpeech() {
                if (isListening) startListening() // Reinicia si sigue presionado
            }

            override fun onError(error: Int) {
                if (isListening) startListening() // Reinicia en caso de error
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val recognizedText = matches[0]
                    textView.text = recognizedText

                }
                if (isListening) startListening() // Reinicia tras resultados si sigue presionado
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!partial.isNullOrEmpty()) {
                    textView.text = partial[0] // Actualiza texto en tiempo real
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) { }
        })

        // Configurar botón para mantener reconocimiento mientras esté presionado
        button.setOnTouchListener { _, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    isListening = true
                    startListening()
                }
                android.view.MotionEvent.ACTION_UP -> {
                    isListening = false
                    stopListening()
                }
            }
            true
        }

        // Botón para navegar atrás
        findViewById<Button>(R.id.botonAtrasSegundaPantalla).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun startListening() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_CODE
            )
            return
        }

        // Configurar intent para reconocimiento
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-MX")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        speechRecognizer.startListening(intent)
    }

    private fun stopListening() {
        speechRecognizer.stopListening()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening()
        } else {
            Toast.makeText(this, "Se necesita permiso para grabar audio", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy() // Liberar recursos
    }
}
