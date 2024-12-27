package com.example.spaceapp_quizapi

import GeminiData
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.provider.CalendarContract.Colors
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.spaceapp_quizapi.model.Apod_data
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
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
    lateinit var  Gemini_Data: GeminiData;
    lateinit var QuizQuestion : TextView;
    lateinit var info_text : TextView;
    lateinit var btn_A: Button;
    lateinit var btn_B: Button;
    lateinit var btn_C: Button;
    lateinit var btn_D: Button;

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

        QuizQuestion = view.findViewById<TextView>(R.id.Question);
        btn_A = view.findViewById<Button>(R.id.btn_A);
        btn_B = view.findViewById<Button>(R.id.btn_B);
        btn_C = view.findViewById<Button>(R.id.btn_C);
        btn_D = view.findViewById<Button>(R.id.btn_D);


        var btn_NewQuiz = view.findViewById<Button>(R.id.btn_NewQuiz)
        info_text = view.findViewById<Button>(R.id.Info_text)



        btn_NewQuiz.setOnClickListener{
            reset_btn_colors();
            QuizQuestion.text = "-";
            info_text.text = "Loading...";
            show_options(false)

            GlobalScope.launch {
                Gemini_Data = AskGemeni();
                withContext(Dispatchers.Main){
                    show_options(true)

                    info_text.text = "";
                    QuizQuestion.text = Gemini_Data.question;
                    btn_A.text = Gemini_Data.A;
                    btn_B.text = Gemini_Data.B;
                    btn_C.text = Gemini_Data.C;
                    btn_D.text = Gemini_Data.D;
                    EnableOptions(true)
                }
            }
        }


        btn_A.setOnClickListener{
            checkAnswer(0)
        }
        btn_B.setOnClickListener{
            checkAnswer(1)
        }
        btn_C.setOnClickListener{
            checkAnswer(2)
        }
        btn_D.setOnClickListener{
            checkAnswer(3)
        }




    }

    private fun checkAnswer(selected: Int) {

        if (selected == Gemini_Data.answer){
            correct_asnwer(selected)
        }
        else{
            wrong_asnwer(selected)
            correct_asnwer(Gemini_Data.answer)
        }
        EnableOptions(false);
       // show_options(false);
        info_text.text = Gemini_Data.explanation;
    }

    private fun wrong_asnwer(selected: Int) {
        val wrong_answer = ContextCompat.getColor(requireContext(), R.color.wrong_answer)
        when(selected){
            0 -> btn_A.setBackgroundColor(wrong_answer)
            1 -> btn_B.setBackgroundColor(wrong_answer)
            2 -> btn_C.setBackgroundColor(wrong_answer)
            3 -> btn_D.setBackgroundColor(wrong_answer)
        }
    }

    private fun correct_asnwer(selected: Int) {
        val correct_answer = ContextCompat.getColor(requireContext(), R.color.correct_answer)
        when(selected){
            0 -> btn_A.setBackgroundColor(correct_answer)
            1 -> btn_B.setBackgroundColor(correct_answer)
            2 -> btn_C.setBackgroundColor(correct_answer)
            3 -> btn_D.setBackgroundColor(correct_answer)
        }
    }

    private fun reset_btn_colors(){
        val standart_color = ContextCompat.getColor(requireContext(), R.color.button_background)
        btn_A.setBackgroundColor(standart_color)
        btn_B.setBackgroundColor(standart_color)
        btn_C.setBackgroundColor(standart_color)
        btn_D.setBackgroundColor(standart_color)

    }
    private fun EnableOptions(enable : Boolean){
        btn_A.isEnabled = enable
        btn_B.isEnabled = enable
        btn_C.isEnabled = enable
        btn_D.isEnabled = enable
    }

    private fun show_options(show : Boolean){
        if (show){
            btn_A.visibility = View.VISIBLE;
            btn_B.visibility = View.VISIBLE;
            btn_C.visibility = View.VISIBLE;
            btn_D.visibility = View.VISIBLE;
        }
        else{
            btn_A.visibility = View.INVISIBLE;
            btn_B.visibility = View.INVISIBLE;
            btn_C.visibility = View.INVISIBLE;
            btn_D.visibility = View.INVISIBLE;
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
  "answer": 2,
  "explanation": "Earth is the 3rd planet after Mercury and Venus"
}

Generate a JSON object where:
1. The question and answers are strictly about Astronomy and Space-related topics.
2. The generated content is unique, diverse, and not repetitive across different prompts.
3. Avoid repeating concepts like "the 3rd planet from the sun" or "largest moon" multiple times. Explore topics such as galaxies, black holes, space missions, stars, and cosmology.
4. Use creativity to make the questions interesting and educational.

Do NOT include any additional text, headers, or formatting outside the JSON structure.
""".trimIndent()



        return try {
            val response: GenerateContentResponse = generativeModel.generateContent(prompt)
            val json = Json { ignoreUnknownKeys = true }

            val jsonString = response.text
            Log.d("Gemini", "Response: $jsonString") // Log the response


            json.decodeFromString<GeminiData>(jsonString.toString())
        } catch (e: Exception) {
            Log.e("Gemini", "Error parsing JSON: ${e.message}")
            GeminiData(" Error! No internet ", "A", "B","C", "D", 0, "");
        }

    }
}