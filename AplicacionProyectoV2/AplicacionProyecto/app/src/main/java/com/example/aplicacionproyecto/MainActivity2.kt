package com.example.aplicacionproyecto

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity2 : AppCompatActivity() {
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var textView: TextView
    private lateinit var button: Button
    private lateinit var clearButton: Button
    private lateinit var editText: EditText
    private val REQUEST_CODE = 123
    private var isListening = false
    private var accumulatedText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // Ajustar márgenes para barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textView = findViewById(R.id.transcriptionOutput)
        button = findViewById(R.id.button2)
        clearButton = findViewById(R.id.clearButton) // Botón para borrar texto

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "El reconocimiento de voz no está disponible en este dispositivo", Toast.LENGTH_SHORT).show()
            return
        }

        // Configuración del reconocimiento de voz
        speechRecognizer.setRecognitionListener(object : android.speech.RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {
                textView.text = "Escuchando..."
            }

            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                if (isListening) startListening()
            }

            override fun onError(error: Int) {
                if (isListening) startListening()
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val recognizedText = matches[0]
                    accumulatedText += " $recognizedText"
                    textView.text = accumulatedText.trim()
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!partial.isNullOrEmpty()) {
                    textView.text = "${accumulatedText.trim()} ${partial[0]}"
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        // Configuración del botón para hablar
        button.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isListening = true
                    startListening()
                }
                MotionEvent.ACTION_UP -> {
                    isListening = false
                    stopListening()
                }
            }
            true
        }

        // Configuración del botón para borrar texto
        clearButton.setOnClickListener {
            accumulatedText = ""
            textView.text = ""
            Toast.makeText(this, "Texto borrado", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.botonAtrasSegundaPantalla).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun startListening() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_CODE)
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-MX")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        speechRecognizer.startListening(intent)
    }

    private fun stopListening() {
        speechRecognizer.stopListening()

        if (accumulatedText.isNotBlank()) {
            showTitleInputDialog()
        } else {
            Toast.makeText(this, "No se puede guardar una nota vacía", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showTitleInputDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Guardar nota")
        dialogBuilder.setMessage("Introduce un título para la nota de voz")

        // Campo de texto para el título
        val input = EditText(this)
        dialogBuilder.setView(input)

        dialogBuilder.setPositiveButton("Guardar") { _, _ ->
            val title = input.text.toString().trim()
            if (title.isNotEmpty()) {
                val resultIntent = Intent()
                resultIntent.putExtra("nota_titulo", title)
                resultIntent.putExtra("nota_texto", accumulatedText.trim())
                setResult(RESULT_OK, resultIntent)

                // Guardar nota en el menú lateral (simulado)
                val sharedPrefs = getSharedPreferences("NotasGuardadas", MODE_PRIVATE)
                val editor = sharedPrefs.edit()
                val notes = sharedPrefs.getStringSet("notas", mutableSetOf()) ?: mutableSetOf()
                notes.add("$title: ${accumulatedText.trim()}")
                editor.putStringSet("notas", notes)
                editor.apply()

                finish()
            } else {
                Toast.makeText(this, "El título no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
        }

        dialogBuilder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        dialogBuilder.create().show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening()
        } else {
            Toast.makeText(this, "Se necesita permiso para grabar audio", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }
}
