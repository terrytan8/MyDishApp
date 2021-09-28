package com.example.favdish.viewmodel

import androidx.lifecycle.*
import com.example.favdish.model.database.FavDishRepository
import com.example.favdish.model.entities.FavDish
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

/**
 * The ViewModel's role is to provide data to the UI and survive configuration changes.
 * A ViewModel acts as a communication center between the Repository and the UI.
 * You can also use a ViewModel to share data between fragments.
 * The ViewModel is part of the lifecycle library.
 *
 * @param repository - The repository class is
 */
class FavDishViewModel(private val repository: FavDishRepository) : ViewModel() {

    /**
     * Launching a new coroutine to insert the data in a non-blocking way.
     */
    fun insert(dish: FavDish) = viewModelScope.launch {
        // Call the repository function and pass the details.
        repository.insertFavDishData(dish)
    }

    val allDishList: LiveData<List<FavDish>> = repository.allDishesList.asLiveData()

    fun update(dish: FavDish) = viewModelScope.launch {
        repository.updateFavDishData(dish)
    }

    val favouriteDishes: LiveData<List<FavDish>> = repository.favouriteDishList.asLiveData()

    fun delete(dish: FavDish) = viewModelScope.launch {
        repository.deleteFavDishData(dish)
    }

    fun getFilteredList(value: String): LiveData<List<FavDish>> =
        repository.filteredDistList(value).asLiveData()

}

    /**
     * To create the ViewModel we implement a ViewModelProvider.Factory that gets as a parameter the dependencies
     * needed to create FavDishViewModel: the FavDishRepository.
     * By using viewModels and ViewModelProvider.Factory then the framework will take care of the lifecycle of the ViewModel.
     * It will survive configuration changes and even if the Activity is recreated,
     * you'll always get the right instance of the FavDishViewModel class.
     */
    class FavDishViewModelFactory(private val repository: FavDishRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FavDishViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FavDishViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

