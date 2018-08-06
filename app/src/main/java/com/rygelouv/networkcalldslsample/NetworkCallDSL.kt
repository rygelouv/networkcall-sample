package com.rygelouv.networkcalldslsample

import android.arch.lifecycle.MutableLiveData
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import retrofit2.Response



class CallHandler<RESPONSE : Any, DATA: Any> {
    lateinit var client: Deferred<Response<RESPONSE>>

    fun makeCall() : MutableLiveData<Resource<DATA>> {
        val result = MutableLiveData<Resource<DATA>>()
        result.setValue(Resource.loading(null))
        launch {
            try {
                val response = client.awaitResult().getOrThrow() as DataResponse<DATA>
                withContext(UI) { result.value = Resource.success(response.retrieveData()) }
            }
            catch (e: Exception) {
                // e.handleException(result)
            }
        }
        return result
    }
}

fun <RESPONSE: DataResponse<*>, DATA: Any> networkCall(block: CallHandler<RESPONSE, DATA>.() -> Unit): MutableLiveData<Resource<DATA>> = CallHandler<RESPONSE, DATA>().apply(block).makeCall()

interface DataResponse<T> {
    fun retrieveData(): T
}
