package cn.vce.easylook.feature_music.other

data class DownloadResult<out T>
    (val status: Status, val data: T?, val message: String?, val name: String? = null,
     val currentLength: Long? = null, val length: Long? = null, val process: Float? = null){
    companion object {
        fun <T> success(data: T? = null) = DownloadResult(Status.SUCCESS, data, null)

        fun <T> error(message: String, data: T? = null) = DownloadResult(Status.ERROR, data, message)

        fun <T> loading(currentLength: Long, length: Long, process: Float, name: String) =
            DownloadResult(Status.LOADING, null, null, name, currentLength, length, process)
    }
}