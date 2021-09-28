package com.example.favdish.view.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.favdish.R
import com.example.favdish.application.FavDishApplication
import com.example.favdish.databinding.ActivityAddUpdateDishBinding
import com.example.favdish.databinding.DialogCustomImageSelectionBinding
import com.example.favdish.databinding.DialogCustomListBinding
import com.example.favdish.model.database.FavDishRepository
import com.example.favdish.model.entities.FavDish
import com.example.favdish.utils.Constants
import com.example.favdish.view.adapters.CustomListItemAdapter
import com.example.favdish.viewmodel.FavDishViewModel
import com.example.favdish.viewmodel.FavDishViewModelFactory
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

//Implement onclick listener for the image button
class AddUpdateDishActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding:ActivityAddUpdateDishBinding
    private var mImagePath = ""
    private lateinit var mCustomListDialog: Dialog

    private var mFavDishDetails:FavDish? =null

    private val mFavDishViewModel:FavDishViewModel by viewModels{
        FavDishViewModelFactory((application as FavDishApplication).repository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding= ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        //Set click event to to image picture

        if(intent.hasExtra(Constants.EXTRA_DISH_DETAILS)){
            //PASS INFORMATION TO THIS ACTIVITY
            mFavDishDetails = intent.getParcelableExtra(Constants.EXTRA_DISH_DETAILS)
        }
        setupActionBar()

        mFavDishDetails?.let {
            if(it.id!=0){
                mImagePath = it.image
                Glide.with(this)
                    .load(mImagePath)
                    .centerCrop()
                    .into(mBinding.ivDishImage)

                mBinding.etTitle.setText(it.title)
                mBinding.etType.setText(it.type)
                mBinding.etCategory.setText(it.category)
                mBinding.etIngredients.setText(it.ingredients)
                mBinding.etCookingTime.setText(it.cookingTime)
                mBinding.etDirectionToCook.setText(it.directionToCook)
                mBinding.btnAddDish.text = resources.getString(R.string.title_update_dish)

            }
        }



        mBinding.ivAddDishImage.setOnClickListener(this)
        mBinding.etCategory.setOnClickListener(this)
        mBinding.etType.setOnClickListener(this)
        mBinding.etCookingTime.setOnClickListener(this)
        mBinding.btnAddDish.setOnClickListener(this)


    }

    private fun setupActionBar(){
        //如果里面没有detail 那就是 add dish 而不是 edit dish
        setSupportActionBar(mBinding.toolbarAddDishActivity)
        if(mFavDishDetails!=null && mFavDishDetails!!.id !=0){
            supportActionBar?.let {
                it.title = resources.getString(R.string.title_edit_dish)
            }
        }else{
            supportActionBar?.let { {
                it.title = resources.getString(R.string.title_add_dish)
            } }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mBinding.toolbarAddDishActivity.setNavigationOnClickListener{
            onBackPressed()
        }

    }

    override fun onClick(p0: View?) {
        if(p0!=null){
            when(p0.id){
                R.id.iv_add_dish_image->{
                    customImageSelectionDialog()
                    return
                }
                R.id.et_type->{
                    customItemDialog(resources.getString(R.string.title_select_dish_type),
                    Constants.dishTypes(),Constants.DISH_TYPE)

                    return
                }
                R.id.et_category->{
                    customItemDialog(resources.getString(R.string.title_select_dish_category),
                        Constants.dishCategory(),Constants.DISH_CATEGORY)
                    return
                }
                R.id.et_cooking_time->{
                    customItemDialog(resources.getString(R.string.title_select_dish_cooking_time),
                        Constants.cookingTime(),Constants.DISH_COOKING_TIME)

                    return
                }
                R.id.btn_add_dish->{
                    //Clear all the space
                    val title = mBinding.etTitle.text.toString().trim{it<=' '}
                    val type = mBinding.etType.text.toString().trim{it<=' '}
                    val category = mBinding.etCategory.text.toString().trim{it<=' '}
                    val ingredients = mBinding.etIngredients.text.toString().trim{it<=' '}
                    val cookingTime = mBinding.etCookingTime.text.toString().trim{it<=' '}
                    val cookingDirection = mBinding.etDirectionToCook.text.toString().trim{it<=' '}
                    when{

                        TextUtils.isEmpty(mImagePath)->{
                            Toast.makeText(this@AddUpdateDishActivity,resources.getString(R.string.err_msg_select_dish_image),
                            Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(title)->{
                            Toast.makeText(this,resources.getString(R.string.err_msg_enter_dish_title),
                                Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(type)->{
                            Toast.makeText(this,resources.getString(R.string.err_msg_select_dish_type),
                                Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(category)->{
                            Toast.makeText(this,resources.getString(R.string.err_msg_select_dish_category),
                                Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(ingredients)->{
                            Toast.makeText(this,resources.getString(R.string.err_msg_enter_dish_ingredients),
                                Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(cookingTime)->{
                            Toast.makeText(this,resources.getString(R.string.err_msg_select_dish_cooking_time),
                                Toast.LENGTH_SHORT).show()
                        }
                        TextUtils.isEmpty(cookingDirection)->{
                            Toast.makeText(this,resources.getString(R.string.err_msg_enter_dish_cooking_instructions),
                                Toast.LENGTH_SHORT).show()
                        }
                        else->{
                            var dishID = 0
                            var imageSource = Constants.DISH_IMAGE_SOURCE_LOCAL
                            var favouriteDish = false
                            mFavDishDetails?.let {
                                if(it.id!=0){
                                    dishID = it.id
                                    imageSource = it.imageSource
                                    favouriteDish = it.favouriteDish
                                }
                            }
                            val favDishDetails:FavDish = FavDish(
                                mImagePath,
                                imageSource,
                                title,
                                type,
                                category,
                                ingredients,
                                cookingTime,
                                cookingDirection,
                                favouriteDish,
                                dishID

                            )
                            //NEW DISH
                            if(dishID==0){
                                mFavDishViewModel.insert(favDishDetails)
                                Toast.makeText(this,"Dish detail add successfully",
                                    Toast.LENGTH_SHORT).show()
                                Log.e("Insertion", "Success")
                            }else{
                                mFavDishViewModel.update(favDishDetails)
                                Toast.makeText(this,"Dish detail update successfully",
                                    Toast.LENGTH_SHORT).show()
                                Log.e("Updating", "Success")
                            }


                            finish()

                        }

                    }
                    return
                }
            }
        }
    }
    // CALL THE DIALOG
    private fun  customImageSelectionDialog(){
        var dialog = Dialog(this)
        val binding = DialogCustomImageSelectionBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        binding.tvCamera.setOnClickListener{
            Dexter.withContext(this).withPermissions(
                //IMPORT ANDROID. MANIFEST
                Manifest.permission.READ_EXTERNAL_STORAGE,
                //Not available for higher SDK
               // Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ).withListener(object:MultiplePermissionsListener{
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    // IF REPORT NOT EMPTY THEN...
                    report?.let {
                        if(report.areAllPermissionsGranted()){
                            //OPEN CAMERA
                           val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivityForResult(intent,CAMERA)
                        }else{
                            showRationalDialogForPermissions()
                        }
                    }

                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }
            }).onSameThread().check()
            dialog.dismiss()

        }

        binding.tvGallery.setOnClickListener{
           Dexter.withContext(this).withPermission(
               Manifest.permission.READ_EXTERNAL_STORAGE,
              // Manifest.permission.WRITE_EXTERNAL_STORAGE
           ).withListener(object :PermissionListener{
               override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                   val galleryIntent= Intent(Intent.ACTION_PICK,
                   MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                   startActivityForResult(galleryIntent, GALLERY)

               }

               override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                   Toast.makeText(
                       this@AddUpdateDishActivity,
                       "You have denied the storage permission to select image.",
                       Toast.LENGTH_SHORT
                   ).show()
               }

               override fun onPermissionRationaleShouldBeShown(
                   p0: PermissionRequest?,
                   p1: PermissionToken?
               ) {
                   showRationalDialogForPermissions()
               }


           }).onSameThread().check()
            dialog.dismiss()

        }
        dialog.show()

    }

    fun selectedListItem(item:String,selection:String){
        when(selection){
            Constants.DISH_TYPE->{
                mCustomListDialog.dismiss()
                mBinding.etType.setText(item)
            }
            Constants.DISH_CATEGORY->{
                mCustomListDialog.dismiss()
                mBinding.etCategory.setText(item)
            }
            Constants.DISH_COOKING_TIME->{
                mCustomListDialog.dismiss()
                mBinding.etCookingTime.setText(item)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK){
            if(requestCode== CAMERA){
                data?.extras?.let {
                    val thumbnail:Bitmap = data.extras!!.get("data") as Bitmap
                    //Use glide
                   // mBinding.ivDishImage.setImageBitmap(thumbnail)

                    Glide.with(this)
                        .load(thumbnail)
                        .centerCrop()
                        .into(mBinding.ivDishImage)
                    //GET IMAGE PATH ON GALLERY
                    mImagePath = saveImageToInternalStorage(thumbnail)
                    Log.i("imagepath",mImagePath)

                    //After got image then change to edit icon
                    mBinding.ivAddDishImage.setImageDrawable(
                       ContextCompat.getDrawable(this,R.drawable.ic_vector_edit)
                    )
                }

            }
        }
        if(resultCode== Activity.RESULT_OK){
            if(requestCode== GALLERY){
                data?.let {
                    val selectedPhotoUrl = data.data
                    //mBinding.ivDishImage.setImageURI(selectedPhotoUrl)

                    Glide.with(this)
                        .load(selectedPhotoUrl)
                        .centerCrop()
                            //GET IMAGE PATH ON GALLERY
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(object :RequestListener<Drawable>{
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                Log.e("TAG","Error loading image")
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                resource?.let {
                                    val bitmap:Bitmap = resource.toBitmap()
                                    mImagePath = saveImageToInternalStorage(bitmap)
                                    Log.e("TAG",mImagePath)
                                }
                                return false

                            }

                        })
                        .into(mBinding.ivDishImage)

                    //After got image then change to edit icon
                    mBinding.ivAddDishImage.setImageDrawable(
                        ContextCompat.getDrawable(this,R.drawable.ic_vector_edit)
                    )
                }

            }else if(resultCode==Activity.RESULT_CANCELED){
                Log.e("Cancelled","User Cancelled image selection")
            }
        }

    }
    // CANNOT ACCESS CAMERA
    private fun showRationalDialogForPermissions(){
        AlertDialog.Builder(this).setMessage(R.string.permission_failed)
            .setPositiveButton(R.string.go_to_setting){
                _,_->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    //Get the application link URI
                    val uri = Uri.fromParts("package",packageName,null)
                    intent.data = uri

                    startActivity(intent)


                }catch (e:ActivityNotFoundException){
                    e.printStackTrace()
                }
            }.setNegativeButton(R.string.cancel){
                dialog,_->
                    dialog.dismiss()
            }.show()

    }
    private fun saveImageToInternalStorage(bitmap: Bitmap):String{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY,Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")
        try {
            val stream:OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        }catch (e:IOException){
            e.printStackTrace()
        }
        return file.absolutePath
    }

    private fun customItemDialog(title:String,itemList:List<String>,selection:String){
        mCustomListDialog = Dialog(this)
        val binding : DialogCustomListBinding= DialogCustomListBinding.inflate(layoutInflater)

        //Use the xml file
        mCustomListDialog.setContentView(binding.root)
        binding.tvTitle.text = title
        binding.rvList.layoutManager = LinearLayoutManager(this)
        val adapter= CustomListItemAdapter(this,null,itemList,selection)
        binding.rvList.adapter = adapter
        mCustomListDialog.show()
    }

    companion object{
        private const val CAMERA = 1
        private const val GALLERY= 2

        private const val IMAGE_DIRECTORY = "FavDishImages" //The photo directory
    }

}