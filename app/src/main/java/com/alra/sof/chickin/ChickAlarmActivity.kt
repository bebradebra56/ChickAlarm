package com.alra.sof.chickin

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.alra.sof.chickin.regoif.ChickAlarmGlobalLayoutUtil
import com.alra.sof.chickin.regoif.chickAlarmSetupSystemBars
import com.alra.sof.chickin.regoif.presentation.app.ChickAlarmApplication
import com.alra.sof.chickin.regoif.presentation.pushhandler.ChickAlarmPushHandler
import org.koin.android.ext.android.inject

class ChickAlarmActivity : AppCompatActivity() {
    private val chickAlarmPushHandler by inject<ChickAlarmPushHandler>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        chickAlarmSetupSystemBars()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_chick_alarm)
        val chickAlarmRootView = findViewById<View>(android.R.id.content)
        ChickAlarmGlobalLayoutUtil().chickAlarmAssistActivity(this)
        ViewCompat.setOnApplyWindowInsetsListener(chickAlarmRootView) { chickAlarmView, chickAlarmInsets ->
            val chickAlarmSystemBars = chickAlarmInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val chickAlarmDisplayCutout = chickAlarmInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val chickAlarmIme = chickAlarmInsets.getInsets(WindowInsetsCompat.Type.ime())


            val chickAlarmTopPadding = maxOf(chickAlarmSystemBars.top, chickAlarmDisplayCutout.top)
            val chickAlarmLeftPadding = maxOf(chickAlarmSystemBars.left, chickAlarmDisplayCutout.left)
            val chickAlarmRightPadding = maxOf(chickAlarmSystemBars.right, chickAlarmDisplayCutout.right)
            window.setSoftInputMode(ChickAlarmApplication.chickAlarmInputMode)

            if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                Log.d(ChickAlarmApplication.CHICK_ALARM_MAIN_TAG, "ADJUST PUN")
                val chickAlarmBottomInset = maxOf(chickAlarmSystemBars.bottom, chickAlarmDisplayCutout.bottom)

                chickAlarmView.setPadding(chickAlarmLeftPadding, chickAlarmTopPadding, chickAlarmRightPadding, 0)

                chickAlarmView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = chickAlarmBottomInset
                }
            } else {
                Log.d(ChickAlarmApplication.CHICK_ALARM_MAIN_TAG, "ADJUST RESIZE")

                val chickAlarmBottomInset = maxOf(chickAlarmSystemBars.bottom, chickAlarmDisplayCutout.bottom, chickAlarmIme.bottom)

                chickAlarmView.setPadding(chickAlarmLeftPadding, chickAlarmTopPadding, chickAlarmRightPadding, 0)

                chickAlarmView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = chickAlarmBottomInset
                }
            }



            WindowInsetsCompat.CONSUMED
        }
        Log.d(ChickAlarmApplication.CHICK_ALARM_MAIN_TAG, "Activity onCreate()")
        chickAlarmPushHandler.chickAlarmHandlePush(intent.extras)
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            chickAlarmSetupSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()
        chickAlarmSetupSystemBars()
    }
}