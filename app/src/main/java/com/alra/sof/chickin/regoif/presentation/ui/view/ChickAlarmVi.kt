package com.alra.sof.chickin.regoif.presentation.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Message
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import com.alra.sof.chickin.regoif.presentation.app.ChickAlarmApplication

class ChickAlarmVi(
    private val chickAlarmContext: Context,
    private val chickAlarmCallback: ChickAlarmCallBack,
    private val chickAlarmWindow: Window
) : WebView(chickAlarmContext) {
    private var chickAlarmFileChooserHandler: ((ValueCallback<Array<Uri>>?) -> Unit)? = null
    fun chickAlarmSetFileChooserHandler(handler: (ValueCallback<Array<Uri>>?) -> Unit) {
        this.chickAlarmFileChooserHandler = handler
    }
    init {
        val webSettings = settings
        webSettings.apply {
            setSupportMultipleWindows(true)
            allowFileAccess = true
            allowContentAccess = true
            domStorageEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            userAgentString = WebSettings.getDefaultUserAgent(chickAlarmContext).replace("; wv)", "").replace("Version/4.0 ", "")
            @SuppressLint("SetJavaScriptEnabled")
            javaScriptEnabled = true
            cacheMode = WebSettings.LOAD_NO_CACHE
        }
        isNestedScrollingEnabled = true



        layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        super.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?,
            ): Boolean {
                val link = request?.url?.toString() ?: ""

                return if (request?.isRedirect == true) {
                    view?.loadUrl(request?.url.toString())
                    true
                }
                else if (URLUtil.isNetworkUrl(link)) {
                    false
                } else {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    try {
                        chickAlarmContext.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(chickAlarmContext, "This application not found", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
            }


            override fun onPageFinished(view: WebView?, url: String?) {
                CookieManager.getInstance().flush()
                if (url?.contains("ninecasino") == true) {
                    ChickAlarmApplication.chickAlarmInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                    Log.d(ChickAlarmApplication.CHICK_ALARM_MAIN_TAG, "onPageFinished : ${ChickAlarmApplication.chickAlarmInputMode}")
                    chickAlarmWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                } else {
                    ChickAlarmApplication.chickAlarmInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                    Log.d(ChickAlarmApplication.CHICK_ALARM_MAIN_TAG, "onPageFinished : ${ChickAlarmApplication.chickAlarmInputMode}")
                    chickAlarmWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                }
            }


        })

        super.setWebChromeClient(object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest?) {
                request?.grant(request.resources)
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: WebChromeClient.FileChooserParams?,
            ): Boolean {
                chickAlarmFileChooserHandler?.invoke(filePathCallback)
                return true
            }
            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                chickAlarmHandleCreateWebWindowRequest(resultMsg)
                return true
            }
        })
    }


    fun chickAlarmFLoad(link: String) {
        super.loadUrl(link)
    }

    private fun chickAlarmHandleCreateWebWindowRequest(resultMsg: Message?) {
        if (resultMsg == null) return
        if (resultMsg.obj != null && resultMsg.obj is WebView.WebViewTransport) {
            val transport = resultMsg.obj as WebView.WebViewTransport
            val windowWebView = ChickAlarmVi(chickAlarmContext, chickAlarmCallback, chickAlarmWindow)
            transport.webView = windowWebView
            resultMsg.sendToTarget()
            chickAlarmCallback.chickAlarmHandleCreateWebWindowRequest(windowWebView)
        }
    }

}