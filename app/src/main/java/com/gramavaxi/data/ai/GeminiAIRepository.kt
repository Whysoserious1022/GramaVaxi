package com.gramavaxi.data.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.gramavaxi.domain.model.DiseaseAnalysis
import com.gramavaxi.domain.model.UrgencyLevel
import com.gramavaxi.domain.repository.AIRepository
import javax.inject.Inject
import timber.log.Timber

class GeminiAIRepository @Inject constructor(
    private val model: GenerativeModel
) : AIRepository {

    private var chat = model.startChat()
    private var hasSentSystemContext = false

    private val systemContext = """
        You are Grama-Vaxi AI, an expert assistant for rural livestock farmers in Karnataka, India.
        Your role is to help farmers with:
        1. Livestock disease identification and first aid advice
        2. Vaccination schedules and importance
        3. Animal nutrition and care
        4. When to consult a veterinarian

        Communication rules:
        - Always respond in simple, clear language. Use Kannada if the user writes in Kannada; otherwise use English.
        - Use simple words that rural farmers can understand.
        - Be empathetic and practical.
        - Always recommend consulting a vet for serious symptoms.
        - Structure responses with: Likely Issue -> Action Steps -> Prevention.
        - Add urgency level: [LOW/MEDIUM/HIGH/EMERGENCY].

        IMPORTANT: Never replace professional veterinary advice. Always include a reminder to consult a vet.
    """.trimIndent()

    override suspend fun sendChatMessage(message: String): Result<String> {
        return try {
            val prompt = if (hasSentSystemContext) {
                message
            } else {
                hasSentSystemContext = true
                "$systemContext\n\nUser: $message"
            }
            val response = chat.sendMessage(prompt)
            Result.success(response.text ?: "Sorry, I could not process that. Please try again.")
        } catch (e: Exception) {
            Timber.e(e, "Gemini chat error")
            if (e.message?.contains("API key") == true || e.message?.contains("403") == true || e.message?.contains("leaked") == true || e.message?.contains("permission") == true) {
                Result.success("Demo Mode: I received your message: '$message'.\n\n(Note: Your Gemini API Key in local.properties is revoked/leaked. Please generate a new one at aistudio.google.com to enable real AI responses.)")
            } else {
                Result.failure(e)
            }
        }
    }

    override suspend fun analyzeSymptoms(
        symptoms: String,
        species: String,
        ageMonths: Int
    ): Result<DiseaseAnalysis> {
        return try {
            val prompt = """
                Analyze these symptoms for a $species aged $ageMonths months:
                Symptoms: $symptoms

                Respond in this exact JSON format:
                {
                  "likely_diseases": ["Disease1", "Disease2"],
                  "urgency": "HIGH",
                  "immediate_actions": ["Action 1", "Action 2"],
                  "prevention": ["Prevention 1"],
                  "vet_required": true,
                  "summary": "Brief summary in simple language"
                }
            """.trimIndent()

            val response = model.generateContent(prompt)
            val text = response.text ?: throw IllegalStateException("Empty Gemini response")

            val analysis = DiseaseAnalysis(
                likelyDiseases = extractJsonArray(text, "likely_diseases"),
                confidence = 0.75f,
                urgency = extractUrgency(text),
                immediateActions = extractJsonArray(text, "immediate_actions"),
                prevention = extractJsonArray(text, "prevention"),
                vetRequired = text.contains("\"vet_required\": true"),
                rawResponse = text
            )
            Result.success(analysis)
        } catch (e: Exception) {
            Timber.e(e, "Symptom analysis error")
            Result.failure(e)
        }
    }

    override suspend fun analyzeAnimalImage(imageBase64: String, symptoms: String): Result<String> {
        return try {
            val prompt = """
                A farmer has described these symptoms: $symptoms

                Based on the symptoms described, provide:
                1. Possible diseases or conditions
                2. Urgency level
                3. Immediate actions
                4. When to call a vet

                Respond in Kannada if symptoms are in Kannada, otherwise use English.
            """.trimIndent()

            val response = model.generateContent(prompt)
            Result.success(response.text ?: "Could not analyze the symptoms.")
        } catch (e: Exception) {
            Timber.e(e, "Vision analysis error")
            Result.failure(e)
        }
    }

    override suspend fun generateCareTips(species: String, breed: String, season: String): Result<String> {
        return try {
            val prompt = """
                Generate 5 seasonal care tips for a $breed $species in $season season in Karnataka.
                Keep tips simple, practical, and actionable for rural farmers.
                Mention common diseases to watch for in this season.
                Write in both English and Kannada.
            """.trimIndent()

            val response = model.generateContent(prompt)
            Result.success(response.text ?: "")
        } catch (e: Exception) {
            Timber.e(e, "Care tips generation error")
            Result.failure(e)
        }
    }

    override suspend fun getCachedOrFetch(query: String): Result<String> = sendChatMessage(query)

    fun resetChat() {
        chat = model.startChat()
        hasSentSystemContext = false
    }

    private fun extractJsonArray(json: String, key: String): List<String> {
        return try {
            val startIdx = json.indexOf("\"$key\": [") + "\"$key\": [".length
            val endIdx = json.indexOf("]", startIdx)
            val arrayContent = json.substring(startIdx, endIdx)
            arrayContent.split(",")
                .map { it.trim().removeSurrounding("\"") }
                .filter { it.isNotBlank() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun extractUrgency(json: String): UrgencyLevel {
        return when {
            json.contains("\"urgency\": \"EMERGENCY\"") -> UrgencyLevel.EMERGENCY
            json.contains("\"urgency\": \"HIGH\"") -> UrgencyLevel.HIGH
            json.contains("\"urgency\": \"MEDIUM\"") -> UrgencyLevel.MEDIUM
            else -> UrgencyLevel.LOW
        }
    }
}
