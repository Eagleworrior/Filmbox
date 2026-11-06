package com.filmbox;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PlayerActivity extends AppCompatActivity {

    private WebView webViewPlayer;
    private boolean isLandscape = true;
    private int tmdbId = 27205;
    private String mediaType = "movie";
    private int seasonNumber = 1;
    private int episodeNumber = 1;
    private String movieTitle = "Movie";

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
        if (urlLower.contains("m3u8") || urlLower.contains(".ts") || urlLower.contains(".mp4") || urlLower.contains("hls") || urlLower.contains("googlevideo") || urlLower.contains("akamaized") || urlLower.contains("vidlink.pro") || urlLower.contains("vidsrc.to") || urlLower.contains("vidsrc.cc") || urlLower.contains("embed.su") || urlLower.contains("tmdb.org") || urlLower.contains("cloudflare.com")) {
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
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            setContentView(R.layout.activity_player);
            hideSystemUI();

            webViewPlayer = findViewById(R.id.webViewPlayer);
            View playerView = findViewById(R.id.playerView);
            if (playerView != null) playerView.setVisibility(View.GONE);
            if (webViewPlayer != null) {
                webViewPlayer.setVisibility(View.VISIBLE);
                setupWebView();
            }

            TextView tvPlayerTitle = findViewById(R.id.tvPlayerTitle);
            ImageButton btnBack = findViewById(R.id.btnBack);
            ImageButton btnRotateScreen = findViewById(R.id.btnRotateScreen);

            if (getIntent() != null) {
                tmdbId = getIntent().getIntExtra("tmdb_id", 27205);
                if (tmdbId <= 0) tmdbId = 27205;
                mediaType = getIntent().getStringExtra("media_type");
                if (mediaType == null) mediaType = "movie";
                seasonNumber = getIntent().getIntExtra("season_number", 1);
                episodeNumber = getIntent().getIntExtra("episode_number", 1);

                movieTitle = getIntent().getStringExtra("title");
                if (movieTitle == null) movieTitle = "Film Box Cinema";
            }

            if (tvPlayerTitle != null) {
                tvPlayerTitle.setText(movieTitle);
            }

            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
            }

            if (btnRotateScreen != null) {
                btnRotateScreen.setOnClickListener(v -> {
                    if (isLandscape) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        isLandscape = false;
                    } else {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                        isLandscape = true;
                    }
                });
            }

            loadMasterServer();
        } catch (Exception e) {
            Toast.makeText(this, "Error starting player: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }

    private void setupWebView() {
        try {
            WebSettings webSettings = webViewPlayer.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setDatabaseEnabled(true);
            webSettings.setLoadsImagesAutomatically(true);
            webSettings.setMediaPlaybackRequiresUserGesture(false);
            webSettings.setAllowFileAccess(true);
            webSettings.setAllowContentAccess(true);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webSettings.setSupportMultipleWindows(false);
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Mobile Safari/537.36");

            android.webkit.CookieManager.getInstance().setAcceptCookie(true);
            android.webkit.CookieManager.getInstance().setAcceptThirdPartyCookies(webViewPlayer, true);

            webViewPlayer.setLayerType(View.LAYER_TYPE_NONE, null);
            webViewPlayer.setDownloadListener(null);

            webViewPlayer.setWebChromeClient(new WebChromeClient() {
                @Override
                public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, android.os.Message resultMsg) {
                    return false;
                }

                @Override
                public void onPermissionRequest(android.webkit.PermissionRequest request) {
                    request.grant(request.getResources());
                }
            });

            webViewPlayer.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    if (request != null && request.getUrl() != null) {
                        String url = request.getUrl().toString();
                        String urlLower = url.toLowerCase();

                        if (urlLower.startsWith("https://embed/") || urlLower.startsWith("http://embed/")) {
                            String fixedUrl = url.replace("https://embed/", "https://vidlink.pro/embed/").replace("http://embed/", "https://vidlink.pro/embed/");
                            view.loadUrl(fixedUrl);
                            return true;
                        }

                        android.net.Uri uri = request.getUrl();
                        String host = uri.getHost();
                        String scheme = uri.getScheme();

                        if (scheme != null && !scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https")) {
                            return true;
                        }

                        if (isAdUrl(urlLower)) {
                            Log.d("PLAYER_ADBLOCK", "Blocked player navigation (Ad Keyword): " + url);
                            return true;
                        }

                        if (host != null && isHostAllowed(host)) {
                            return false;
                        }

                        Log.d("PLAYER_ADBLOCK", "Blocked external player ad redirect (Unallowed Host): " + host);
                        return true;
                    }
                    return false;
                }

                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                    if (request != null && request.getUrl() != null) {
                        String url = request.getUrl().toString();
                        String urlLower = url.toLowerCase();

                        if (isAdUrl(urlLower)) {
                            Log.d("PLAYER_ADBLOCK", "Intercepted & Blocked Sub-resource (Ad Keyword): " + url);
                            return new WebResourceResponse("text/plain", "utf-8", new ByteArrayInputStream(new byte[0]));
                        }
                    }
                    return super.shouldInterceptRequest(view, request);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    try {
                        String unblockScript = "(function() {" +
                                "  var css = '[id*=\"ad-\"],[class*=\"ad-\"],[class*=\"ads\"],[id*=\"ads\"],iframe[src*=\"ads\"],iframe[src*=\"doubleclick\"],ins.adsbygoogle,[class*=\"banner\"],[id*=\"banner\"],[class*=\"popup\"],[id*=\"popup\"],[class*=\"sponsor\"],[id*=\"sponsor\"],[class*=\"overlay\"],div[style*=\"position: fixed\"][style*=\"z-index\"] { display:none!important; pointer-events:none!important; };';" +
                                "  var s = document.createElement(\"style\"); s.innerHTML = css; document.head.appendChild(s);" +
                                "  var observer = new MutationObserver(function(mutations) {" +
                                "    var ads = document.querySelectorAll('iframe[src*=\"ads\"], iframe[src*=\"pop\"], iframe[src*=\"track\"], ins.adsbygoogle');" +
                                "    for (var i = 0; i < ads.length; i++) {" +
                                "      if (ads[i].id !== \"player\" && !ads[i].closest(\"#player\")) { ads[i].remove(); }" +
                                "    }" +
                                "  });" +
                                "  observer.observe(document.documentElement, { childList: true, subtree: true });" +
                                "  window.open = function() { return null; };" +
                                "})();";
                        view.evaluateJavascript(unblockScript, null);
                    } catch (Exception ignored) {}
                }
            });
        } catch (Exception ignored) {}
    }

    private void loadMasterServer() {
        try {
            String playerUrl = "file:///android_asset/player.html?id=" + tmdbId + "&type=" + mediaType + "&s=" + seasonNumber + "&e=" + episodeNumber;

            if (webViewPlayer != null) {
                webViewPlayer.setBackgroundColor(0xFF000000);
                webViewPlayer.loadUrl(playerUrl);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Stream error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (webViewPlayer != null) {
                webViewPlayer.onPause();
            }
        } catch (Exception ignored) {}
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (webViewPlayer != null) {
                webViewPlayer.onResume();
            }
        } catch (Exception ignored) {}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (webViewPlayer != null) {
                webViewPlayer.destroy();
                webViewPlayer = null;
            }
        } catch (Exception ignored) {}
    }
}
