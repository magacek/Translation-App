package com.example.translationapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.appthattranslates.databinding.FragmentTranslationBinding
import androidx.lifecycle.ViewModelProvider
import androidx.core.widget.addTextChangedListener
import com.example.appthattranslates.MainActivity
import com.example.appthattranslates.TranslationViewModel

/**
 * A fragment representing the translation input screen.
 *
 * This fragment provides an input field where users can type text for translation.
 * User input changes are immediately reflected in a shared ViewModel to be observed
 * and acted upon by other components, e.g., the parent activity.
 *
 * @author Matt Gacek
 */

class TranslationFragment : Fragment() {

    private var _binding: FragmentTranslationBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: TranslationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTranslationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(TranslationViewModel::class.java)

        binding.editTextInput.addTextChangedListener {
            viewModel.userInput.value = it.toString()
            (activity as MainActivity).handleTextChange()
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
