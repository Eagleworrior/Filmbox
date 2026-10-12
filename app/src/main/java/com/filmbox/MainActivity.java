package com.filmbox;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webViewApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);

            webViewApp = findViewById(R.id.webViewApp);
            if (webViewApp != null) {
                WebSettings webSettings = webViewApp.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webSettings.setDomStorageEnabled(true);
                webSettings.setDatabaseEnabled(true);
                webSettings.setLoadsImagesAutomatically(true);
                webSettings.setMediaPlaybackRequiresUserGesture(false);
                webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
                webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
                webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

                webViewApp.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                webViewApp.setWebChromeClient(new WebChromeClient());
                webViewApp.setWebViewClient(new WebViewClient());

                webViewApp.loadUrl("file:///android_asset/index.html");
            }
        } catch (Exception e) {
            Toast.makeText(this, "App error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (webViewApp != null) {
                webViewApp.loadUrl("about:blank");
            }
        } catch (Exception ignored) {}
    }
}
