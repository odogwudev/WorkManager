package com.odogwudev.workmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.odogwudev.workmanager.utils.*
import com.odogwudev.workmanager.workers.CookingWorker
import com.odogwudev.workmanager.workers.DeliveryWorker
import com.odogwudev.workmanager.workers.PackingWorker
import com.odogwudev.workmanager.workers.PreparationWorker

class DeliveryViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val workManager = WorkManager.getInstance(application)

    // New instance variable for the WorkInfo
    internal val outputWorkInfos_prep: LiveData<List<WorkInfo>>
    internal val outputWorkInfos_cook: LiveData<List<WorkInfo>>
    internal val outputWorkInfos_pack: LiveData<List<WorkInfo>>
    internal val outputWorkInfos_del: LiveData<List<WorkInfo>>

    // Add an init block to the BlurViewModel class
    init {
        // This transformation makes sure that whenever the current work Id changes the WorkInfo
        // the UI is listening to changes
        outputWorkInfos_prep = workManager.getWorkInfosByTagLiveData(PREPARATIONWORKER)
        outputWorkInfos_cook = workManager.getWorkInfosByTagLiveData(COOKINGWORKER)
        outputWorkInfos_pack = workManager.getWorkInfosByTagLiveData(PACKIGNWORKER)
        outputWorkInfos_del = workManager.getWorkInfosByTagLiveData(DELIVERYWORKER)
    }

    internal fun order() {
        val prepartionBuilder = OneTimeWorkRequestBuilder<PreparationWorker>()
            .addTag(PREPARATIONWORKER)
            .build()

        var orderingProcess = workManager
            .beginUniqueWork(
                ORDERING_WORK_NAME,
                ExistingWorkPolicy.KEEP,
                prepartionBuilder
            )

        val cookingBuilder = OneTimeWorkRequestBuilder<CookingWorker>()
            .addTag(COOKINGWORKER)
            .build()
        orderingProcess = orderingProcess.then(cookingBuilder)
        val packingBuilder = OneTimeWorkRequestBuilder<PackingWorker>()
            .addTag(PACKIGNWORKER)
            .build()
        orderingProcess = orderingProcess.then(packingBuilder)
        val deliveryBuilder = OneTimeWorkRequestBuilder<DeliveryWorker>()
            .addTag(DELIVERYWORKER)
            .build()
        orderingProcess = orderingProcess.then(deliveryBuilder)

        orderingProcess.enqueue()
    }
}