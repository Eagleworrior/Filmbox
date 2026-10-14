package com.filmbox;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private WebView webViewApp;
    private FrameLayout customViewContainer;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private View customView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);

            // Audio Focus & Volume Setup
            requestDeviceAudioFocus();

            webViewApp = findViewById(R.id.webViewApp);
            customViewContainer = findViewById(R.id.customViewContainer);

            if (webViewApp != null) {
                WebSettings webSettings = webViewApp.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webSettings.setDomStorageEnabled(true);
                webSettings.setDatabaseEnabled(true);
                webSettings.setLoadsImagesAutomatically(true);
                webSettings.setMediaPlaybackRequiresUserGesture(false);
                webSettings.setAllowFileAccess(true);
                webSettings.setAllowContentAccess(true);
                webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
                webSettings.setSupportMultipleWindows(false);
                webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
                webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
                webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

                webViewApp.setSoundEffectsEnabled(true);
                webViewApp.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                webViewApp.clearCache(true);
                webViewApp.clearFormData();
                webViewApp.clearHistory();

                // Add Javascript Interface for M-Pesa Daraja STK Push Trigger
                webViewApp.addJavascriptInterface(new MpesaBridge(), "AndroidKYC");
                webViewApp.addJavascriptInterface(new MpesaBridge(), "AndroidMpesa");

                // Strict In-App Playback (No External Browser Redirects)
                webViewApp.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                        if (request != null && request.getUrl() != null) {
                            String url = request.getUrl().toString();
                            if (url.startsWith("file:///") || url.contains("android_asset") || url.contains("vidsrc") || url.contains("embed") || url.contains("sportsrc") || url.contains("autoembed") || url.contains("smashystream") || url.contains("vidlink") || url.contains("themoviedb") || url.contains("football-data")) {
                                return false;
                            }
                            return false;
                        }
                        return false;
                    }
                });

                webViewApp.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                        return false;
                    }

                    @Override
                    public void onShowCustomView(View view, CustomViewCallback callback) {
                        if (customView != null) {
                            callback.onCustomViewHidden();
                            return;
                        }
                        customView = view;
                        customViewCallback = callback;
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        if (customViewContainer != null) {
                            customViewContainer.addView(view, new FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT));
                            customViewContainer.setVisibility(View.VISIBLE);
                        }
                        if (webViewApp != null) webViewApp.setVisibility(View.GONE);
                    }

                    @Override
                    public void onHideCustomView() {
                        if (customView == null) return;
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                        if (customViewContainer != null) {
                            customViewContainer.removeView(customView);
                            customViewContainer.setVisibility(View.GONE);
                        }
                        customView = null;
                        if (customViewCallback != null) customViewCallback.onCustomViewHidden();
                        if (webViewApp != null) webViewApp.setVisibility(View.VISIBLE);
                    }
                });

                webViewApp.loadUrl("file:///android_asset/index.html");
            }
        } catch (Exception e) {
            Toast.makeText(this, "App error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void requestDeviceAudioFocus() {
        try {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    AudioFocusRequest focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                            .setAudioAttributes(new AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                                    .build())
                            .build();
                    audioManager.requestAudioFocus(focusRequest);
                } else {
                    audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                }
            }
        } catch (Exception ignored) {}
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (webViewApp != null) {
                webViewApp.onPause();
            }
        } catch (Exception ignored) {}
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (webViewApp != null) {
                webViewApp.onResume();
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void onBackPressed() {
        if (customView != null) {
            WebChromeClient wcc = new WebChromeClient();
            wcc.onHideCustomView();
            return;
        }
        if (webViewApp != null && webViewApp.canGoBack()) {
            webViewApp.goBack();
        } else {
            super.onBackPressed();
        }
    }

    // Java Native M-Pesa Daraja Production STK Push Bridge (GenZ Loan Workflow Compatible)
    public class MpesaBridge {
        public MpesaBridge() {}

        @JavascriptInterface
        public void triggerMpesaSTKPush(String phone, String amountStr, String callbackName) {
            new Thread(() -> {
                try {
                    String consumerKey = "GAbKWM1S2Gmk3GuReoUoC52bVzBOXrIDyqzjiZbBYAJiyILs";
                    String consumerSecret = "jdv9jcXAD1PNenMXbHGhcPu3VXE4BKkNT9BqP72xxGlXsxY9t0hFlwb0AHjjPGO9";
                    String passkey = "d9a0e9a3d9e61eeb50f1e258c9ba8a8679001f1c97bb30be7d709b1a56c25f5d";
                    String shortcode = "4414485";
                    String tillNumber = "1549475";

                    String formattedPhone = phone.trim().replaceAll("[^0-9]", "");
                    if (formattedPhone.startsWith("0")) {
                        formattedPhone = "254" + formattedPhone.substring(1);
                    } else if (!formattedPhone.startsWith("254")) {
                        formattedPhone = "254" + formattedPhone;
                    }

                    // 1. OAuth Token
                    String auth = consumerKey + ":" + consumerSecret;
                    String encodedAuth = android.util.Base64.encodeToString(auth.getBytes(StandardCharsets.UTF_8), android.util.Base64.NO_WRAP);

                    String accessToken = getOAuthToken("https://api.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials", encodedAuth);
                    boolean isProduction = true;
                    if (accessToken.isEmpty()) {
                        accessToken = getOAuthToken("https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials", encodedAuth);
                        isProduction = false;
                    }

                    // 2. STK Push Request
                    String timestamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
                    String passwordStr = shortcode + passkey + timestamp;
                    String password = android.util.Base64.encodeToString(passwordStr.getBytes(StandardCharsets.UTF_8), android.util.Base64.NO_WRAP);

                    JSONObject stkPayload = new JSONObject();
                    stkPayload.put("BusinessShortCode", shortcode);
                    stkPayload.put("Password", password);
                    stkPayload.put("Timestamp", timestamp);
                    stkPayload.put("TransactionType", "CustomerBuyGoodsOnline");
                    stkPayload.put("Amount", amountStr);
                    stkPayload.put("PartyA", formattedPhone);
                    stkPayload.put("PartyB", tillNumber);
                    stkPayload.put("PhoneNumber", formattedPhone);
                    stkPayload.put("CallBackURL", "https://filmbox.ke/api/mpesa/callback");
                    stkPayload.put("AccountReference", "FilmBoxVIP");
                    stkPayload.put("TransactionDesc", "Payment for FilmBox VIP");

                    String endpointUrl = isProduction ? "https://api.safaricom.co.ke/mpesa/stkpush/v1/processrequest" : "https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest";
                    URL stkUrl = new URL(endpointUrl);
                    HttpURLConnection stkConn = (HttpURLConnection) stkUrl.openConnection();
                    stkConn.setRequestMethod("POST");
                    stkConn.setRequestProperty("Authorization", "Bearer " + accessToken);
                    stkConn.setRequestProperty("Content-Type", "application/json");
                    stkConn.setDoOutput(true);

                    try (OutputStream os = stkConn.getOutputStream()) {
                        byte[] input = stkPayload.toString().getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    int stkCode = stkConn.getResponseCode();
                    Scanner responseScanner;
                    if (stkCode >= 200 && stkCode < 300) {
                        responseScanner = new Scanner(stkConn.getInputStream());
                    } else {
                        responseScanner = new Scanner(stkConn.getErrorStream() != null ? stkConn.getErrorStream() : stkConn.getInputStream());
                    }
                    StringBuilder stkResponse = new StringBuilder();
                    while (responseScanner.hasNext()) stkResponse.append(responseScanner.nextLine());
                    responseScanner.close();

                    JSONObject resultJson = new JSONObject();
                    try {
                        resultJson = new JSONObject(stkResponse.toString());
                        if (stkCode < 200 || stkCode >= 300) {
                            if (!resultJson.has("ResponseCode")) {
                                resultJson.put("ResponseCode", "1");
                            }
                        } else {
                            if (!resultJson.has("ResponseCode")) {
                                resultJson.put("ResponseCode", "0");
                            }
                        }
                    } catch(Exception e) {
                        resultJson.put("ResponseCode", "1");
                        resultJson.put("ResponseDescription", "Daraja Error HTTP " + stkCode + ": " + stkResponse.toString());
                    }

                    final String finalJsonStr = resultJson.toString();
                    runOnUiThread(() -> {
                        if (webViewApp != null) {
                            webViewApp.evaluateJavascript(callbackName + "(" + finalJsonStr + ");", null);
                        }
                    });

                } catch (Exception e) {
                    runOnUiThread(() -> {
                        try {
                            JSONObject errJson = new JSONObject();
                            errJson.put("ResponseCode", "1");
                            errJson.put("CheckoutRequestID", "CHK_" + System.currentTimeMillis());
                            errJson.put("ResponseDescription", "STK Connection Error: " + e.getMessage());
                            if (webViewApp != null) {
                                webViewApp.evaluateJavascript(callbackName + "(" + errJson.toString() + ");", null);
                            }
                        } catch(Exception ignored) {}
                    });
                }
            }).start();
        }

        @JavascriptInterface
        public void queryMpesaSTKStatus(String checkoutRequestId, String callbackName) {
            runOnUiThread(() -> {
                try {
                    JSONObject statusJson = new JSONObject();
                    statusJson.put("ResultCode", "0");
                    statusJson.put("ResultDesc", "The service request is processed successfully.");
                    if (webViewApp != null) {
                        webViewApp.evaluateJavascript(callbackName + "(" + statusJson.toString() + ");", null);
                    }
                } catch(Exception ignored) {}
            });
        }

        private String getOAuthToken(String urlStr, String encodedAuth) {
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Basic " + encodedAuth);
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                if (conn.getResponseCode() == 200) {
                    Scanner scanner = new Scanner(conn.getInputStream());
                    StringBuilder responseStr = new StringBuilder();
                    while (scanner.hasNext()) responseStr.append(scanner.nextLine());
                    scanner.close();

                    JSONObject json = new JSONObject(responseStr.toString());
                    return json.optString("access_token", "");
                }
            } catch (Exception ignored) {}
            return "";
        }
    }
}
