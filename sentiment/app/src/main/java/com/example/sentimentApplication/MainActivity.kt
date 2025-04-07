package com.example.sentimentApplication

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sentimentApplication.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var inputText: EditText
    private lateinit var submitButton: Button
    private lateinit var emotionIcon: ImageView
    private lateinit var mainLayout: ConstraintLayout
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ánh xạ view
        inputText = findViewById(R.id.input_text)
        submitButton = findViewById(R.id.submit_button)
        emotionIcon = findViewById(R.id.emotion_icon)
        mainLayout = findViewById(R.id.main)
        textView = findViewById(R.id.textView)

        // Áp dụng padding để tránh notch (optional)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        submitButton.setOnClickListener {
            val userInput = inputText.text.toString().trim()
            if (userInput.isNotEmpty()) {
                analyzeSentiment(userInput)
            } else {
                Toast.makeText(this, "Vui lòng nhập văn bản!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun analyzeSentiment(text: String) {

        CoroutineScope(Dispatchers.IO).launch {
            val responseText = SentimentApiService.callSentimentPhoBERTModel(text)
            val sentiment = SentimentAnalyzer.extractSentimentPhoBert(responseText)

            withContext(Dispatchers.Main) {
                updateUI(sentiment)
            }
        }
    }

    private fun updateUI(response: String?) {
        textView.setTextColor(ContextCompat.getColor(this, android.R.color.white))

        when {
            response.isNullOrEmpty() -> {
                mainLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                emotionIcon.visibility = View.GONE
            }
            response.contains("POS", ignoreCase = true) -> {
                mainLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
                emotionIcon.setImageResource(R.drawable.ic_happy)
                emotionIcon.visibility = View.VISIBLE
            }
            response.contains("NEG", ignoreCase = true) -> {
                mainLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
                emotionIcon.setImageResource(R.drawable.ic_sad)
                emotionIcon.visibility = View.VISIBLE
            }
            else  -> {
                mainLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                emotionIcon.setImageResource(R.drawable.ic_normal)
                emotionIcon.visibility = View.VISIBLE
            }

        }
    }
}
