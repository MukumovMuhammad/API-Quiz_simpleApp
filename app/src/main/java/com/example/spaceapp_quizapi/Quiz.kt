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
    var sl_lang : Int = 0;

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

        QuizQuestion = view.findViewById(R.id.question_text);
        btn_A = view.findViewById(R.id.btn_A);
        btn_B = view.findViewById(R.id.btn_B);
        btn_C = view.findViewById(R.id.btn_C);
        btn_D = view.findViewById(R.id.btn_D);

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
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            spinner.adapter = adapter
        }


        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                (p0?.getChildAt(0) as? TextView)?.setTextColor( // Corrected line
                    ContextCompat.getColor(requireContext(), R.color.white)
                )

                sl_lang = p2;

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
        val WrongAnswer = ContextCompat.getDrawable(requireContext(), R.drawable.wrong_answer_bg)
        when(selected){
            0 -> btn_A.background = WrongAnswer;
            1 -> btn_B.background = WrongAnswer;
            2 -> btn_C.background = WrongAnswer;
            3 -> btn_D.background = WrongAnswer;
        }
    }

    private fun correct_asnwer(selected: Int) {
        val CorrectAnswer = ContextCompat.getDrawable(requireContext(), R.drawable.correct_answer)
        when(selected){
            0 -> btn_A.background = CorrectAnswer;
            1 -> btn_B.background = CorrectAnswer;
            2 -> btn_C.background = CorrectAnswer;
            3 -> btn_D.background = CorrectAnswer;
        }
    }

    private fun reset_btn_colors(){
        val buttonBackgroundDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.button_background)
        btn_A.background = buttonBackgroundDrawable;
        btn_B.background = buttonBackgroundDrawable;
        btn_C.background = buttonBackgroundDrawable;
        btn_D.background = buttonBackgroundDrawable;

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
           Respond ONLY with a JSON object that starts and ends with "{}" no ``` in response!. 

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

            when(sl_lang){
                0 -> GeminiData("Something went wrong, try again", "A", "B","C", "D", 0, "");
                1 -> GeminiData("문제가 발생했습니다, 다시 시도해주세요", "A", "B","C", "D", 0, "");
                2 -> GeminiData("Что-то пошло не так, попробуйте снова", "A", "B","C", "D", 0, "");
                3 -> GeminiData("Ein Fehler ist aufgetreten, versuchen Sie es erneut", "A", "B","C", "D", 0, "");
                4 -> GeminiData("Ha ocurrido un error, inténtalo de nuevo", "A", "B","C", "D", 0, "");
                else -> GeminiData("Something went wrong, try again", "A", "B","C", "D", 0, "");
            }

        }

    }
}