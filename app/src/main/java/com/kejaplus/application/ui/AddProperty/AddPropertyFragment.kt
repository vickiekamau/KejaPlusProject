package com.kejaplus.application.ui.AddProperty

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavAction
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.kejaplus.application.BuildConfig
import com.kejaplus.application.R
import com.kejaplus.application.Support.InputValidator
import com.kejaplus.application.databinding.FragmentAddPropertyBinding
import com.kejaplus.application.ui.Map.MapsFragment
import java.io.File


class AddPropertyFragment : Fragment() {
    private lateinit var addPropertyBinding: FragmentAddPropertyBinding
    private lateinit var imageview1Tag :String
    private lateinit var imageUrl: String
    private lateinit var locationCoordinates: String
    private lateinit var locationName: String

    private val takeImageResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            latestTmpUri?.let { uri ->
                previewImage.setImageURI(uri)
                previewImage.tag = uri
                imageUrl = uri.toString()
                Log.e("cameraImage",previewImage.tag.toString())
            }
        }
    }

    private val selectImageFromGalleryResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { previewImage.setImageURI(uri)
            previewImage.tag = uri;
            imageUrl = uri.toString()
            Log.e("galleryImage",previewImage.tag.toString())
            Log.e("gallery image",uri.toString()
            )}
    }

    private var latestTmpUri: Uri? = null

    private val previewImage by lazy {addPropertyBinding.iDImageView }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        addPropertyBinding = FragmentAddPropertyBinding.inflate(inflater,container,false)

        Log.e("Add property fragment", "fragment add property")

        //property strings
        val property = resources.getStringArray(R.array.property_category)
        val propertyStringAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item,property)
        addPropertyBinding.propertyAutoCompleteTextView.setAdapter(propertyStringAdapter)

        //bedroom Strings
        val noBedroom = resources.getStringArray(R.array.number_bedroom)
        val bedroomStringAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item,noBedroom)
        addPropertyBinding.bedroomAutoCompleteTextView.setAdapter(bedroomStringAdapter)

        // location Strings
        addPropertyBinding.locationInput.isEnabled = false
        addPropertyBinding.locationInput.isFocusable = false
        imageview1Tag = addPropertyBinding.iDImageView.tag.toString()
        addPropertyBinding.locationLayout.setEndIconOnClickListener {
            findNavController().navigate(R.id.mapsFragment)
        }

        addPropertyBinding.btnNext.setOnClickListener(View.OnClickListener { view ->  inputValidation() })
        addPropertyBinding.addButton1.setOnClickListener(View.OnClickListener { view -> fileUpload(context)
            addPropertyBinding.addButton1.visibility= View.INVISIBLE
            addPropertyBinding.closeButton1.visibility = View.VISIBLE
        })

        addPropertyBinding.closeButton1.setOnClickListener(View.OnClickListener { addPropertyBinding.iDImageView.setImageResource(R.drawable.picture)
            addPropertyBinding.addButton1.visibility= View.VISIBLE
            addPropertyBinding.closeButton1.visibility = View.INVISIBLE
        })

        return addPropertyBinding.root

    }

    private fun inputValidation() {
        val validator = InputValidator()

        if(validator.validateRequired(addPropertyBinding.propertyLayout,addPropertyBinding.propertyAutoCompleteTextView) &&
            validator.validateRequired(addPropertyBinding.noOfBedroomLayout,addPropertyBinding.bedroomAutoCompleteTextView) &&
            validator.validateRequired(addPropertyBinding.locationLayout,addPropertyBinding.locationInput)&&
            imageValidation(addPropertyBinding.iDImageView)


        ){
            val property = addPropertyBinding.propertyAutoCompleteTextView.text.toString()
            val bRoom = addPropertyBinding.bedroomAutoCompleteTextView.text.toString()
            val location = addPropertyBinding.locationInput.text.toString()

          val action =  AddPropertyFragmentDirections.actionNavigationAddPropertyToAddPropertyTwoFragment(property,bRoom,location,imageUrl)
            findNavController().navigate(action)
        }

    }
    private fun imageValidation(imageView: ImageView):Boolean {
        return if(addPropertyBinding.iDImageView.tag != "image1"){
            true
        } else{
            addPropertyBinding.errorX.text = "Image Required"
            Log.e("Image", "Image Required")
            Log.e("ImageURL", addPropertyBinding.iDImageView.tag.toString() )
            false
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        // We use a String here, but any type that can be put in a Bundle is supported
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>(MapsFragment.COORDINATE_KEY)?.observe(
                viewLifecycleOwner) { coordinate ->
            // Do something with the result.
             if(!coordinate.isEmpty()){
                 locationCoordinates = coordinate
                 Log.d("Location Coordinates",locationCoordinates)
                 addPropertyBinding.locationInput.text = Editable.Factory.getInstance().newEditable(locationCoordinates)
                 //addPropertyBinding.locationLayout.isEnabled = false
                 //addPropertyBinding.locationLayout.isEndIconVisible =true
                 addPropertyBinding.locationLayout.isFocusable = false
                 savedInstanceState?.remove(MapsFragment.COORDINATE_KEY)
             } else{
                 addPropertyBinding.locationLayout.isEnabled = true
                 addPropertyBinding.locationLayout.isFocusable = true
             }


            }
        }




    private fun fileUpload(mContext: Context?) {
        val alertDialog = AlertDialog.Builder(mContext).create()
        val v: View = LayoutInflater.from(mContext).inflate(R.layout.file_type_choose, null)
        val fileChooserClickListener = View.OnClickListener { v1: View ->
            when (v1.id) {
                R.id.cameraChoice -> mContext?.let { takeImage() }
                R.id.fileChoice -> mContext?.let { selectImageFromGallery() }
                else -> {
                }
            }
            alertDialog.dismiss()
        }
        v.findViewById<View>(R.id.cameraChoice).setOnClickListener(fileChooserClickListener)
        v.findViewById<View>(R.id.fileChoice).setOnClickListener(fileChooserClickListener)
        //sweetAlertDialog.show();
        alertDialog.setView(v)
        alertDialog.show()
        alertDialog.setCancelable(false)
    }

    private fun takeImage() {
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                takeImageResult.launch(uri)
                Log.e("camera image", uri.toString())
            }
        }
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png").apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(requireActivity().application, "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
    }
    private fun getImageUri(imageUri:String):String{
        return imageUri
    }


}