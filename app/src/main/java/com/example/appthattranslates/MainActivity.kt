package com.example.appthattranslates

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.appthattranslates.databinding.ActivityMainBinding
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation.getClient
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.*

/**
 * Main activity of the application that handles the UI related to translation.
 *
 * This activity initializes the ViewModel, observes changes to user input and translation result,
 * and triggers translations using Google's MLKit. The translation mechanism is debounced to ensure
 * efficient use of resources and provide a better user experience.
 *
 * @author Matt Gacek
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: TranslationViewModel

    private var debounceJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize viewModel FIRST before any other operations that might need it
        viewModel = ViewModelProvider(this).get(TranslationViewModel::class.java)

        // THEN set up observations and other configurations
        viewModel.translatedText.observe(this, { translated ->
            if (translated.isNullOrBlank()) {
                binding.translatedText.text = "Translation"
            } else {
                binding.translatedText.text = translated
            }
        })


        setupTranslation()
    }

    private fun setupTranslation() {
        binding.sourceLanguageGroup.setOnCheckedChangeListener { _, _ ->
            translateText()
        }

        binding.targetLanguageGroup.setOnCheckedChangeListener { _, _ ->
            translateText()
        }

        viewModel.userInput.observe(this, {
            debounceJob?.cancel()
            debounceJob = coroutineScope.launch {
                delay(500)  // waits for 500ms pause in typing
                translateText()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel() // Cancel any ongoing coroutines when activity is destroyed
    }

    private fun translateText() {
        val sourceLang = getSourceLanguage()
        val targetLang = getTargetLanguage()

        if (sourceLang != null && targetLang != null) {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(sourceLang)
                .setTargetLanguage(targetLang)
                .build()

            val translator = getClient(options)

            // Define download conditions
            val conditions = com.google.mlkit.common.model.DownloadConditions.Builder()
                .requireWifi()
                .build()

            // Attempt to download the translation models
            translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener {
                    // On success, perform translation
                    performTranslation(translator)
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                    viewModel.translatedText.value = "Model download error: ${exception.message}"
                }
        }
    }



    private fun performTranslation(translator: Translator) {
        viewModel.userInput.value?.let { text ->
            translator.translate(text)
                .addOnSuccessListener { translatedText ->
                    viewModel.translatedText.value = translatedText
                }
                .addOnFailureListener { exception ->
                    viewModel.translatedText.value = "Translation error: ${exception.message}"
                }
        }
    }

    private fun getSourceLanguage(): String? {
        return when (binding.sourceLanguageGroup.checkedRadioButtonId) {
            R.id.sourceEnglish -> TranslateLanguage.ENGLISH
            R.id.sourceSpanish -> TranslateLanguage.SPANISH
            R.id.sourceGerman -> TranslateLanguage.GERMAN
            else -> null
        }
    }

    private fun getTargetLanguage(): String? {
        return when (binding.targetLanguageGroup.checkedRadioButtonId) {
            R.id.targetEnglish -> TranslateLanguage.ENGLISH
            R.id.targetSpanish -> TranslateLanguage.SPANISH
            R.id.targetGerman -> TranslateLanguage.GERMAN
            else -> null
        }
    }
}
