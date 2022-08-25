package  com.kejaplus.application.response

data class Resource<out T>(val status: Status, val data: T?, val id: String, val message: String?) {
    companion object {
        fun <T> success(data: T?, id: String): Resource<T> =
            Resource(status = Status.SUCCESS, data = data, id = id, message = "User Created Successfully")

        fun <T> error(data: T?, message: String): Resource<T> =
            Resource(status = Status.ERROR, data = data, message = message, id = "")

        fun <T> loading(data: T?): Resource<T> =
            Resource(status = Status.LOADING, data = data, message = "Loading", id = "")
    }
}
