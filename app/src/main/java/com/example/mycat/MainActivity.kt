package com.example.mycat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.example.mycat.api.CatApiService
import com.example.mycat.model.ImageResultData
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class MainActivity : AppCompatActivity() {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/v1/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }//We use lazy to make sure the instances are only created when needed.

    private val  catApiService by lazy {
        retrofit.create(CatApiService::class.java)
    }
    private val agentBreedView: TextView by lazy { mainAgentBreedValue }

    private val profileImageView: ImageView by lazy { mainProfileImage }

    private val imageLoader : ImageLoader by lazy { GlideImageLoader(this) }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            getCatImageResponse()
        }

    private fun getCatImageResponse(){
        val call = catApiService.searchImages(1, "full")
        call.enqueue(object : Callback<List<ImageResultData>>{//With the instance obtained in this fashion, we can execute an async request by calling call. enqueue(Callback)
            override fun onFailure(call: Call<List<ImageResultData>>, t: Throwable) {
                Log.e("MainActivity", "Failed to get search results", t)
            }

            override fun onResponse(call: Call<List<ImageResultData>>, response: Response<List<ImageResultData>>) {
                if (response.isSuccessful){
                   val imageResults = response.body()
                    val firstImageUrl = imageResults?.firstOrNull()?.imageUrl ?: "No Url"

                    if (firstImageUrl.isNotBlank()){
                        imageLoader.loadImage(firstImageUrl, profileImageView)
                    }else {
                        Log.d("MainActivity", "Missing image URL")
                    }

                    agentBreedView.text = imageResults?.firstOrNull()?.breeds?.firstOrNull()?.name ?:"UnKnown Value"
                }else{
                    Log.e(
                        "MainActivity","Failed to get search results \n ${response.errorBody()?.string() ?: "" }"
                    )
                }
            }
        })

    }


}
