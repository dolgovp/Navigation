package com.example.navigation

import android.content.ContentValues.TAG
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val options = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.ENGLISH)
        .setTargetLanguage(TranslateLanguage.RUSSIAN)
        .build()

    val englishRussianTranslator = Translation.getClient(options)
    val conditions = DownloadConditions.Builder()
        .requireWifi()
        .build()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val translator = Translation.getClient(options)
        lifecycle.addObserver(translator)
        val button = findViewById<View>(R.id.button)
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.img_3);
        val image = InputImage.fromBitmap(bitmap, 0)


        recognizer.process(image)
            .addOnSuccessListener { text ->
                englishRussianTranslator.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener {
                        englishRussianTranslator.translate(text.text)
                            .addOnSuccessListener { translatedText ->
                                textView2.text = translatedText
                            }
                            .addOnFailureListener { exception ->
                                // Error.
                                // ...
                            }
                    }
                    .addOnFailureListener { exception ->
                        // Model couldn’t be downloaded or other internal error.
                        // ...
                    }
            }
            .addOnFailureListener { exception ->
                // Task failed with an exception
                Log.e(TAG, "Text recognition error", exception)
            }
    }


}
