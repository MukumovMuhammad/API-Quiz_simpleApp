package com.example.spaceapp_quizapi.model

import kotlinx.serialization.Serializable

@Serializable
data class Apod_data(
    val title: String,
    val date: String,
    val url: String,
    val explanation: String
)
