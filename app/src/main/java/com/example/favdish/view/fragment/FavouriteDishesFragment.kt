package com.example.favdish.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.favdish.application.FavDishApplication
import com.example.favdish.databinding.FragmentFavouriteDishesBinding
import com.example.favdish.model.entities.FavDish
import com.example.favdish.view.activities.MainActivity
import com.example.favdish.view.adapters.FavDishAdapter
import com.example.favdish.viewmodel.DashboardViewModel
import com.example.favdish.viewmodel.FavDishViewModel
import com.example.favdish.viewmodel.FavDishViewModelFactory

class FavouriteDishesFragment : Fragment() {

  private lateinit var dashboardViewModel: DashboardViewModel
  private val mFavDishModel: FavDishViewModel by viewModels {
    FavDishViewModelFactory(((requireActivity().application) as FavDishApplication).repository)
  }
  private var mBinding: FragmentFavouriteDishesBinding? = null

  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = mBinding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    mBinding = FragmentFavouriteDishesBinding.inflate(inflater, container, false)

    return mBinding!!.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    mFavDishModel.favouriteDishes.observe(viewLifecycleOwner) { dishes ->
      dishes.let {

        mBinding!!.rvFavoriteDishesList.layoutManager =
          GridLayoutManager(requireActivity(), 2)

        val adapter = FavDishAdapter(this)
        mBinding!!.rvFavoriteDishesList.adapter = adapter


        if (it.isNotEmpty()) {
          mBinding!!.rvFavoriteDishesList.visibility = View.VISIBLE
          mBinding!!.tvNoFavouriteDishesAvailable.visibility = View.GONE
          adapter.dishesList(it)

        } else {
          mBinding!!.rvFavoriteDishesList.visibility = View.GONE
          mBinding!!.tvNoFavouriteDishesAvailable.visibility = View.VISIBLE
        }
      }
    }
  }

  fun dishDetails(favDish: FavDish){
    findNavController().navigate(
      FavouriteDishesFragmentDirections.
      actionNavigationFavouriteDishesToDishDetailsFragment(favDish))

    if(requireActivity() is MainActivity){
      (activity as MainActivity?)?.hideBottomNavigationView()
    }
  }

  override fun onResume() {
    super.onResume()
    if(requireActivity() is MainActivity){
      (activity as MainActivity?)?.showBottomNavigationView()
    }
  }

    override fun onDestroyView() {
      super.onDestroyView()
      mBinding = null
    }

}
