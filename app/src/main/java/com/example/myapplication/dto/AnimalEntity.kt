package com.example.myapplication.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "animal")
data class AnimalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rfid: String? = null,
    val tattoo: String?,
    val race: String?,
    val sex: String?,
    val type: String? = null,
    val currentDateTime: String? = null
)