package com.rygelouv.networkcalldslsample

import kotlinx.coroutines.experimental.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient



class Repository {
    fun getPostList() = networkCall<PostResponse, List<Post>> {
        client = PostsAPI.postService.getPostList()
    }
}

data class Post(val title: String, val content: String)

data class PostResponse(val data: List<Post>): BaseApiResponse(), DataResponse<List<Post>> {
    override fun retrieveData(): List<Post> = data
}

abstract class BaseApiResponse {
    var status: Int = 0
    var message: String? = null
}

object PostsAPI {

    var API_BASE_URL: String = "https://api.github.com/"
    var httpClient = OkHttpClient.Builder()
    var builder: Retrofit.Builder = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
    var retrofit = builder
            .client(httpClient.build())
            .build()

    var postService = retrofit.create<PostService>(PostService::class.java!!)

    interface PostService {
        @GET("posts/list")
        fun getPostList(): Deferred<Response<PostResponse>>
    }
}