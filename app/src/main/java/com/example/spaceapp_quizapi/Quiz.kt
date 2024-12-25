package com.example.spaceapp_quizapi

import GeminiData
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.spaceapp_quizapi.model.Apod_data
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Quiz.newInstance] factory method to
 * create an instance of this fragment.
 */
class Quiz : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz, container, false)

        val QuizQuestion = requireView()!!.findViewById<TextView>(R.id.Question);

        QuizQuestion.text = "This Text is just created!";




    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var QuizQuestion = view.findViewById<TextView>(R.id.Question);
        var btn_A = view.findViewById<TextView>(R.id.btn_A);
        var btn_B = view.findViewById<TextView>(R.id.btn_B);
        var btn_C = view.findViewById<TextView>(R.id.btn_C);
        var btn_D = view.findViewById<TextView>(R.id.btn_D);


        var Gemini_Data: GeminiData;
        GlobalScope.launch {
            Gemini_Data = AskGemeni();
            withContext(Dispatchers.Main){
                QuizQuestion.text = Gemini_Data.question;
                btn_A.text = Gemini_Data.A;
                btn_B.text = Gemini_Data.B;
                btn_C.text = Gemini_Data.C;
                btn_D.text = Gemini_Data.D;
            }
        }


    }


    suspend fun AskGemeni(): GeminiData {
        delay(1000);
        val generativeModel = GenerativeModel(modelName = "gemini-1.5-flash", apiKey = "AIzaSyDAmq-30rzxXD6n1eoQAGd9OiLVrdyW8rQ")


        val prompt = """
           Respond ONLY with a JSON object that starts and ends with "{}". 

Example:
{
  "question": "The 3rd planet from the sun is?",
  "A": "Mercury",
  "B": "Venus",
  "C": "Earth",
  "D": "Mars",
  "answer": "C"
}

Generate a JSON object where the question and answers are strictly about Astronomy and Space-related topics. Do NOT include any additional text, formatting, or explanations.


        """.trimIndent()


        return try {
            val response: GenerateContentResponse = generativeModel.generateContent(prompt)
            val json = Json { ignoreUnknownKeys = true }

            val jsonString = response.text
            Log.d("Gemini", "Response: $jsonString") // Log the response


            json.decodeFromString<GeminiData>(jsonString.toString())
        } catch (e: Exception) {
            Log.e("Gemini", "Error parsing JSON: ${e.message}")
            return GeminiData("No internet ", "A", "B","C", "D", "A");
        }

    }
}