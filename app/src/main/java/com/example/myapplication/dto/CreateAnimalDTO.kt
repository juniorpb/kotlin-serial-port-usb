package com.example.myapplication.dto

import java.util.UUID

data class CreateAnimalDTO(
    val tattoo: String?,
    val rfid: String = UUID.randomUUID().toString(),
    val race: String = "Nelore",
    val sex: String = "MALE",
    val picketId: Int = 27
)
