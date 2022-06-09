package com.kejaplus.application.Model

import android.net.Uri


data class Property(val property_category:String, val property_type: String, val no_bedroom:String,
                    val location:String, val property_name: String, val condition: String, val price: String,
                    val contact_no: String, val property_desc: String, val imagePath: Uri,val imageId : String, val timeStamp:String) {


    }

