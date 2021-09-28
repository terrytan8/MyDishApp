package com.example.favdish.view.adapters

import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.favdish.R
import com.example.favdish.databinding.ItemDishesLayoutBinding
import com.example.favdish.model.entities.FavDish
import com.example.favdish.utils.Constants
import com.example.favdish.view.activities.AddUpdateDishActivity
import com.example.favdish.view.fragment.AllDishesFragment
import com.example.favdish.view.fragment.FavouriteDishesFragment
import java.lang.Exception

class FavDishAdapter(private val fragment:Fragment):RecyclerView.Adapter<FavDishAdapter.ViewHolder>() {

    private var dishes:List<FavDish> = listOf()

    class ViewHolder(view:ItemDishesLayoutBinding):RecyclerView.ViewHolder(view.root){

        val ivDishImage = view.ivDishImage
        val tvTitle = view.tvDishTitle
        //EDIT DELETE
        val ibMore = view.ibMore
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val binding:ItemDishesLayoutBinding = ItemDishesLayoutBinding.inflate(
           LayoutInflater.from(fragment.context),parent,false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
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
        holder.ibMore.setOnClickListener{
            val popup = PopupMenu(fragment.context,holder.ibMore)
            popup.menuInflater.inflate(R.menu.menu_adapter,popup.menu)

            popup.setOnMenuItemClickListener {
                if(it.itemId== R.id.action_edit_dish){
                    //FROM POP UP TO ACTIVITY
                   val intent = Intent(fragment.requireActivity(),AddUpdateDishActivity::class.java)
                    intent.putExtra(Constants.EXTRA_DISH_DETAILS,dish)
                    fragment.requireActivity().startActivity(intent)

                }else if(it.itemId == R.id.action_delete_dish){
                    Log.i("TAG","CLICK DELETE")
                }
                true
            }
            //SHOW POP UP ICON
            try{
                val showIcon =PopupMenu::class.java.getDeclaredField("mPopup")
                showIcon.isAccessible = true
                val menu = showIcon.get(popup)
                menu.javaClass
                    .getDeclaredMethod("setForceShowIcon",Boolean::class.java)
                    .invoke(menu,true)

            }catch (e:Exception){
                e.printStackTrace()
            }
            popup.show()
        }
        if(fragment is AllDishesFragment){
            holder.ibMore.visibility = View.VISIBLE
        }else if (fragment is FavouriteDishesFragment){
            holder.ibMore.visibility = View.GONE
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