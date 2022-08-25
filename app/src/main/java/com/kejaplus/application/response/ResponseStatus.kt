package com.kejaplus.application.response


data class ResponseStatus(
    val isSuccessful: Boolean = false,

) {
    constructor(response: Response) : this(
        isSuccessful = response.isSuccessful,

    )
}
