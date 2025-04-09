package com.example.flighttracker20

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class FlightWebActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flight_web)

        val webView = findViewById<WebView>(R.id.flightWebView)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val titleText = findViewById<TextView>(R.id.flightTitle)

        // Get flight number or fallback
        val flightNumber = intent.getStringExtra("flight_number") ?: "EK215"
        titleText.text = "Tracking: $flightNumber"

        // Set up WebView
        webView.settings.javaScriptEnabled = true
        webView.setLayerType(WebView.LAYER_TYPE_HARDWARE, null)

        // Show progress while loading
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                progressBar.visibility = View.GONE
                webView.visibility = View.VISIBLE
            }
        }

        val url = "https://www.google.com/search?q=${flightNumber}+flight+status"
        webView.loadUrl(url)
    }
}
