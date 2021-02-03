package com.odogwudev.workmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkInfo
import com.odogwudev.workmanager.utils.COOKINGWORKER
import com.odogwudev.workmanager.utils.DELIVERYWORKER
import com.odogwudev.workmanager.utils.PACKIGNWORKER
import com.odogwudev.workmanager.utils.PREPARATIONWORKER
import com.odogwudev.workmanager.viewmodel.DeliveryViewModel
import kotlinx.android.synthetic.main.activity_ordering.*
import java.text.SimpleDateFormat
import java.util.*

class OrderingActivity : AppCompatActivity() {
    private lateinit var deliveryViewModel: DeliveryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ordering)

        deliveryViewModel = ViewModelProvider(this)
            .get(DeliveryViewModel::class.java)

        deliveryViewModel.outputWorkInfos_prep
            .observe(this, deliveryObserver())
        deliveryViewModel.outputWorkInfos_cook
            .observe(this, deliveryObserver())
        deliveryViewModel.outputWorkInfos_pack
            .observe(this, deliveryObserver())
        deliveryViewModel.outputWorkInfos_del
            .observe(this, deliveryObserver())

        deliveryViewModel.order()
    }

    private fun deliveryObserver(): Observer<List<WorkInfo>> {
        return Observer { listOfWorkInfo ->

            if (listOfWorkInfo.isNullOrEmpty()) {
                return@Observer
            }

            val workInfo = listOfWorkInfo[0]
            val data = workInfo.outputData

            if (!data.getString(PREPARATIONWORKER).isNullOrEmpty()) {
                setOrderStatus(1f, getString(R.string.Preparation))
            } else if (!data.getString(COOKINGWORKER).isNullOrEmpty()) {
                setOrderStatus(2f, getString(R.string.Cooking))
            } else if (!data.getString(PACKIGNWORKER).isNullOrEmpty()) {
                setOrderStatus(3f, getString(R.string.Packing))
            } else if (!data.getString(DELIVERYWORKER).isNullOrEmpty()) {
                setOrderStatus(4f, getString(R.string.Delivery))
            }
        }
    }

    private fun setOrderStatus(phase: Float, phaseName: String) {
        pb_item_progress.progress = ((phase / 4) * 100).toInt()
        tv_steps.text = String.format(getString(R.string.fourPhase), phase.toInt())
        tv_orderStatus.append("$phaseName\n${Calendar.getInstance().time.formatTime()}\n\n")
    }
}

fun Date.formatTime(): String {
    val pattern = "HH:mm:ss"
    val simpleDateFormat = SimpleDateFormat(pattern)
    return simpleDateFormat.format(this)
}