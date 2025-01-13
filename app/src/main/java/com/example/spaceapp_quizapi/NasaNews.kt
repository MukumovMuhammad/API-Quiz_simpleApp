package com.example.spaceapp_quizapi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spaceapp_quizapi.model.Apod_data




class NasaNews : Fragment() {
    private lateinit var apod_data: Apod_data
    private lateinit var rv_News: RecyclerView
    private lateinit var rv_adapter: NasaNews_Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            apod_data = it.getParcelable("apodData")!!
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
