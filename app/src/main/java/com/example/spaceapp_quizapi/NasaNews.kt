package com.example.spaceapp_quizapi

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spaceapp_quizapi.model.Apod_data
import io.ktor.client.HttpClient


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NasaNews.newInstance] factory method to
 * create an instance of this fragment.
 */
class NasaNews : Fragment() {
    private lateinit var apod_data: Apod_data
    private var param2: String? = null
    private lateinit var rv_News: RecyclerView
    private lateinit var rv_adapter: NasaNews_Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            apod_data = it.getParcelable("apod_data")!!
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nasa_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize RecyclerView and its adapter
        rv_News = view.findViewById(R.id.rv_news)
        rv_News.layoutManager = LinearLayoutManager(requireContext())
        rv_adapter = NasaNews_Adapter(apod_data)
        rv_News.adapter = rv_adapter
    }
}
