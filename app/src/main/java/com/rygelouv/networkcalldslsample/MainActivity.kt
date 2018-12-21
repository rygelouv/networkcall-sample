package com.rygelouv.networkcalldslsample

import android.arch.lifecycle.Observer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getPostList().observe(this, Observer {
            when (it?.status) {
                Resource.LOADING -> {
                    Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show()
                }
                Resource.SUCCESS -> {
                    Toast.makeText(this, "success: ${it.data?.size} records fetched", Toast.LENGTH_SHORT).show()
                }
                Resource.ERROR -> {
                    Toast.makeText(this, "error: ${it.errorType}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    fun getPostList() = networkCall<PostResponse, List<Post>> {
        client = PostsAPI.postService.getPostList()
    }

}
