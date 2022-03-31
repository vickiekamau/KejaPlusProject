package com.kejaplus.application.interfaces

interface Communicator {
    fun passData(position: Int, propertyName: String, propertyType: String, propertyDesc: String, propertyLocation: String, propertyCondition: String, propertyCategory:String, price: String, timeStamp:String, propertyImage:String)

}