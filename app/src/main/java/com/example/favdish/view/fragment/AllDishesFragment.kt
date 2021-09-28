package com.example.favdish.view.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.favdish.R
import com.example.favdish.application.FavDishApplication
import com.example.favdish.databinding.DialogCustomListBinding
import com.example.favdish.databinding.FragmentAllDishesBinding
import com.example.favdish.model.entities.FavDish
import com.example.favdish.utils.Constants
import com.example.favdish.view.activities.AddUpdateDishActivity
import com.example.favdish.view.activities.MainActivity
import com.example.favdish.view.adapters.CustomListItemAdapter
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

    fun deleteDish(dish: FavDish){
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(resources.getString(R.string.title_delete_dish))
        builder.setMessage(resources.getString(R.string.msg_delete_dish_dialog,dish.title))
        builder.setIcon(R.drawable.ic_delete)
        builder.setPositiveButton(resources.getString(R.string.lbl_yes)){
            dialogInterface,_->
            mFavDishViewModel.delete(dish)
            dialogInterface.dismiss()
            Toast.makeText(requireActivity(),"Dish detail delete successfully",
                Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton(resources.getString(R.string.lbl_no)){
                dialogInterface,_->
            dialogInterface.dismiss()
        }
        val alertDialog:AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun filterDishesListDialog(){
        val customListDialog= Dialog(requireActivity())
        val binding:DialogCustomListBinding= DialogCustomListBinding.inflate(layoutInflater)

        customListDialog.setContentView(binding.root)
        binding.tvTitle.text = resources.getString(R.string.title_select_item_to_filter)

        val dishTypes = Constants.dishTypes()
        dishTypes.add(0,Constants.ALL_ITEMS)
        binding.rvList.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = CustomListItemAdapter(requireActivity(),dishTypes,Constants.FILTER_SELECTION)
        binding.rvList.adapter = adapter
        customListDialog.show()
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
        R.id.action_filter_dishes->{
            filterDishesListDialog()
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