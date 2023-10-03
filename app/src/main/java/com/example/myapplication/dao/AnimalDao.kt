package com.example.myapplication.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.myapplication.dto.AnimalEntity

@Dao
interface AnimalDao {

    @Insert
    fun insertAnimal(animal: AnimalEntity)

    @Query("SELECT * FROM animal")
    fun getAllAnimals(): List<AnimalEntity>
}