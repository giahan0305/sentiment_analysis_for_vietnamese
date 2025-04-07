package com.example.sentimentApplication

import org.json.JSONObject

object SentimentAnalyzer {
    fun extractSentiment(responseText: String): String {
        return try {
            val jsonResponse = JSONObject(responseText)
            val candidates = jsonResponse.getJSONArray("candidates")
            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.getJSONObject("content")
            val parts = content.getJSONArray("parts")
            parts.getJSONObject(0).getString("text").trim()
        } catch (e: Exception) {
            "Parsing error: ${e.message}"
        }
    }
    fun extractSentimentPhoBert(responseText: String): String {
        return try {
            val jsonResponse = JSONObject(responseText)
            val prediction = jsonResponse.getString("prediction")
            prediction
        } catch (e: Exception) {
            "Parsing error: ${e.message}"
        }
    }
}
