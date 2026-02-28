package com.app.padams.ml

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FaceScanManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val WORK_NAME = "face_scan_work"
    }

    private val workManager = WorkManager.getInstance(context)

    fun startScan(): UUID {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val request = OneTimeWorkRequestBuilder<FaceScanWorker>()
            .setConstraints(constraints)
            .addTag(WORK_NAME)
            .build()

        workManager.enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.KEEP,
            request
        )

        return request.id
    }

    fun observeProgress(): Flow<WorkInfo?> {
        return workManager
            .getWorkInfosForUniqueWorkFlow(WORK_NAME)
            .map { it.firstOrNull() }
    }

    fun cancelScan() {
        workManager.cancelUniqueWork(WORK_NAME)
    }
}
