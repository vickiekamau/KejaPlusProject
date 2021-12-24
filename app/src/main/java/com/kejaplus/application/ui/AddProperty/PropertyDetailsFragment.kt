package com.kejaplus.application.ui.AddProperty

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.navigation.fragment.navArgs
import com.kejaplus.application.R
import com.kejaplus.application.databinding.FragmentAddPropertyTwoBinding
import com.kejaplus.application.databinding.FragmentPropertyDetailsBinding


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

        Log.d("name",propertyName)
        Log.d("type", propertyType)
        Log.d("description", propertyDesc)
        Log.d("condition",propertyCondition)
        Log.d("location",propertyLocation)
        Log.d("category",propertyCategory)
        Log.d("price",price)

        propertyDetailsBinding.amount.text = price
        propertyDetailsBinding.propertyName.text = propertyName
        propertyDetailsBinding.locationUploaded.text = propertyLocation
        propertyDetailsBinding.category.text = propertyCategory
        propertyDetailsBinding.price.text = price
        propertyDetailsBinding.condition.text = propertyCondition
        propertyDetailsBinding.type.text = propertyType



    }

}