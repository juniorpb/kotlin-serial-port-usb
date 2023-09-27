package com.example.myapplication.client

import com.example.myapplication.dto.CreateAnimalDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface IntelicampoClient {

    @POST("animal")
    fun createAnimal(@Body createAnimalDTO: CreateAnimalDTO): CreateAnimalDTO
}