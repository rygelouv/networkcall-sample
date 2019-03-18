package com.rygelouv.networkcalldslsample

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import retrofit2.HttpException
import retrofit2.Response

class CallHandler<RESPONSE : Any, DATA: Any> {
    lateinit var client: Deferred<Response<RESPONSE>>

    @Suppress("UNCHECKED_CAST")
    fun makeCall() : MutableLiveData<Resource<DATA>> {
        val result = MutableLiveData<Resource<DATA>>()
        result.setValue(Resource.loading(null))

        GlobalScope.launch {
            try {
                val response = client.awaitResult().getOrThrow() as DataResponse<DATA>

                withContext(Dispatchers.Main) {
                    result.value = Resource.success(response.retrieveData())
                }
            } catch (e: Throwable) {
                withContext(Dispatchers.Main) {
                    if (e is HttpException)
                        result.value = Resource.error("${e.message} | code ${e.response().code()}",
                                0)
                    else
                        result.value = Resource.error("${e.message}", 0)
                }
                e.printStackTrace()
            }
        }

        return result
    }
}

fun <RESPONSE: DataResponse<*>, DATA: Any> networkCall(block: CallHandler<RESPONSE, DATA>.() -> Unit): MutableLiveData<Resource<DATA>>
    = CallHandler<RESPONSE, DATA>().apply(block).makeCall()

    interface DataResponse<T> {
    fun retrieveData(): T
}
