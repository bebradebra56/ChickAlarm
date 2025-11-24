package com.alra.sof.chickin.regoif.presentation.ui.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel

class ChickAlarmDataStore : ViewModel(){
    val chickAlarmViList: MutableList<ChickAlarmVi> = mutableListOf()
    var chickAlarmIsFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var chickAlarmContainerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var chickAlarmView: ChickAlarmVi

}