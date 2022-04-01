package com.example.favdish.utils

object Constants {
    const val DISH_TYPE :String = "DishType"
    const val DISH_CATEGORY:String = "DishCategory"
    const val DISH_COOKING_TIME:String = "DishCookingTime"
    const val DISH_IMAGE_SOURCE_LOCAL:String = "Local"
    const val DISH_IMAGE_SOURCE_ONLINE:String ="Online"
    const val ALL_ITEMS:String = "All"
    const val FILTER_SELECTION:String = "FilterSelection"

    const val EXTRA_DISH_DETAILS:String = "DishDetails"
    //FOR API CREATE LIKE A COMPLETE LINK
    const val API_ENDPOINT:String = "recipes/random"
    const val API_KEY:String = "apiKey"
    const val LIMIT_LICENSE:String = "limitLicense"
    const val TAGS:String = "tags"
    const val NUMBER:String = "number"
    const val BASE_URL = "https://api.spoonacular.com/"
    const val API_KEY_VALUE = "YOUR_API_KEY_VALUE"
    const val LIMIT_LICENSE_VALUE :Boolean = true
    const val TAGS_VALUE:String = "vegetarian, dessert"
    const val NUMBER_VALUE:Int = 1
    const val NOTIFICATION_ID = "FavDish_notification_id"
    const val NOTIFICATION_NAME = "FavDish"
    const val NOTIFICATION_CHANNEL = "FavDish_channel_01"

    fun dishTypes():ArrayList<String>{
        val list = ArrayList<String>()
        list.add("Breakfast")
        list.add("Lunch")
        list.add("Snacks")
        list.add("Dinner")
        list.add("Salad")
        list.add("Side Dish")
        list.add("Dessert")
        list.add("Other")

        return list
    }

    fun dishCategory():ArrayList<String>{
        val list = ArrayList<String>()

        list.add("Pizza")
        list.add("BBQ")
        list.add("Bakery")
        list.add("Burger")
        list.add("Cafe")
        list.add("Chicken")
        list.add("Dessert")
        list.add("Drinks")
        list.add("Hot Dogs")
        list.add("Juices")
        list.add("Sandwich")
        list.add("Tea & Coffee")
        list.add("Wraps")
        list.add("Other")

        return list
    }

    fun cookingTime():ArrayList<String>{
        val list = ArrayList<String>()
        list.add("10")
        list.add("15")
        list.add("20")
        list.add("30")
        list.add("45")
        list.add("50")
        list.add("60")
        list.add("90")
        list.add("120")
        list.add("150")
        list.add("180")
        return list
    }
}