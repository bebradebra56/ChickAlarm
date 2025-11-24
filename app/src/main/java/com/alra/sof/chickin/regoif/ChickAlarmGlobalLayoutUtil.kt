package com.alra.sof.chickin.regoif

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import com.alra.sof.chickin.regoif.presentation.app.ChickAlarmApplication

class ChickAlarmGlobalLayoutUtil {

    private var chickAlarmMChildOfContent: View? = null
    private var chickAlarmUsableHeightPrevious = 0

    fun chickAlarmAssistActivity(activity: Activity) {
        val content = activity.findViewById<FrameLayout>(android.R.id.content)
        chickAlarmMChildOfContent = content.getChildAt(0)

        chickAlarmMChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener {
            possiblyResizeChildOfContent(activity)
        }
    }

    private fun possiblyResizeChildOfContent(activity: Activity) {
        val chickAlarmUsableHeightNow = chickAlarmComputeUsableHeight()
        if (chickAlarmUsableHeightNow != chickAlarmUsableHeightPrevious) {
            val chickAlarmUsableHeightSansKeyboard = chickAlarmMChildOfContent?.rootView?.height ?: 0
            val chickAlarmHeightDifference = chickAlarmUsableHeightSansKeyboard - chickAlarmUsableHeightNow

            if (chickAlarmHeightDifference > (chickAlarmUsableHeightSansKeyboard / 4)) {
                activity.window.setSoftInputMode(ChickAlarmApplication.chickAlarmInputMode)
            } else {
                activity.window.setSoftInputMode(ChickAlarmApplication.chickAlarmInputMode)
            }
//            mChildOfContent?.requestLayout()
            chickAlarmUsableHeightPrevious = chickAlarmUsableHeightNow
        }
    }

    private fun chickAlarmComputeUsableHeight(): Int {
        val r = Rect()
        chickAlarmMChildOfContent?.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top  // Visible height без status bar
    }
}