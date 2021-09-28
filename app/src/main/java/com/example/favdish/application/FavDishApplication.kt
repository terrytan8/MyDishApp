package com.example.favdish.application

import android.app.Application
import com.example.favdish.model.database.FavDishRepository
import com.example.favdish.model.database.FavDishRoomDatabase

class FavDishApplication:Application() {
    //Create when needed
    private val database by lazy { FavDishRoomDatabase.getDatabase(this@FavDishApplication) }

    // A variable for repository.
    val repository by lazy { FavDishRepository(database.favDishDao()) }
}