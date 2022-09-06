package com.kejaplus.application.Model

import com.example.kejaplus.Model.SaveProperty

data class FetchDataResponse(
    var property: List<SaveProperty>? = null,
    var exception: Exception? = null
)
