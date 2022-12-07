package com.kejaplus.application.ui.AddProperty

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kejaplus.application.ui.mainui.MainActivity
import com.kejaplus.application.Model.Notification
import com.kejaplus.application.Model.Property
import com.kejaplus.application.R
import com.kejaplus.application.Support.InputValidator
import com.kejaplus.application.databinding.FragmentAddPropertyTwoBinding
import com.kejaplus.application.response.Status
import java.text.SimpleDateFormat
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
    private val viewModel: AddPropertyViewModel by viewModels()
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


       createNotificationChannel()

        return addPropertyTwoFragment.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        propertyText = args.propertyType
        noBedroomText = args.noBedrooms
        locationText = args.location
        imageText = args.imageUrl
        val uriImage = imageText.toUri()


        val ifOnline = viewModel.netConnectivity(mContext)
        if(ifOnline) {
            addPropertyTwoFragment.submitBtn.setOnClickListener(View.OnClickListener {
                inputValidation(propertyText, noBedroomText, locationText, uriImage)

            })
        } else {
            sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE)
            sweetAlertDialog.titleText = "Oops"
            sweetAlertDialog.contentText = "Network Error"
            sweetAlertDialog.setOnDismissListener(null)

        }

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
            savePropertyData(property,propertyType,noBedroom,location,propertyName,condition,price,contactNo,propertyDesc,imageUrl)
        }
    }

   private fun savePropertyData(propertyCategory: String, propertyType:String,noBedroom: String,location: String,propertyName: String,condition:String,
                                price: String,contactNo:String, propertyDesc: String, imageFilePath: Uri){
       val sweetAlertDialog = SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE)
       val imageId = ("images/" + UUID.randomUUID().toString())
       val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")


       val currentDateAndTime: String = simpleDateFormat.format(Date())
       val saveProperty = Property(propertyCategory,propertyType,noBedroom,location,propertyName,condition,price,contactNo,propertyDesc,imageFilePath,imageId,currentDateAndTime)

       viewModel.insertPropertyData(saveProperty)


       val title = "Property Data"
       val message = "Property Data Saved Successfully"

       val ts: String = simpleDateFormat.format(Date())

       val notification = Notification(0,title,message,ts)
       viewModel.insertNotification(notification)

       sweetAlertDialog.progressHelper.barColor = Color.parseColor("#41c300")
       sweetAlertDialog.titleText = "Loading..."
       sweetAlertDialog.setCancelable(false)
       sweetAlertDialog.show()

       viewModel.insertPropertyStatus.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
           when (it.status) {
               Status.SUCCESS -> {
                   sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                   sweetAlertDialog.titleText = "Success"
                   sweetAlertDialog.contentText = it.message
                   sweetAlertDialog.setOnDismissListener { dialog: DialogInterface? ->

                       addPropertyTwoFragment.propertyNameInput.text?.clear()
                       addPropertyTwoFragment.propertyTypeAutoCompleteTextView.text?.clear()
                       addPropertyTwoFragment.conditionAutoCompleteTextView.text?.clear()
                       addPropertyTwoFragment.priceInput.text?.clear()
                       addPropertyTwoFragment.contactNumberInput.text?.clear()
                       addPropertyTwoFragment.propertyDescriptionInput.text?.clear()


                       startActivity(Intent(mContext, MainActivity::class.java))
                   }



               }

               Status.LOADING -> {
                   sweetAlertDialog.progressHelper.barColor = ContextCompat.getColor(mContext, R.color.backColor)
                   sweetAlertDialog.titleText = "Loading..."
                   sweetAlertDialog.setCancelable(true)
                   sweetAlertDialog.show()
                   //inputValidation()
               }
               Status.ERROR -> {
                   sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE)
                   sweetAlertDialog.titleText = "Oops"
                   sweetAlertDialog.contentText = it.message.toString()
                   sweetAlertDialog.setOnDismissListener(null)

                   Snackbar.make(
                       addPropertyTwoFragment.root,
                       it.message.toString(),
                       Snackbar.LENGTH_LONG
                   ).show()
               }

               else -> {

               }
           }
       })

   }
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(MainActivity.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }



}