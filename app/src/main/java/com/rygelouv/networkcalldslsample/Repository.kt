package com.rygelouv.networkcalldslsample

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import retrofit2.http.Query


object Repository {
    fun getRepos(query: String) = networkCall<ResposResponse, List<Repo>> {
        client = PostsAPI.postService.getRepos(query)
    }
}

data class Repo(val id: Int, val name: String, val full_name: String, val description: String, val git_url:String)

data class ResposResponse(val items: List<Repo>): BaseApiResponse<Repo>(), DataResponse<List<Repo>> {
    override fun retrieveData(): List<Repo> = items
}

abstract class BaseApiResponse<T> {
    var total_count: Int = 0
    var incomplete_results: Boolean = false
}

object PostsAPI {

    var API_BASE_URL: String = "https://api.github.com/"
    var httpClient = OkHttpClient.Builder()
    var builder: Retrofit.Builder = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
    var retrofit = builder
            .client(httpClient.build())
            .build()

    var postService = retrofit.create<PostService>(PostService::class.java)

    interface PostService {
        @GET("search/repositories")
        fun getRepos(@Query("q") query: String): Deferred<Response<ResposResponse>>
    }
}