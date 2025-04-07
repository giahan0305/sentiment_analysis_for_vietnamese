package com.example.sentimentApplication


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object SentimentApiService {
    suspend fun callSentimentApi(text: String, apiKey: String): String {
        return withContext(Dispatchers.IO) {
            val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey")
            val connection = url.openConnection() as HttpURLConnection

            try {
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json")

                val jsonBody = buildJsonBody(text)
                connection.outputStream.use { it.write(jsonBody.toString().toByteArray()) }

                if (connection.responseCode == 200) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    "Error: ${connection.responseCode} - ${connection.errorStream?.bufferedReader()?.readText()}"
                }
            } catch (e: Exception) {
                "Exception: ${e.message}"
            } finally {
                connection.disconnect()
            }
        }
    }


    suspend fun callSentimentPhoBERTModel(text: String): String {
        return withContext(Dispatchers.IO) {
            val url = URL("http://10.45.128.229:8000/predict")
            val connection = url.openConnection() as HttpURLConnection

            try {
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json")

                val jsonBody = buildJsonBodyPhoBert(text)
                connection.outputStream.use { it.write(jsonBody.toString().toByteArray()) }

                if (connection.responseCode == 200) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    "Error: ${connection.responseCode} - ${connection.errorStream?.bufferedReader()?.readText()}"
                }
            } catch (e: Exception) {
                "Exception: ${e.message}"
            } finally {
                connection.disconnect()
            }
        }
    }

    private fun buildJsonBody(text: String): JSONObject {
        return JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", "Analyze the sentiment of this sentence: \"$text\". Return only 'positive' or 'negative'.")
                        })
                    })
                })
            })
        }
    }
    private fun buildJsonBodyPhoBert(text: String): JSONObject {
        return JSONObject().apply {
            put("sentence", text) // Truyền trực tiếp văn bản vào trường sentence
        }
    }
}
