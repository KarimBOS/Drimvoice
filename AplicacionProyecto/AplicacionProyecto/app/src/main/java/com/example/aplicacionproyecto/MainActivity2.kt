package com.example.aplicacionproyecto

import android.content.Intent
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
import android.Manifest
import android.content.pm.PackageManager

class MainActivity2 : AppCompatActivity() {
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var textView: TextView
    private lateinit var button: Button
    private lateinit var editText: EditText
    private val REQUEST_CODE = 123 // Código de solicitud para el reconocimiento de voz

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // Configurar los márgenes para las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar las vistas
        textView = findViewById(R.id.textView2)
        button = findViewById(R.id.button2)
        editText = findViewById(R.id.editTextText)


        // Inicializar el SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "El reconocimiento de voz no está disponible en este dispositivo", Toast.LENGTH_SHORT).show()
            return
        }

        // Configurar el reconocimiento de voz
        speechRecognizer.setRecognitionListener(object : android.speech.RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { }
            override fun onBeginningOfSpeech() { }
            override fun onRmsChanged(rmsdB: Float) { }
            override fun onBufferReceived(buffer: ByteArray?) { }
            override fun onEndOfSpeech() { }
            override fun onError(error: Int) {
                val message = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Error de audio"
                    SpeechRecognizer.ERROR_CLIENT -> "Error en el cliente"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permisos insuficientes"
                    SpeechRecognizer.ERROR_NETWORK -> "Error de red"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Tiempo de espera de red agotado"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No se encontró una coincidencia"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "El reconocimiento de voz está ocupado"
                    SpeechRecognizer.ERROR_SERVER -> "Error en el servidor"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No se detectó entrada de voz"
                    else -> "Error desconocido"
                }
                Toast.makeText(this@MainActivity2, "Error en el reconocimiento de voz", Toast.LENGTH_SHORT).show()
            }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null && matches.isNotEmpty()) {
                    val recognizedText = matches[0]
                    textView.text = recognizedText
                    editText.setText(recognizedText)
                }
            }
            override fun onPartialResults(partialResults: Bundle?) { }
            override fun onEvent(eventType: Int, params: Bundle?) { }
        })

        // Configurar el botón para iniciar y detener el reconocimiento de voz
        button.setOnTouchListener { v, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> startListening()
                android.view.MotionEvent.ACTION_UP -> stopListening()
            }
            true
        }

        // Configurar el botón para navegar hacia atrás
        val botonNavegarAtras: Button = findViewById(R.id.botonAtrasSegundaPantalla)
        botonNavegarAtras.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startListening() {
        val intentt = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intentt.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechRecognizer.startListening(intentt)


        // Si no tienes el permiso, solicita el permiso para grabar audio
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_CODE)
            return
        }
        // Configurar el Intent para iniciar el reconocimiento de voz
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechRecognizer.startListening(intent)
    }

    private fun stopListening() {
        // Detener el reconocimiento de voz
        speechRecognizer.stopListening()
    }

    // Asegúrate de manejar el resultado de los permisos si el usuario los solicita
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening()
        } else {
            Toast.makeText(this, "Se necesita permiso para grabar audio", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Liberar recursos cuando la actividad se destruye
        speechRecognizer.destroy()
    }
}
