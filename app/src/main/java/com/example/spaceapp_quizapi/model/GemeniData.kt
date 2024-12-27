import kotlinx.serialization.Serializable

@Serializable
data class GeminiData(
    val question: String,
    val A: String,
    val B: String,
    val C: String,
    val D: String,
    val answer: Int,
    val explanation: String
)
