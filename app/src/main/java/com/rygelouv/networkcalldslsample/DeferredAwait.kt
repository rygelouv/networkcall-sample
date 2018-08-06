package com.rygelouv.networkcalldslsample

import kotlinx.coroutines.experimental.CancellableContinuation
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import retrofit2.HttpException
import retrofit2.Response


suspend fun <T : Any> Deferred<Response<T>>.awaitResult(): Result<T> {
    return suspendCancellableCoroutine { continuation ->

        launch {
          try {
              val response = await()
              continuation.resume(
                      if (response.isSuccessful) {
                          val body = response.body()
                          body?.let {
                              Result.Ok(it, response.raw())
                          } ?: "error".let {
                              if (response.code() ==200){
                                  Result.Exception(Exception("body is empty"))
                              }
                              else{
                                  Result.Exception(NullPointerException("Response body is null"))
                              }


                          }

                      } else {
                          Result.Error(HttpException(response), response.raw())
                      }
              )
          }
          catch (e:Throwable){
            //  Log.e("DeferredAwait",e.message)
             continuation.resume(Result.Exception(e))
          }

        }

        registerOnCompletion(continuation)
    }
}



private fun Deferred<Response<*>>.registerOnCompletion(continuation: CancellableContinuation<*>) {
    continuation.invokeOnCompletion {
        if (continuation.isCancelled)
            try {
                cancel()
            } catch (ex: Throwable) {
                //Ignore cancel exception
                ex.printStackTrace()
            }
    }
}
