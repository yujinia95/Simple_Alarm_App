package com.bcit.lab9yujinjeong.data.imageAPI

import android.media.Image
import com.bcit.lab9yujinjeong.R
import com.bcit.lab9yujinjeong.data.dataclass.CatPhoto
import com.bcit.lab9yujinjeong.data.dataclass.ImageSrc
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.gson.gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 * This class is responsible for fetching images from API or use drawable images in error case.
 */
class ImageRepository {

    companion object{

        //Creating HTTP client to make network request.
        private val client = HttpClient(Android) {

            install(ContentNegotiation) {
                gson()  //Used for converting JSON responses into kotlin obj.
            }

            install(Logging) {
                level = LogLevel.BODY   //Logging full request and response.
            }

            //Setting default headers for all HTTP requests to send and receive JSON.
            defaultRequest {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
            }

            //This block of code prevents request from waiting indefinitely.(All 15sec)
            install(HttpTimeout) {
                requestTimeoutMillis = 15000L
                connectTimeoutMillis = 15000L
                socketTimeoutMillis  = 15000L
            }
        }

        //Making Singleton pattern for instance of ImageRepository.
        private var instance: ImageRepository? = null

        fun getInstance(): ImageRepository {

            //synchronized prevents race conditions.
            return instance ?: synchronized(this) {
                //Double checking instance is still null.
                //also { instance = it } returns newly created instance is assigned to instance.
                instance ?: ImageRepository().also {instance = it}
            }
        }
    }


    //Getting images from API asynchronously.
    //withContext(Dispatchers.IO) makes this function run on background thread(No blocking UI).
    suspend fun getImagesForCards(): Result<List<ImageSrc>> = withContext(Dispatchers.IO) {

        try {
            //Will hold both API img and drawable img.
            val imagePool = mutableListOf<ImageSrc>()


            //Getting images from API
            try {
                val response: List<CatPhoto> = client.get("https://cataas.com/api/cats") {
                    parameter("tags", "cute") //filtering img has "cute" tag.
                    parameter("limit", 10)   //requesting 10 imgs.
                }.body()    //.body extracting the body of HTTP response as specified type.

                //Transform list of CatPhoto obj into ImageSrc(data class) and add to image pool.
                imagePool.addAll(response.map {
                    ImageSrc(url = "https://cataas.com/cat/${it.id}")
                })

            } catch (error: Exception) {
                println("API fetch failed:( - ${error.message} (Check ImageRepository.kt)")
            }


            val drawableImg = getDrawableImageResources()
            //Transform list of drawable images into ImageSrc(data class) and add to image pool.
            imagePool.addAll(drawableImg.map {
                ImageSrc(resourceId = it)
            })

            //Shuffle and take 4 random API images.
            if (imagePool.size >= 4) {

                val randomImgs = imagePool.shuffled().take(4)
                Result.success(randomImgs)

                //In case API image is not available, use drawable images
            } else {

                Result.success(drawableImg.shuffled().map {
                    ImageSrc(resourceId = it)
                }.take(4))
            }

        } catch (error: Exception) {
            Result.failure(error)
        }
    }

    /**
     * Getting temp images from res/drawable.
     */
    private fun getDrawableImageResources(): List<Int> {

        return listOf(
            R.drawable.cow1,
            R.drawable.cow2,
            R.drawable.cow3,
            R.drawable.cow4,
            R.drawable.cow5,
            R.drawable.cow6,
            R.drawable.cow7,
            R.drawable.cow8,
            R.drawable.cow9,
            R.drawable.cow10
        )
    }
}
