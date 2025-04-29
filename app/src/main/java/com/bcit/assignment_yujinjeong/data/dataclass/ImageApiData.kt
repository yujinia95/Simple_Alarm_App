package com.bcit.assignment_yujinjeong.data.dataclass

import com.google.gson.annotations.SerializedName

//For getting cat images from API
data class CatPhoto(

    //SerializedName provides the name that matches the JSON
    @SerializedName("id") val id: String,
    @SerializedName("tags") val tags: List<String>,
    @SerializedName("mimetype") val mimeType: String,
    @SerializedName("createdAt") val createdAt: String
)

//For image source. Uses an API image (URL) if available, otherwise falls back to drawable resource
// in case of an error.
data class ImageSrc(
    val url: String? = null,
    val resourceId: Int? = null
) {
    //This equals ans hashcode is to find matching card by comparing the same object.
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ImageSrc) return false

        if (url != null && other.url != null) return url == other.url
        if (resourceId != null && other.resourceId != null) return resourceId == other.resourceId

        return false
    }

    override fun hashCode(): Int {
        var result = url?.hashCode() ?: 0
        result = 31 * result + (resourceId ?: 0)
        return result
    }
}