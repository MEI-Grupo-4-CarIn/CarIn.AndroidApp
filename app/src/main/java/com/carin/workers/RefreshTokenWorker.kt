package com.carin.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.carin.utils.AuthUtils
import com.carin.utils.NetworkUtils
import kotlinx.coroutines.runBlocking
import java.util.Date
import kotlin.time.Duration.Companion.minutes

class RefreshTokenWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val userAuth = AuthUtils.getUserAuth(applicationContext)

        // If userAuth is not authenticated or the token is valid for more than 5 minutes
        if (userAuth == null || userAuth.expiresOn.after(Date(System.currentTimeMillis() + 5.minutes.inWholeMilliseconds))) {
            return Result.success()
        }

        if (!NetworkUtils.isNetworkAvailable(applicationContext)) {
            return Result.retry()
        }

        return runBlocking {
            val result = AuthUtils.refreshTokenBlocking(applicationContext, userAuth.refreshToken)
            if (result) {
                Result.success()
            } else {
                Result.retry()
            }
        }
    }
}