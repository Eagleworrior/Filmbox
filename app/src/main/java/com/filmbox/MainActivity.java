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
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private WebView webViewApp;
    private FrameLayout customViewContainer;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private View customView;

    private static final Set<String> ALLOWED_HOSTS = new HashSet<>(Arrays.asList(
            "filmbox.ke",
            "blogspot.com",
            "themoviedb.org",
            "tmdb.org",
            "vidsrc.cc",
            "vidsrc.to",
            "vidsrc.net",
            "vidsrc.stream",
            "vidsrc.me",
            "vidsrc.pro",
            "vidsrc.in",
            "vidsrc.pm",
            "vidsrc.xyz",
            "vidsrc.vip",
            "vidlink.pro",
            "embed.su",
            "autoembed.cc",
            "smashystream.xyz",
            "2embed.cc",
            "multiembed.mov",
            "bunnycdn.com",
            "cloudflare.com",
            "cloudflare-ipfs.com",
            "jsdelivr.net",
            "googlevideo.com",
            "akamaized.net",
            "api.football-data.org",
            "api.sportsrc.org",
            "v2.sportsrc.com",
            "newsdata.io",
            "youtube.com",
            "youtu.be",
            "instagram.com",
            "facebook.com",
            "fb.me",
            "paypal.com",
            "paypalobjects.com",
            "safaricom.co.ke",
            "googleapis.com",
            "google.com",
            "gstatic.com",
            "unpkg.com",
            "cdnjs.com"
    ));

    private static final String[] AD_KEYWORDS = {
            "doubleclick", "googlesyndication", "adservice", "popads", "propellerads",
            "adsterra", "exoclick", "onclickads", "taboola", "outbrain", "adserver",
            "banner", "popunder", "monetag", "clickadu", "tsyndicate", "adtelligent",
            "pubmatic", "rubiconproject", "openx", "amazon-adsystem", "popcash",
            "hilltopads", "clickaine", "adcash", "yandex.rtb", "exdynsrv", "bet365",
            "1xbet", "mostbet", "parimatch", "/popads/", "/adsterra/", "/exoclick/",
            "/propeller/", "/popunder/", "/adserver/"
    };

    private boolean isHostAllowed(String host) {
        if (host == null || host.isEmpty()) return true;
        if (host.equalsIgnoreCase("android_asset") || host.equalsIgnoreCase("localhost")) return true;
        String hostLower = host.toLowerCase();
        for (String allowed : ALLOWED_HOSTS) {
            if (hostLower.equals(allowed) || hostLower.endsWith("." + allowed)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAdUrl(String urlLower) {
        if (urlLower.contains("m3u8") || urlLower.contains(".ts") || urlLower.contains(".mp4") || urlLower.contains("hls") || urlLower.contains("googlevideo") || urlLower.contains("akamaized")) {
            return false;
        }
        for (String keyword : AD_KEYWORDS) {
            if (urlLower.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

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
                webSettings.setAllowFileAccess(false);
                webSettings.setAllowContentAccess(false);
                webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
                webSettings.setSupportMultipleWindows(false);
                webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
                webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Mobile Safari/537.36");

                android.webkit.CookieManager.getInstance().setAcceptCookie(true);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    android.webkit.CookieManager.getInstance().setAcceptThirdPartyCookies(webViewApp, true);
                }

                webViewApp.setSoundEffectsEnabled(true);
                webViewApp.setBackgroundColor(0xFF0b0b1a);
                webViewApp.setClickable(true);
                webViewApp.setFocusable(true);
                webViewApp.setFocusableInTouchMode(true);
                webViewApp.requestFocus();
                webViewApp.setLayerType(View.LAYER_TYPE_NONE, null);
                webViewApp.setDownloadListener(null);

                // Add Javascript Interface for M-Pesa Daraja STK Push, PayPal, & Private In-App Offline Downloads
                webViewApp.addJavascriptInterface(new MpesaBridge(), "AndroidKYC");
                webViewApp.addJavascriptInterface(new MpesaBridge(), "AndroidMpesa");
                webViewApp.addJavascriptInterface(new PayPalBridge(), "AndroidPayPal");
                webViewApp.addJavascriptInterface(new WebAppInterface(this), "AndroidApp");

                // Strict In-App Playback, Host Whitelisting & Network-Level Ad Interception
                webViewApp.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                        if (request != null && request.getUrl() != null) {
                            String url = request.getUrl().toString();
                            String urlLower = url.toLowerCase();

                            if (urlLower.startsWith("https://embed/") || urlLower.startsWith("http://embed/")) {
                                String fixedUrl = url.replace("https://embed/", "https://vidsrc.to/embed/").replace("http://embed/", "https://vidsrc.to/embed/");
                                view.loadUrl(fixedUrl);
                                return true;
                            }

                            android.net.Uri uri = request.getUrl();
                            String host = uri.getHost();
                            String scheme = uri.getScheme();

                            if (urlLower.contains("whatsapp.com") || urlLower.startsWith("whatsapp://")) {
                                try {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    view.getContext().startActivity(intent);
                                    return true;
                                } catch (Exception e) {
                                    return true;
                                }
                            }

                            if (scheme != null && !scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https")) {
                                android.util.Log.d("ADBLOCK", "Blocked non-HTTP scheme navigation: " + url);
                                return true;
                            }

                            if (isAdUrl(urlLower)) {
                                android.util.Log.d("ADBLOCK", "Blocked navigation (Ad Keyword): " + url);
                                return true;
                            }

                            if (host != null && !isHostAllowed(host)) {
                                android.util.Log.d("ADBLOCK", "Blocked navigation (Host Not Allowed): " + host + " (URL: " + url + ")");
                                return true;
                            }

                            return false;
                        }
                        return false;
                    }

                    @Override
                    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                        if (request != null && request.getUrl() != null) {
                            String url = request.getUrl().toString();
                            String urlLower = url.toLowerCase();

                            if (isAdUrl(urlLower)) {
                                android.util.Log.d("ADBLOCK", "Intercepted & Blocked Sub-resource (Ad Keyword): " + url);
                                return new WebResourceResponse("text/plain", "utf-8", new ByteArrayInputStream(new byte[0]));
                            }

                            if (urlLower.startsWith("file://") || urlLower.startsWith("data:") || urlLower.startsWith("blob:") || urlLower.startsWith("https://embed/") || urlLower.startsWith("http://embed/") || urlLower.contains("m3u8") || urlLower.contains("ts") || urlLower.contains("mp4") || urlLower.contains("hls")) {
                                return super.shouldInterceptRequest(view, request);
                            }

                            android.net.Uri uri = request.getUrl();
                            String host = uri.getHost();

                            if (host != null && !isHostAllowed(host)) {
                                android.util.Log.d("ADBLOCK", "Intercepted & Blocked Sub-resource (Host Not Allowed): " + host + " (URL: " + url + ")");
                                return new WebResourceResponse("text/plain", "utf-8", new ByteArrayInputStream(new byte[0]));
                            }
                        }
                        return super.shouldInterceptRequest(view, request);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        view.evaluateJavascript(
                                "(function() {" +
                                        "  var css = '[id*=\"ad-\"],[class*=\"ad-\"],[class*=\"ads\"],[id*=\"ads\"],iframe[src*=\"ads\"],iframe[src*=\"doubleclick\"],ins.adsbygoogle,[class*=\"banner\"],[id*=\"banner\"],[class*=\"popup\"],[id*=\"popup\"],[class*=\"sponsor\"],[id*=\"sponsor\"],[class*=\"overlay\"],div[style*=\"position: fixed\"][style*=\"z-index\"] { display:none!important; pointer-events:none!important; };';" +
                                        "  var s = document.createElement(\"style\"); s.innerHTML = css; document.head.appendChild(s);" +
                                        "  var observer = new MutationObserver(function(mutations) {" +
                                        "    var ads = document.querySelectorAll('iframe[src*=\"ads\"], iframe[src*=\"pop\"], iframe[src*=\"track\"], iframe[src*=\"analytics\"], iframe[src*=\"stat\"], div[style*=\"z-index: 2147483647\"], div[id*=\"pop\"], ins.adsbygoogle');" +
                                        "    for (var i = 0; i < ads.length; i++) {" +
                                        "      if (ads[i].id !== \"player\" && !ads[i].closest(\"#player\") && !ads[i].closest(\"#modal\")) { ads[i].remove(); }" +
                                        "    }" +
                                        "  });" +
                                        "  observer.observe(document.documentElement, { childList: true, subtree: true });" +
                                        "  window.open = function() { return null; };" +
                                        "  window.alert = function() { return true; };" +
                                        "  window.confirm = function() { return true; };" +
                                        "  var origFetch = window.fetch;" +
                                        "  window.fetch = function(url, init) {" +
                                        "    if (typeof url === 'string' && (url.includes('ad') || url.includes('pop') || url.includes('track') || url.includes('stat') || url.includes('analytics'))) {" +
                                        "      return Promise.resolve(new Response('', {status: 204}));" +
                                        "    }" +
                                        "    return origFetch.apply(this, arguments);" +
                                        "  };" +
                                        "  var origCreate = document.createElement;" +
                                        "  document.createElement = function(tag) {" +
                                        "    var el = origCreate.call(document, tag);" +
                                        "    if (tag.toLowerCase() === 'script') {" +
                                        "      el.setAttribute = function(name, val) {" +
                                        "        if (name === 'src' && (val.includes('ad') || val.includes('pop') || val.includes('track') || val.includes('stat'))) {" +
                                        "          return;" +
                                        "        }" +
                                        "        HTMLScriptElement.prototype.setAttribute.call(this, name, val);" +
                                        "      };" +
                                        "    }" +
                                        "    return el;" +
                                        "  };" +
                                        "  document.addEventListener('click', function(e) {" +
                                        "    var t = e.target.closest('a');" +
                                        "    if (t && t.href && (t.href.includes('http') && !t.href.includes('filmbox') && !t.href.includes('vidsrc') && !t.href.includes('vidlink') && !t.href.includes('embed') && !t.href.includes('tmdb'))) {" +
                                        "      e.preventDefault(); e.stopPropagation();" +
                                        "    }" +
                                        "  }, true);" +
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
                    public boolean onJsAlert(WebView view, String url, String message, android.webkit.JsResult result) {
                        result.confirm();
                        return true;
                    }

                    @Override
                    public boolean onJsConfirm(WebView view, String url, String message, android.webkit.JsResult result) {
                        result.confirm();
                        return true;
                    }

                    @Override
                    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, android.webkit.JsPromptResult result) {
                        result.confirm("");
                        return true;
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

    private String getOAuthToken(String tokenUrl, String encodedAuth) {
        try {
            URL url = new URL(tokenUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Basic " + encodedAuth);

            int responseCode = conn.getResponseCode();
            Scanner scanner;
            if (responseCode >= 200 && responseCode < 300) {
                scanner = new Scanner(conn.getInputStream());
            } else {
                scanner = new Scanner(conn.getErrorStream() != null ? conn.getErrorStream() : conn.getInputStream());
            }
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNext()) sb.append(scanner.nextLine());
            scanner.close();

            android.util.Log.d("MPESA", "OAuth Token Response Code: " + responseCode + " Body: " + sb.toString());

            if (responseCode >= 200 && responseCode < 300) {
                JSONObject json = new JSONObject(sb.toString());
                return json.optString("access_token", "");
            }
        } catch (Exception e) {
            android.util.Log.e("MPESA", "getOAuthToken error: " + e.getMessage(), e);
        }
        return "";
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
    protected void onDestroy() {
        super.onDestroy();
        try {
            TorrentDownloadManager.getInstance().stop();
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

    // Java Native M-Pesa Daraja Production STK Push Bridge
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

                    String formattedPhone = phone.trim().replaceAll("[^0-9]", "");
                    if (formattedPhone.startsWith("0")) {
                        formattedPhone = "254" + formattedPhone.substring(1);
                    } else if (formattedPhone.startsWith("7") || formattedPhone.startsWith("1")) {
                        formattedPhone = "254" + formattedPhone;
                    } else if (formattedPhone.startsWith("+254")) {
                        formattedPhone = formattedPhone.substring(1);
                    }

                    int parsedAmt = 0;
                    if (amountStr != null && !amountStr.trim().isEmpty()) {
                        String clean = amountStr.replaceAll("[^0-9]", "");
                        if (!clean.isEmpty()) {
                            try {
                                parsedAmt = Integer.parseInt(clean);
                            } catch (Exception ignored) {}
                        }
                    }

                    android.util.Log.d("MPESA", "VIP Offer Payment -> raw amountStr: '" + amountStr + "', parsed finalAmount: " + parsedAmt);

                    if (parsedAmt < 1) {
                        android.util.Log.e("MPESA", "Invalid amount parsed: " + parsedAmt + ". STK Push cancelled.");
                        runOnUiThread(() -> {
                            try {
                                JSONObject errJson = new JSONObject();
                                errJson.put("ResponseCode", "1");
                                errJson.put("ResponseDescription", "Invalid amount, please reselect your offer.");
                                if (webViewApp != null) {
                                    webViewApp.evaluateJavascript(callbackName + "(" + errJson.toString() + ");", null);
                                }
                            } catch (Exception ignored) {}
                        });
                        return;
                    }

                    String finalAmount = String.valueOf(parsedAmt);

                    // 1. OAuth Token
                    String auth = consumerKey + ":" + consumerSecret;
                    String encodedAuth = android.util.Base64.encodeToString(auth.getBytes(StandardCharsets.UTF_8), android.util.Base64.NO_WRAP);

                    String accessToken = getOAuthToken("https://api.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials", encodedAuth);
                    android.util.Log.d("MPESA", "Token Response: " + accessToken);

                    boolean isProduction = true;
                    if (accessToken.isEmpty()) {
                        accessToken = getOAuthToken("https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials", encodedAuth);
                        isProduction = false;
                    }

                    // 2. Execute STK Push
                    JSONObject resultJson = sendStkRequest(accessToken, isProduction, passkey, "4414485", "1549475", "CustomerBuyGoodsOnline", formattedPhone, finalAmount);

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

        private JSONObject sendStkRequest(String accessToken, boolean isProduction, String passkey, String shortCode, String partyB, String transType, String phone, String amount) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("Africa/Nairobi"));
                String timestamp = sdf.format(new Date());

                String passwordStr = shortCode + passkey + timestamp;
                String password = android.util.Base64.encodeToString(passwordStr.getBytes(StandardCharsets.UTF_8), android.util.Base64.NO_WRAP);

                JSONObject stkPayload = new JSONObject();
                stkPayload.put("BusinessShortCode", shortCode);
                stkPayload.put("Password", password);
                stkPayload.put("Timestamp", timestamp);
                stkPayload.put("TransactionType", transType);
                stkPayload.put("Amount", amount);
                stkPayload.put("PartyA", phone);
                stkPayload.put("PartyB", partyB);
                stkPayload.put("PhoneNumber", phone);
                stkPayload.put("CallBackURL", "https://filmbox.ke/mpesa/callback");
                stkPayload.put("AccountReference", "FilmBoxVIP");
                stkPayload.put("TransactionDesc", "Payment for FilmBox VIP");

                android.util.Log.d("MPESA", "STK Request Body (" + transType + "): " + stkPayload.toString());

                String endpointUrl = isProduction ? "https://api.safaricom.co.ke/mpesa/stkpush/v1/processrequest" : "https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest";
                URL stkUrl = new URL(endpointUrl);
                HttpURLConnection stkConn = (HttpURLConnection) stkUrl.openConnection();
                stkConn.setConnectTimeout(15000);
                stkConn.setReadTimeout(15000);
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

                android.util.Log.d("MPESA", "STK Response Code (" + transType + "): " + stkCode + " Body: " + stkResponse.toString());

                JSONObject resultJson = new JSONObject(stkResponse.toString());
                if (stkCode < 200 || stkCode >= 300) {
                    if (!resultJson.has("ResponseCode")) resultJson.put("ResponseCode", "1");
                } else {
                    if (!resultJson.has("ResponseCode")) resultJson.put("ResponseCode", "0");
                }
                return resultJson;
            } catch (Exception e) {
                android.util.Log.e("MPESA", "sendStkRequest error (" + transType + "): " + e.getMessage(), e);
                JSONObject err = new JSONObject();
                try {
                    err.put("ResponseCode", "1");
                    err.put("ResponseDescription", e.getMessage());
                } catch (Exception ignored) {}
                return err;
            }
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

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
                    sdf.setTimeZone(TimeZone.getTimeZone("Africa/Nairobi"));
                    String timestamp = sdf.format(new Date());

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
    }

    public void evaluateJavaScriptOnWebView(String script) {
        if (webViewApp != null) {
            runOnUiThread(() -> webViewApp.evaluateJavascript(script, null));
        }
    }

    // Java Native PayPal Checkout Bridge
    public class PayPalBridge {
        public PayPalBridge() {}

        @JavascriptInterface
        public void openPayPalUrl(String url) {
            try {
                if (url != null && !url.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            } catch (Exception e) {
                android.util.Log.e("PayPalBridge", "Error opening PayPal URL: " + e.getMessage());
            }
        }
    }
}
