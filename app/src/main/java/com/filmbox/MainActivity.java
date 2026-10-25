package com.filmbox;

import android.content.Context;
import android.content.Intent;
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

import org.json.JSONArray;
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
                webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
                webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

                webViewApp.setSoundEffectsEnabled(true);
                webViewApp.setBackgroundColor(0xFF0b0b1a);
                webViewApp.setClickable(true);
                webViewApp.setFocusable(true);
                webViewApp.setFocusableInTouchMode(true);
                webViewApp.requestFocus();
                webViewApp.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                webViewApp.clearCache(true);
                webViewApp.clearFormData();
                webViewApp.clearHistory();

                // Add Javascript Interface for M-Pesa Daraja STK Push & PayPal
                webViewApp.addJavascriptInterface(new MpesaBridge(), "AndroidKYC");
                webViewApp.addJavascriptInterface(new MpesaBridge(), "AndroidMpesa");
                webViewApp.addJavascriptInterface(new PayPalBridge(), "AndroidPayPal");

                // Strict In-App Playback, Host Whitelisting & Network-Level Ad Interception
                webViewApp.setWebViewClient(new WebViewClient() {
                    private final java.util.Set<String> allowedHosts = new java.util.HashSet<>(java.util.Arrays.asList(
                            "api.themoviedb.org",
                            "image.tmdb.org",
                            "cdn.jsdelivr.net",
                            "api.football-data.org",
                            "vidsrc.to",
                            "vidlink.pro",
                            "vidsrc.pro",
                            "embed.su",
                            "player.autoembed.cc",
                            "player.smashystream.xyz",
                            "api.sportsrc.org",
                            "v2.sportsrc.com",
                            "newsdata.io",
                            "filmboxproductions.blogspot.com",
                            "blogspot.com"
                    ));

                    private boolean isAllowed(String host) {
                        if (host == null) return true;
                        for (String allowed : allowedHosts) {
                            if (host.equals(allowed) || host.endsWith("." + allowed)) {
                                return true;
                            }
                        }
                        return false;
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                        if (request != null && request.getUrl() != null) {
                            android.net.Uri uri = request.getUrl();
                            String host = uri.getHost();
                            String url = uri.toString();

                            if (url.contains("whatsapp.com") || url.startsWith("whatsapp://")) {
                                try {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    view.getContext().startActivity(intent);
                                    return true;
                                } catch (Exception e) {
                                    Toast.makeText(view.getContext(), "WhatsApp app not found", Toast.LENGTH_SHORT).show();
                                    return true;
                                }
                            }

                            if (url.contains("paypal.com") || url.contains("paypal.me")) {
                                try {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    view.getContext().startActivity(intent);
                                    return true;
                                } catch (Exception e) {
                                    return false;
                                }
                            }

                            if (url.contains("facebook.com") || url.contains("fb.me") || url.contains("blogspot.com")) {
                                try {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    view.getContext().startActivity(intent);
                                    return true;
                                } catch (Exception e) {
                                    return false;
                                }
                            }

                            if (url.startsWith("file:///") || url.contains("android_asset") || isAllowed(host)) {
                                return false;
                            }
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public android.webkit.WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                        if (request != null && request.getUrl() != null) {
                            String url = request.getUrl().toString().toLowerCase();

                            if (url.contains("doubleclick") || url.contains("googlesyndication") || url.contains("googleadservices") || 
                                url.contains("adservice") || url.contains("adnxs") || url.contains("outbrain") || url.contains("taboola") || 
                                url.contains("analytics") || url.contains("tracker") || url.contains("popads") || url.contains("popcash") || 
                                url.contains("propeller") || url.contains("adsterra") || url.contains("exoclick") || url.contains("juicyads") || 
                                url.contains("alwingulla") || url.contains("hilltopads") || url.contains("trafficjunky") || url.contains("clickadu") ||
                                url.contains("bet365") || url.contains("1xbet") || url.contains("ads.")) {
                                return new android.webkit.WebResourceResponse("text/plain", "utf-8", new java.io.ByteArrayInputStream(new byte[0]));
                            }
                        }
                        return super.shouldInterceptRequest(view, request);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        view.evaluateJavascript(
                                "(function() {" +
                                        "  var css = '[id*=\"ad-\"],[class*=\"ad-\"],[class*=\"ads\"],[id*=\"ads\"],iframe[src*=\"ads\"],iframe[src*=\"doubleclick\"],ins.adsbygoogle{display:none!important};" +
                                        "  var s = document.createElement(\"style\"); s.innerHTML = css; document.head.appendChild(s);" +
                                        "  var observer = new MutationObserver(function(mutations) {" +
                                        "    var ads = document.querySelectorAll('iframe[src*=\"ads\"], iframe[src*=\"pop\"], iframe[src*=\"track\"], div[style*=\"z-index: 2147483647\"], div[id*=\"pop\"], ins.adsbygoogle');" +
                                        "    for (var i = 0; i < ads.length; i++) { ads[i].remove(); }" +
                                        "  });" +
                                        "  observer.observe(document.documentElement, { childList: true, subtree: true });" +
                                        "  window.open = function() { return null; };" +
                                        "})();", null
                        );
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
        if (webViewApp != null) {
            webViewApp.evaluateJavascript("window.handleDeviceBack ? window.handleDeviceBack() : false;", result -> {
                if (result != null && result.equalsIgnoreCase("true")) {
                    return;
                } else {
                    runOnUiThread(() -> {
                        if (webViewApp.canGoBack()) {
                            webViewApp.goBack();
                        } else {
                            finish();
                        }
                    });
                }
            });
        } else {
            super.onBackPressed();
        }
    }

    // Java Native M-Pesa Daraja Production STK Push Bridge (GenZ Loan Workflow Compatible)
    public class MpesaBridge {
        public MpesaBridge() {}

        @JavascriptInterface
        public void downloadMediaFile(String fileUrl, String fileName) {
            runOnUiThread(() -> {
                try {
                    String cleanName = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
                    android.app.DownloadManager.Request request = new android.app.DownloadManager.Request(android.net.Uri.parse(fileUrl));
                    request.setTitle("Downloading " + fileName);
                    request.setDescription("Movie Box offline download in progress...");
                    request.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(android.os.Environment.DIRECTORY_DOWNLOADS, cleanName + ".mp4");

                    android.app.DownloadManager downloadManager = (android.app.DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    if (downloadManager != null) {
                        downloadManager.enqueue(request);
                        Toast.makeText(MainActivity.this, "📥 Real File Download Started: " + fileName, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Download error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

        @JavascriptInterface
        public void setFullscreenMode(boolean enabled) {
            runOnUiThread(() -> {
                try {
                    if (enabled) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            if (getWindow().getInsetsController() != null) {
                                getWindow().getInsetsController().hide(android.view.WindowInsets.Type.statusBars() | android.view.WindowInsets.Type.navigationBars());
                                getWindow().getInsetsController().setSystemBarsBehavior(android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                            }
                        } else {
                            getWindow().getDecorView().setSystemUiVisibility(
                                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            );
                        }
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            if (getWindow().getInsetsController() != null) {
                                getWindow().getInsetsController().show(android.view.WindowInsets.Type.statusBars() | android.view.WindowInsets.Type.navigationBars());
                            }
                        } else {
                            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                        }
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    }
                } catch (Exception ignored) {}
            });
        }

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
                    stkConn.setConnectTimeout(3000);
                    stkConn.setReadTimeout(3000);
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
            new Thread(() -> {
                try {
                    String consumerKey = "GAbKWM1S2Gmk3GuReoUoC52bVzBOXrIDyqzjiZbBYAJiyILs";
                    String consumerSecret = "jdv9jcXAD1PNenMXbHGhcPu3VXE4BKkNT9BqP72xxGlXsxY9t0hFlwb0AHjjPGO9";
                    String passkey = "d9a0e9a3d9e61eeb50f1e258c9ba8a8679001f1c97bb30be7d709b1a56c25f5d";
                    String shortcode = "4414485";

                    String auth = consumerKey + ":" + consumerSecret;
                    String encodedAuth = android.util.Base64.encodeToString(auth.getBytes(StandardCharsets.UTF_8), android.util.Base64.NO_WRAP);
                    String accessToken = getOAuthToken("https://api.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials", encodedAuth);

                    String timestamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
                    String passwordStr = shortcode + passkey + timestamp;
                    String password = android.util.Base64.encodeToString(passwordStr.getBytes(StandardCharsets.UTF_8), android.util.Base64.NO_WRAP);

                    JSONObject queryPayload = new JSONObject();
                    queryPayload.put("BusinessShortCode", shortcode);
                    queryPayload.put("Password", password);
                    queryPayload.put("Timestamp", timestamp);
                    queryPayload.put("CheckoutRequestID", checkoutRequestId);

                    URL queryUrl = new URL("https://api.safaricom.co.ke/mpesa/stkpushquery/v1/query");
                    HttpURLConnection queryConn = (HttpURLConnection) queryUrl.openConnection();
                    queryConn.setConnectTimeout(3000);
                    queryConn.setReadTimeout(3000);
                    queryConn.setRequestMethod("POST");
                    queryConn.setRequestProperty("Authorization", "Bearer " + accessToken);
                    queryConn.setRequestProperty("Content-Type", "application/json");
                    queryConn.setDoOutput(true);

                    try (OutputStream os = queryConn.getOutputStream()) {
                        byte[] input = queryPayload.toString().getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    int queryCode = queryConn.getResponseCode();
                    Scanner scanner;
                    if (queryCode >= 200 && queryCode < 300) {
                        scanner = new Scanner(queryConn.getInputStream());
                    } else {
                        scanner = new Scanner(queryConn.getErrorStream() != null ? queryConn.getErrorStream() : queryConn.getInputStream());
                    }
                    StringBuilder responseStr = new StringBuilder();
                    while (scanner.hasNext()) responseStr.append(scanner.nextLine());
                    scanner.close();

                    JSONObject resultJson = new JSONObject(responseStr.toString());
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
                            errJson.put("ResultCode", "1037");
                            errJson.put("ResultDesc", "STK Query pending.");
                            if (webViewApp != null) {
                                webViewApp.evaluateJavascript(callbackName + "(" + errJson.toString() + ");", null);
                            }
                        } catch(Exception ignored) {}
                    });
                }
            }).start();
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

    // Java Native PayPal REST API Bridge
    public class PayPalBridge {
        public PayPalBridge() {}

        private final String clientId = "BAA1dtlx0lmxerhVWLeU2FXAPXyAoQyFzrGWJIc_djIp8oT3y54X1sLX2GR1XWZnloh7yqTc4j57vB-bKQ";
        private final String clientSecret = "EIhtPvussfV5KhcSTIPj7gs2jcOeoQ6G6KW3uFE8YH9wqfFg8hu-m9YU5lBsS7pRdd2ZTR5lhnF5I-Hj";

        @JavascriptInterface
        public void createPayPalOrder(String amountUsdStr, String callbackName) {
            new Thread(() -> {
                try {
                    String auth = clientId + ":" + clientSecret;
                    String encodedAuth = android.util.Base64.encodeToString(auth.getBytes(StandardCharsets.UTF_8), android.util.Base64.NO_WRAP);

                    // 1. Get Access Token
                    URL tokenUrl = new URL("https://api-m.paypal.com/v1/oauth2/token");
                    HttpURLConnection tokenConn = (HttpURLConnection) tokenUrl.openConnection();
                    tokenConn.setRequestMethod("POST");
                    tokenConn.setRequestProperty("Authorization", "Basic " + encodedAuth);
                    tokenConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    tokenConn.setDoOutput(true);

                    try (OutputStream os = tokenConn.getOutputStream()) {
                        byte[] input = "grant_type=client_credentials".getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    Scanner scanner = new Scanner(tokenConn.getInputStream());
                    StringBuilder tokenRes = new StringBuilder();
                    while (scanner.hasNext()) tokenRes.append(scanner.nextLine());
                    scanner.close();

                    JSONObject tokenJson = new JSONObject(tokenRes.toString());
                    String accessToken = tokenJson.getString("access_token");

                    // 2. Create Order
                    JSONObject orderPayload = new JSONObject();
                    orderPayload.put("intent", "CAPTURE");

                    JSONArray purchaseUnits = new JSONArray();
                    JSONObject unit = new JSONObject();
                    JSONObject amount = new JSONObject();
                    amount.put("currency_code", "USD");
                    amount.put("value", amountUsdStr);
                    unit.put("amount", amount);
                    purchaseUnits.put(unit);

                    orderPayload.put("purchase_units", purchaseUnits);

                    URL orderUrl = new URL("https://api-m.paypal.com/v2/checkout/orders");
                    HttpURLConnection orderConn = (HttpURLConnection) orderUrl.openConnection();
                    orderConn.setRequestMethod("POST");
                    orderConn.setRequestProperty("Authorization", "Bearer " + accessToken);
                    orderConn.setRequestProperty("Content-Type", "application/json");
                    orderConn.setDoOutput(true);

                    try (OutputStream os = orderConn.getOutputStream()) {
                        byte[] input = orderPayload.toString().getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    Scanner orderScanner = new Scanner(orderConn.getInputStream());
                    StringBuilder orderRes = new StringBuilder();
                    while (orderScanner.hasNext()) orderRes.append(orderScanner.nextLine());
                    orderScanner.close();

                    final String finalResStr = orderRes.toString();
                    runOnUiThread(() -> {
                        if (webViewApp != null) {
                            webViewApp.evaluateJavascript(callbackName + "(" + finalResStr + ");", null);
                        }
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        try {
                            JSONObject err = new JSONObject();
                            err.put("error", e.getMessage());
                            if (webViewApp != null) {
                                webViewApp.evaluateJavascript(callbackName + "(" + err.toString() + ");", null);
                            }
                        } catch (Exception ignored) {}
                    });
                }
            }).start();
        }

        @JavascriptInterface
        public void capturePayPalOrder(String orderId, String callbackName) {
            new Thread(() -> {
                try {
                    String auth = clientId + ":" + clientSecret;
                    String encodedAuth = android.util.Base64.encodeToString(auth.getBytes(StandardCharsets.UTF_8), android.util.Base64.NO_WRAP);

                    // 1. Token
                    URL tokenUrl = new URL("https://api-m.paypal.com/v1/oauth2/token");
                    HttpURLConnection tokenConn = (HttpURLConnection) tokenUrl.openConnection();
                    tokenConn.setRequestMethod("POST");
                    tokenConn.setRequestProperty("Authorization", "Basic " + encodedAuth);
                    tokenConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    tokenConn.setDoOutput(true);

                    try (OutputStream os = tokenConn.getOutputStream()) {
                        byte[] input = "grant_type=client_credentials".getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    Scanner scanner = new Scanner(tokenConn.getInputStream());
                    StringBuilder tokenRes = new StringBuilder();
                    while (scanner.hasNext()) tokenRes.append(scanner.nextLine());
                    scanner.close();

                    JSONObject tokenJson = new JSONObject(tokenRes.toString());
                    String accessToken = tokenJson.getString("access_token");

                    // 2. Capture Order
                    URL captureUrl = new URL("https://api-m.paypal.com/v2/checkout/orders/" + orderId + "/capture");
                    HttpURLConnection captureConn = (HttpURLConnection) captureUrl.openConnection();
                    captureConn.setConnectTimeout(5000);
                    captureConn.setReadTimeout(5000);
                    captureConn.setRequestMethod("POST");
                    captureConn.setRequestProperty("Authorization", "Bearer " + accessToken);
                    captureConn.setRequestProperty("Content-Type", "application/json");
                    captureConn.setDoOutput(true);

                    try (OutputStream os = captureConn.getOutputStream()) {
                        byte[] input = "{}".getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    int captureCode = captureConn.getResponseCode();
                    Scanner captureScanner;
                    if (captureCode >= 200 && captureCode < 300) {
                        captureScanner = new Scanner(captureConn.getInputStream());
                    } else {
                        captureScanner = new Scanner(captureConn.getErrorStream() != null ? captureConn.getErrorStream() : captureConn.getInputStream());
                    }
                    StringBuilder captureRes = new StringBuilder();
                    while (captureScanner.hasNext()) captureRes.append(captureScanner.nextLine());
                    captureScanner.close();

                    final String finalResStr = captureRes.toString();
                    runOnUiThread(() -> {
                        if (webViewApp != null) {
                            webViewApp.evaluateJavascript(callbackName + "(" + finalResStr + ");", null);
                        }
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        try {
                            JSONObject err = new JSONObject();
                            err.put("error", e.getMessage());
                            if (webViewApp != null) {
                                webViewApp.evaluateJavascript(callbackName + "(" + err.toString() + ");", null);
                            }
                        } catch (Exception ignored) {}
                    });
                }
            }).start();
        }
    }
}
