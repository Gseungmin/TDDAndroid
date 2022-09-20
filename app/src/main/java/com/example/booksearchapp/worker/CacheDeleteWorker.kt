package com.example.booksearchapp.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class CacheDeleteWorker(
    context: Context,
    workerParams: WorkerParameters,
) : Worker(context, workerParams) {

    //Worker 클래스를 상속 받아 doWork에 내부 구현
    override fun doWork(): Result {
        return try {
            //캐시를 제거하는 가상 상황을 가정하여 로그로 해결
            Log.d("WorkManager", "Cache has successfully deleted")
            Result.success()
        } catch (exception: Exception) {
            exception.printStackTrace()
            Result.failure()
        }
    }
}