package com.kejaplus.application.ui.AddProperty

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kejaplus.application.databinding.FragmentPropertyDetailsBinding
import com.squareup.picasso.Picasso
import timber.log.Timber
import java.io.File


class PropertyDetailsFragment : Fragment() {
    private lateinit var propertyDetailsBinding: FragmentPropertyDetailsBinding
    private lateinit var mContext: Context
    private lateinit var propertyName: String
    private lateinit var propertyType: String
    private lateinit var propertyDesc: String
    private lateinit var propertyCondition: String
    private lateinit var propertyLocation: String
    private lateinit var propertyCategory:String
    private lateinit var price: String
    private lateinit var timeStamp: String
    private lateinit var propertyImage: String

    val args :PropertyDetailsFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        propertyDetailsBinding = FragmentPropertyDetailsBinding.inflate(inflater,container,false)


        mContext = container!!.context
        return propertyDetailsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        propertyName = args.propertyName
        propertyType = args.propertyType
        propertyDesc = args.propertyDesc
        propertyCondition = args.propertyCondition
        propertyLocation = args.propertyLocation
        propertyCategory = args.propertyCategory
        price = args.price
        timeStamp = args.timeStamp
        propertyImage = args.propertyImage

        Timber.tag("name").d(propertyName)
        Timber.tag("type").d(propertyType)
        Timber.tag("description").d(propertyDesc)
        Timber.tag("condition").d(propertyCondition)
        Timber.tag("location").d(propertyLocation)
        Timber.tag("category").d(propertyCategory)
        Timber.tag("price").d(price)
        Timber.tag("timestamp").d(timeStamp)
        Timber.tag("propertyImage").d(propertyImage)

        propertyDetailsBinding.amount.text = price
        propertyDetailsBinding.propertyName.text = propertyName
        propertyDetailsBinding.locationUploaded.text = propertyLocation
        propertyDetailsBinding.category.text = propertyCategory
        propertyDetailsBinding.price.text = price
        propertyDetailsBinding.condition.text = propertyCondition
        propertyDetailsBinding.type.text = propertyType
        propertyDetailsBinding.dateUploaded.text = timeStamp
        propertyDetailsBinding.date.text = timeStamp
        //picasso.load(propertyImage).into(propertyDetailsBinding.propertyImage)
        fetchPropertyImage(propertyImage)


    }
    private fun fetchPropertyImage(propertyImage:String) {
        var storageReference = FirebaseStorage.getInstance().getReference("images/")
        val progressBar: ProgressBar = propertyDetailsBinding.progressBar
        storageReference = FirebaseStorage.getInstance().reference.child(propertyImage)
        Timber.tag("ImagePicasso").i(propertyImage)
        //val picasso = Picasso.get()


        storageReference.downloadUrl.addOnSuccessListener { uri -> // Got the download URL for the image
            progressBar.visibility = View.GONE
            // Pass it to Coil to download, show in ImageView and caching
            //picasso.load(uri.toString()).into(propertyDetailsBinding.propertyImage)
            propertyDetailsBinding.propertyImage.load(uri)
        }.addOnFailureListener { e ->
            // Handle any errors
            progressBar.visibility = View.GONE
            Toast.makeText(
                context,
                "Failed " + e.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}