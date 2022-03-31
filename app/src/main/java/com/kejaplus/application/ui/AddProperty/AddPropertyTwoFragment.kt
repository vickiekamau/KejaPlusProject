package com.kejaplus.application.ui.AddProperty

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.kejaplus.Model.SaveProperty
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kejaplus.application.MainActivity
import com.kejaplus.application.R
import com.kejaplus.application.Support.InputValidator
import com.kejaplus.application.databinding.FragmentAddPropertyTwoBinding
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class AddPropertyTwoFragment: Fragment() {
    private lateinit var addPropertyTwoFragment: FragmentAddPropertyTwoBinding
    private lateinit var propertyText: String
    private lateinit var noBedroomText: String
    private lateinit var locationText: String
    private lateinit var imageText: String
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var mProgressDialog: ProgressDialog
    private lateinit var  sweetAlertDialog: SweetAlertDialog
    private lateinit var mContext: Context
    val args :AddPropertyTwoFragmentArgs by navArgs()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        addPropertyTwoFragment = FragmentAddPropertyTwoBinding.inflate(inflater,container,false)

        mContext = container!!.context
        FirebaseApp.initializeApp(mContext);
        storageReference = FirebaseStorage.getInstance().reference

        //property condition string
        val condition = resources.getStringArray(R.array.condition)
        val conditionStringAdapter = ArrayAdapter(requireContext(),
            R.layout.dropdown_item, condition)
        addPropertyTwoFragment.conditionAutoCompleteTextView.setAdapter(conditionStringAdapter)

        //property type string
        val type = resources.getStringArray(R.array.property_type)
        val typeStringAdapter = ArrayAdapter(requireContext(),
            R.layout.dropdown_item, type)
        addPropertyTwoFragment.propertyTypeAutoCompleteTextView.setAdapter(typeStringAdapter)



        //mProgressDialog = ProgressDialog(this@AddPropertyTwoFragment)
        //mProgressDialog.setMessage("Please Wait...")
        //mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        //mProgressDialog.setCancelable(false)
        //mProgressDialog.setCanceledOnTouchOutside(false)

        return addPropertyTwoFragment.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        propertyText = args.propertyType
        noBedroomText = args.noBedrooms
        locationText = args.location
        imageText = args.imageUrl
        val uriImage = imageText.toUri()

        Log.d("property1",propertyText)
        Log.d("bedrooms2", noBedroomText)
        Log.d("location3", locationText)
        Log.d("imageUrl4",imageText)

        addPropertyTwoFragment.submitBtn.setOnClickListener(View.OnClickListener {
            inputValidation(propertyText,noBedroomText,locationText,uriImage)

        })

    }


    //method that validates the text in the edittext layouts in the fragment
    private fun inputValidation(property:String, noBedroom: String, location: String, imageUrl: Uri) {
        val validator = InputValidator()
        if(validator.validateRequired(addPropertyTwoFragment.propertyName,addPropertyTwoFragment.propertyNameInput) &&
            validator.validateRequired(addPropertyTwoFragment.propertyTypeLayout,addPropertyTwoFragment.propertyTypeAutoCompleteTextView)&&
            validator.validateRequired(addPropertyTwoFragment.conditionLayout,addPropertyTwoFragment.conditionAutoCompleteTextView) &&
            validator.validateRequired(addPropertyTwoFragment.priceLayout,addPropertyTwoFragment.priceInput) &&
            validator.validateRequired(addPropertyTwoFragment.contactNumberLayout,addPropertyTwoFragment.contactNumberInput) &&
            validator.validateRequired(addPropertyTwoFragment.propertyDescriptionLayout, addPropertyTwoFragment.propertyDescriptionInput)){

            // fetch the text in the autocomplete text and edittext
            val propertyName = addPropertyTwoFragment.propertyNameInput.text.toString()
            val propertyType = addPropertyTwoFragment.propertyTypeAutoCompleteTextView.text.toString()
            val condition =  addPropertyTwoFragment.conditionAutoCompleteTextView.text.toString()
            val price = addPropertyTwoFragment.priceInput.text.toString()
            val contactNo = addPropertyTwoFragment.contactNumberInput.text.toString()
            val propertyDesc = addPropertyTwoFragment.propertyDescriptionInput.text.toString()

            //call the save method and parse in the all the fetched data from both fragments
            saveProperty(property,propertyType,noBedroom,location,propertyName,condition,price,contactNo,propertyDesc,imageUrl)
        }
    }
    // Save method that saves the property data to realtime database
    private fun saveProperty(propertyCategory: String, propertyType:String,noBedroom: String,location: String,propertyName: String,condition:String,
                             price: String,contactNo:String, propertyDesc: String, imageFilePath: Uri
    ){
        val sweetAlertDialog = SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE)
        val imageId = ("images/"
                + UUID.randomUUID().toString())

        databaseReference = FirebaseDatabase.getInstance().getReference("property")
        val propertyId = databaseReference.push().key
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        val currentDateAndTime: String = simpleDateFormat.format(Date())

        val saveProperty = SaveProperty(propertyId!!,propertyCategory,propertyType,noBedroom,location,propertyName,condition,price,contactNo,propertyDesc,imageId,currentDateAndTime)

        sweetAlertDialog.progressHelper.barColor = Color.parseColor("#41c300")
        sweetAlertDialog.titleText = "Loading..."
        sweetAlertDialog.setCancelable(false)
        sweetAlertDialog.show()

        databaseReference.child(propertyId).setValue(saveProperty).addOnCompleteListener{task ->
            if(task.isSuccessful){
                val ref: StorageReference = storageReference.child(imageId)
                ref.putFile(imageFilePath).addOnSuccessListener(OnSuccessListener<Any?> {

                    Toast.makeText(
                        requireActivity().application,
                        "Image Uploaded!!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }).addOnFailureListener(OnFailureListener { e ->
                    sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE)
                    sweetAlertDialog.titleText = "Oops"
                    sweetAlertDialog.contentText = e.message
                    sweetAlertDialog.setOnDismissListener(null)
                })

                sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                sweetAlertDialog.titleText = "Success"
                sweetAlertDialog.contentText = "Property Saved Successfully!"
                sweetAlertDialog.setOnDismissListener { dialog: DialogInterface? ->
                    startActivity(Intent(mContext, MainActivity::class.java))

                }
            }
            else {
                sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE)
                sweetAlertDialog.titleText = "Oops"
                sweetAlertDialog.contentText = "Property not Saved!, Please Retry"
                sweetAlertDialog.setOnDismissListener(null)
            }
        }

    }


}