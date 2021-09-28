package com.example.favdish.model.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.favdish.model.entities.FavDish as FavDish


@Dao
interface FavDishDao {
        @Insert
       suspend fun insertFavDishDetails(favDish: FavDish)


       @Query("SELECT * FROM FAV_DISHES_TABLE ORDER BY ID")
       fun getAllDishesList(): Flow<List<FavDish>>


       @Update
       suspend fun updateFaveDishDetails(favDish: FavDish)

       @Query("SELECT * FROM FAV_DISHES_TABLE WHERE favourite_dish = 1")
       fun getFavouriteDishesList():Flow<List<FavDish>>

       @Delete
       suspend fun deleteFavDishDetails(favDish: FavDish)

       @Query("SELECT* FROM FAV_DISHES_TABLE WHERE type = :filterType")
       fun getFilteredDishesList(filterType:String):Flow<List<FavDish>>
}