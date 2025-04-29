package com.bcit.assignment_yujinjeong.data.imageAPI

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.bcit.assignment_yujinjeong.R
import com.bcit.assignment_yujinjeong.data.dataclass.CatPhoto
import com.bcit.assignment_yujinjeong.data.dataclass.ImageSrc
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
import kotlinx.coroutines.delay

/**
 * This class is responsible for fetching images from API or use drawable images in error case.
 */
class ImageRepository {

    companion object {
        //Creating HTTP client to make network request.
        private val client = HttpClient(Android) {

            expectSuccess = true

            install(ContentNegotiation) {
                gson()  //Used for converting JSON responses into kotlin obj.
            }

            install(Logging) {
                level = LogLevel.ALL   //Logging full request and response.
            }

            //Setting default headers for all HTTP requests to send and receive JSON.
            defaultRequest {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
            }

            //This block of code prevents request from waiting indefinitely.(All 30sec)
            install(HttpTimeout) {
                requestTimeoutMillis = 30000L
                connectTimeoutMillis = 30000L
                socketTimeoutMillis = 30000L
            }
        }

        //Making Singleton pattern for instance of ImageRepository.
        private var instance: ImageRepository? = null

        fun getInstance(): ImageRepository {
            //synchronized prevents race conditions.
            return instance ?: synchronized(this) {
                //Double checking instance is still null.
                //also { instance = it } returns newly created instance is assigned to instance.
                instance ?: ImageRepository().also { instance = it }
            }
        }
    }


    //Getting images from API asynchronously.
    //withContext(Dispatchers.IO) makes this function run on background thread(No blocking UI).
    suspend fun getImagesForCards(): Result<List<ImageSrc>> = withContext(Dispatchers.IO) {
        try {
            //Will hold both API img and drawable img.
            val imagePool = mutableListOf<ImageSrc>()
            var apiSuccess = false

            //Getting images from API
            for (attempt in 1..3) {
                try {
                    Log.d("ImageRepository", "API fetch attempt $attempt")

                    val response: List<CatPhoto> = client.get("https://cataas.com/api/cats") {
                        parameter("tags", "cute") //filtering img has "cute" tag.
                        parameter("limit", 10)   //requesting 10 imgs.
                    }.body()    //.body extracting the body of HTTP response as specified type.


                    //Transform list of CatPhoto obj into ImageSrc(data class) and add to image pool.
                    if (response.isNotEmpty()) {

                        imagePool.addAll(response.map {
                            val url = "https://cataas.com/cat/${it.id}"
                            Log.d("ImageRepository", "Adding image URL: $url")
                            ImageSrc(url = url)
                        })

                        apiSuccess = true
                        Log.d(
                            "ImageRepository",
                            "Successfully fetched ${response.size} images from API"
                        )
                        break
                    }

                } catch (error: Exception) {
                    println("API fetch failed:( - ${error.message} (Check ImageRepository.kt)")
                }
                delay(1000)
            }

            //Only fall back to drawable images if API completely failed
            if (!apiSuccess) {

                val drawableImg = getDrawableImageResources()

                println("Drawable images found: ${drawableImg.size}")

                imagePool.addAll(drawableImg.map {
                    ImageSrc(resourceId = it)
                })
            }

            //Prioritizing API images in selection
            val apiImages = imagePool.filter { it.url != null }
            val drawableImages = imagePool.filter { it.resourceId != null }

            //Shuffle and take 4 random API images.
            val selectedImages = when {
                apiImages.size >= 4 -> apiImages.shuffled().take(4)
                apiImages.isNotEmpty() -> {
                    // Use all available API images and fill the rest with drawables
                    val needed = 4 - apiImages.size
                    apiImages + drawableImages.shuffled().take(needed)
                }

                else -> drawableImages.shuffled().take(4)
            }

            Result.success(selectedImages)

        } catch (error: Exception) {
            Result.failure(error)
        }
    }

    /**
     * Getting temp images from res/drawable.
     */
    private fun getDrawableImageResources(): List<Int> {
        val drawables = listOf(
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

        // Debug logging
        println("Drawable resources found: ${drawables.size}")
        drawables.forEachIndexed { index, resourceId ->
            println("Drawable $index: $resourceId")
        }

        return drawables
    }
}