package com.alra.sof.chickin.regoif.presentation.ui.view

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.alra.sof.chickin.regoif.presentation.app.ChickAlarmApplication
import com.alra.sof.chickin.regoif.presentation.ui.load.ChickAlarmLoadFragment
import org.koin.android.ext.android.inject

class ChickAlarmV : Fragment(){

    private lateinit var chickAlarmPhoto: Uri
    private var chickAlarmFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val chickAlarmTakeFile: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        chickAlarmFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
        chickAlarmFilePathFromChrome = null
    }

    private val chickAlarmTakePhoto: ActivityResultLauncher<Uri> = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            chickAlarmFilePathFromChrome?.onReceiveValue(arrayOf(chickAlarmPhoto))
            chickAlarmFilePathFromChrome = null
        } else {
            chickAlarmFilePathFromChrome?.onReceiveValue(null)
            chickAlarmFilePathFromChrome = null
        }
    }

    private val chickAlarmDataStore by activityViewModels<ChickAlarmDataStore>()


    private val chickAlarmViFun by inject<ChickAlarmViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(ChickAlarmApplication.CHICK_ALARM_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (chickAlarmDataStore.chickAlarmView.canGoBack()) {
                        chickAlarmDataStore.chickAlarmView.goBack()
                        Log.d(ChickAlarmApplication.CHICK_ALARM_MAIN_TAG, "WebView can go back")
                    } else if (chickAlarmDataStore.chickAlarmViList.size > 1) {
                        Log.d(ChickAlarmApplication.CHICK_ALARM_MAIN_TAG, "WebView can`t go back")
                        chickAlarmDataStore.chickAlarmViList.removeAt(chickAlarmDataStore.chickAlarmViList.lastIndex)
                        Log.d(ChickAlarmApplication.CHICK_ALARM_MAIN_TAG, "WebView list size ${chickAlarmDataStore.chickAlarmViList.size}")
                        chickAlarmDataStore.chickAlarmView.destroy()
                        val previousWebView = chickAlarmDataStore.chickAlarmViList.last()
                        chickAlarmAttachWebViewToContainer(previousWebView)
                        chickAlarmDataStore.chickAlarmView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (chickAlarmDataStore.chickAlarmIsFirstCreate) {
            chickAlarmDataStore.chickAlarmIsFirstCreate = false
            chickAlarmDataStore.chickAlarmContainerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return chickAlarmDataStore.chickAlarmContainerView
        } else {
            return chickAlarmDataStore.chickAlarmContainerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(ChickAlarmApplication.CHICK_ALARM_MAIN_TAG, "onViewCreated")
        if (chickAlarmDataStore.chickAlarmViList.isEmpty()) {
            chickAlarmDataStore.chickAlarmView = ChickAlarmVi(requireContext(), object :
                ChickAlarmCallBack {
                override fun chickAlarmHandleCreateWebWindowRequest(chickAlarmVi: ChickAlarmVi) {
                    chickAlarmDataStore.chickAlarmViList.add(chickAlarmVi)
                    Log.d(ChickAlarmApplication.CHICK_ALARM_MAIN_TAG, "WebView list size = ${chickAlarmDataStore.chickAlarmViList.size}")
                    Log.d(ChickAlarmApplication.CHICK_ALARM_MAIN_TAG, "CreateWebWindowRequest")
                    chickAlarmDataStore.chickAlarmView = chickAlarmVi
                    chickAlarmVi.chickAlarmSetFileChooserHandler { callback ->
                        chickAlarmHandleFileChooser(callback)
                    }
                    chickAlarmAttachWebViewToContainer(chickAlarmVi)
                }

            }, chickAlarmWindow = requireActivity().window).apply {
                chickAlarmSetFileChooserHandler { callback ->
                    chickAlarmHandleFileChooser(callback)
                }
            }
            chickAlarmDataStore.chickAlarmView.chickAlarmFLoad(arguments?.getString(ChickAlarmLoadFragment.CHICK_ALARM_D) ?: "")
//            ejvview.fLoad("www.google.com")
            chickAlarmDataStore.chickAlarmViList.add(chickAlarmDataStore.chickAlarmView)
            chickAlarmAttachWebViewToContainer(chickAlarmDataStore.chickAlarmView)
        } else {
            chickAlarmDataStore.chickAlarmViList.forEach { webView ->
                webView.chickAlarmSetFileChooserHandler { callback ->
                    chickAlarmHandleFileChooser(callback)
                }
            }
            chickAlarmDataStore.chickAlarmView = chickAlarmDataStore.chickAlarmViList.last()

            chickAlarmAttachWebViewToContainer(chickAlarmDataStore.chickAlarmView)
        }
        Log.d(ChickAlarmApplication.CHICK_ALARM_MAIN_TAG, "WebView list size = ${chickAlarmDataStore.chickAlarmViList.size}")
    }

    private fun chickAlarmHandleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        Log.d(ChickAlarmApplication.CHICK_ALARM_MAIN_TAG, "handleFileChooser called, callback: ${callback != null}")

        chickAlarmFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    Log.d(ChickAlarmApplication.CHICK_ALARM_MAIN_TAG, "Launching file picker")
                    chickAlarmTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                1 -> {
                    Log.d(ChickAlarmApplication.CHICK_ALARM_MAIN_TAG, "Launching camera")
                    chickAlarmPhoto = chickAlarmViFun.chickAlarmSavePhoto()
                    chickAlarmTakePhoto.launch(chickAlarmPhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                Log.d(ChickAlarmApplication.CHICK_ALARM_MAIN_TAG, "File chooser canceled")
                callback?.onReceiveValue(null)
                chickAlarmFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun chickAlarmAttachWebViewToContainer(w: ChickAlarmVi) {
        chickAlarmDataStore.chickAlarmContainerView.post {
            (w.parent as? ViewGroup)?.removeView(w)
            chickAlarmDataStore.chickAlarmContainerView.removeAllViews()
            chickAlarmDataStore.chickAlarmContainerView.addView(w)
        }
    }


}