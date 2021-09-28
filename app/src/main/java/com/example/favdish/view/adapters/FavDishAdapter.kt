package com.example.favdish.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.favdish.databinding.ItemDishesLayoutBinding
import com.example.favdish.model.entities.FavDish
import com.example.favdish.view.fragment.AllDishesFragment
import com.example.favdish.view.fragment.FavouriteDishesFragment

class FavDishAdapter(private val fragment:Fragment):RecyclerView.Adapter<FavDishAdapter.ViewHolder>() {

    private var dishes:List<FavDish> = listOf()

    class ViewHolder(view:ItemDishesLayoutBinding):RecyclerView.ViewHolder(view.root){

        val ivDishImage = view.ivDishImage
        val tvTitle = view.tvDishTitle
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val binding:ItemDishesLayoutBinding = ItemDishesLayoutBinding.inflate(
           LayoutInflater.from(fragment.context),parent,false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dish = dishes[position]

        Glide.with(fragment)
            .load(dish.image)
            .into(holder.ivDishImage)

        holder.tvTitle.text = dish.title

        holder.itemView.setOnClickListener{
            if(fragment is AllDishesFragment){
                fragment.dishDetails(dish)
            }
            if(fragment is FavouriteDishesFragment){
                fragment.dishDetails(dish)
            }
        }
    }

    override fun getItemCount(): Int {
        return dishes.size
    }

    fun dishesList(list:List<FavDish>){
        dishes = list
        notifyDataSetChanged()
    }

}