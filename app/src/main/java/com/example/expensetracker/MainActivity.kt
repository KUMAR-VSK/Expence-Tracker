package com.example.expensetracker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebViewAssetLoader
import com.example.expensetracker.theme.ExpenseTrackerTheme

class MainActivity : ComponentActivity() {
    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    private val fileChooserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (filePathCallback != null) {
            val intentData = result.data
            val results: Array<Uri>? = if (result.resultCode == RESULT_OK && intentData != null) {
                val dataString = intentData.dataString
                if (dataString != null) {
                    arrayOf(Uri.parse(dataString))
                } else null
            } else null

            filePathCallback?.onReceiveValue(results)
            filePathCallback = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)

        enableEdgeToEdge()
        setContent {
            ExpenseTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AndroidView(
                        factory = { context ->
                            val assetLoader = WebViewAssetLoader.Builder()
                                .addPathHandler("/", WebViewAssetLoader.AssetsPathHandler(context))
                                .build()

                            WebView(context).apply {
                                @Suppress("DEPRECATION")
                                settings.apply {
                                    javaScriptEnabled = true
                                    domStorageEnabled = true
                                    allowFileAccess = false
                                    allowContentAccess = true
                                    allowFileAccessFromFileURLs = false
                                    allowUniversalAccessFromFileURLs = false
                                    loadWithOverviewMode = true
                                    useWideViewPort = true
                                    cacheMode = WebSettings.LOAD_NO_CACHE
                                    mediaPlaybackRequiresUserGesture = false
                                }

                                webViewClient = object : WebViewClient() {
                                    override fun shouldInterceptRequest(
                                        view: WebView?,
                                        request: WebResourceRequest?
                                    ): WebResourceResponse? {
                                        return request?.url?.let { assetLoader.shouldInterceptRequest(it) }
                                    }

                                    override fun onReceivedError(
                                        view: WebView?,
                                        errorCode: Int,
                                        description: String?,
                                        failingUrl: String?
                                    ) {
                                        Log.e("WebViewError", "Error $errorCode: $description on $failingUrl")
                                    }
                                }

                                webChromeClient = object : WebChromeClient() {
                                    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                                        consoleMessage?.let {
                                            val level = when (it.messageLevel()) {
                                                ConsoleMessage.MessageLevel.ERROR -> "ERROR"
                                                ConsoleMessage.MessageLevel.WARNING -> "WARN"
                                                ConsoleMessage.MessageLevel.LOG -> "LOG"
                                                ConsoleMessage.MessageLevel.DEBUG -> "DEBUG"
                                                ConsoleMessage.MessageLevel.TIP -> "TIP"
                                                else -> "UNKNOWN"
                                            }
                                            Log.d("WebViewConsole", "[$level] ${it.message()} -- Line ${it.lineNumber()} of ${it.sourceId()}")
                                        }
                                        return true
                                    }

                                    override fun onShowFileChooser(
                                        webView: WebView?,
                                        filePathCallbackIn: ValueCallback<Array<Uri>>?,
                                        fileChooserParams: FileChooserParams?
                                    ): Boolean {
                                        filePathCallback?.onReceiveValue(null)
                                        filePathCallback = filePathCallbackIn

                                        val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
                                            addCategory(Intent.CATEGORY_OPENABLE)
                                            type = "*/*"
                                        }

                                        val chooserIntent = Intent(Intent.ACTION_CHOOSER).apply {
                                            putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                                            putExtra(Intent.EXTRA_TITLE, "Select Document or Photo")
                                        }

                                        fileChooserLauncher.launch(chooserIntent)
                                        return true
                                    }
                                }

                                loadUrl("https://appassets.androidplatform.net/web/index.html")
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
