package com.example.spaceapp_quizapi

import GeminiData
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class Quiz : Fragment() {
    lateinit var  Gemini_Data: GeminiData;
    lateinit var QuizQuestion : TextView;
    lateinit var info_text : TextView;
    lateinit var btn_A: Button;
    lateinit var btn_B: Button;
    lateinit var btn_C: Button;
    lateinit var btn_D: Button;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_quiz, container, false)

        val QuizQuestion = requireView()!!.findViewById<TextView>(R.id.question_text);

        QuizQuestion.text = "This Text is just created!";



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        QuizQuestion = view.findViewById<TextView>(R.id.question_text);
        btn_A = view.findViewById<Button>(R.id.btn_A);
        btn_B = view.findViewById<Button>(R.id.btn_B);
        btn_C = view.findViewById<Button>(R.id.btn_C);
        btn_D = view.findViewById<Button>(R.id.btn_D);


        var btn_NewQuiz = view.findViewById<Button>(R.id.btn_new_quiz)
        info_text = view.findViewById<Button>(R.id.info_text)
        reset_btn_colors();
        val spinner: Spinner  = view.findViewById(R.id.lang_spinner)
        var  selectedLang = "English";

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.languages,
            android.R.layout.simple_spinner_item,

        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }


        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedLang = resources.getStringArray(R.array.languages)[p2]
                Toast.makeText(requireContext(), selectedLang, Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        btn_NewQuiz.setOnClickListener{
            reset_btn_colors();
            QuizQuestion.text = "-";
            info_text.text = "Loading...";
            show_options(false)
            GlobalScope.launch {
                Gemini_Data = AskGemeni(selectedLang);
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

    suspend fun AskGemeni(selectedLang: String): GeminiData {
        delay(1000);
        val generativeModel = GenerativeModel(modelName = "gemini-1.5-flash", apiKey = "AIzaSyAkNt2DITi1T3Ofurp7yWNjwFVF1I4k58o")


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
5. The questions and answers in ${selectedLang} but keys must stay in english
Do NOT include any additional text, headers, or formatting outside the JSON structure.
""".trimIndent()

        Log.i("Gemini", "Making request to Gemini...")

        return try {
            val response: GenerateContentResponse = generativeModel.generateContent(prompt)
            val json = Json { ignoreUnknownKeys = true }

            val jsonString = response.text
            Log.d("Gemini", "Response: $jsonString") // Log the response


            json.decodeFromString<GeminiData>(jsonString.toString())
        } catch (e: Exception) {
            Log.e("Gemini", "Error:  ${e.message}")
            GeminiData(" Error! No internet ", "A", "B","C", "D", 0, "");
        }

    }
}