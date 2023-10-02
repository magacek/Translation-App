package com.example.appthattranslates


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel for holding and managing translation-related data.
 *
 * This ViewModel contains live data for both the user's input and the translated text.
 * It serves as the primary data holder and communicator between the activity and its fragments.
 * LiveData is used to ensure data changes are observed and UI components can react accordingly.
 *
 * @author Matt Gacek
 */

class TranslationViewModel : ViewModel() {
    val userInput: MutableLiveData<String> = MutableLiveData("")
    val translatedText: MutableLiveData<String> = MutableLiveData("")
}
