package com.example.kejaplus.Model

class SaveProperty(val id: String, val property_type: String, val no_bedroom:String, val location:String, val property_name: String, val condition: String, val price: String, val contact_no: String, val property_desc: String, val image:String)
{
    constructor() : this(id = "", property_type = "", no_bedroom ="", location = "", property_name ="", condition="", price="", contact_no="", property_desc="", image="") // this constructor is an explicit
    // "empty" constructor, as seen by Java.
}