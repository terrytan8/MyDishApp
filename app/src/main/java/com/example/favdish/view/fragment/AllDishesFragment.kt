package com.example.favdish.view.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.favdish.R
import com.example.favdish.application.FavDishApplication
import com.example.favdish.databinding.FragmentAllDishesBinding
import com.example.favdish.model.entities.FavDish
import com.example.favdish.view.activities.AddUpdateDishActivity
import com.example.favdish.view.activities.MainActivity
import com.example.favdish.view.adapters.FavDishAdapter
import com.example.favdish.viewmodel.AllDishesViewModel
import com.example.favdish.viewmodel.FavDishViewModel
import com.example.favdish.viewmodel.FavDishViewModelFactory

class AllDishesFragment : Fragment() {

  private lateinit var mBinding:FragmentAllDishesBinding

  private val mFavDishViewModel: FavDishViewModel by viewModels {
    FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
  }

private var _binding: FragmentAllDishesBinding? = null


  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Option menu on top will show
    setHasOptionsMenu(true)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    mBinding= FragmentAllDishesBinding.inflate(inflater,container,false)

    return mBinding.root
  }
 // When the UI create
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

   mBinding.rvDishesList.layoutManager = GridLayoutManager(requireActivity(),2)

   val favDishAdapter = FavDishAdapter(this)
   mBinding.rvDishesList.adapter = favDishAdapter


    mFavDishViewModel.allDishList.observe(viewLifecycleOwner){
      dishes->
        dishes.let {
          if(it.isNotEmpty())
          {
            mBinding.rvDishesList.visibility= View.VISIBLE
            mBinding.tvNoDishesAddedYet.visibility = View.GONE
            favDishAdapter.dishesList(it)

          }else{
            mBinding.rvDishesList.visibility= View.GONE
            mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
          }
        }

    }
  }

    //When click item
    fun dishDetails(favDish:FavDish){
        findNavController().navigate(AllDishesFragmentDirections.actionNavigationAllDishToDishDetailsFragment(favDish))

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
//Setting up the option menu on top right
  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_all_dishes,menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when(item.itemId){
      R.id.action_add_dish->{
        startActivity(Intent(requireActivity(),AddUpdateDishActivity::class.java))
        return true
      }
    }
    return super.onOptionsItemSelected(item)
  }

override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}