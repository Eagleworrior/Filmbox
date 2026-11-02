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
            "vidsrc.to", "vidsrc.net", "vidsrc.pro", "vidsrc.cc", "vidsrc.me", "vidsrc.stream", "vidsrc.in", "vidsrc.pm",
            "vidlink.pro", "embed.su", "autoembed.cc", "2embed.cc", "smashystream.xyz", "multiembed.mov",
            "bunnycdn.com", "cloudflare.com", "googlevideo.com", "akamaized.net", "jsdelivr.net", "googleapis.com"
    ));

    private static final String[] AD_KEYWORDS = {
            "doubleclick", "googlesyndication", "adservice", "popads", "propellerads",
            "adsterra", "exoclick", "onclickads", "taboola", "outbrain", "adserver",
            "banner", "popunder", "tracker", "click", "redirect"
    };

    private boolean isHostAllowed(String host) {
        if (host == null || host.isEmpty()) return true;
        if (host.equalsIgnoreCase("android_asset") || host.equalsIgnoreCase("localhost")) return true;
        String hostLower = host.toLowerCase();
        if (hostLower.contains("vidsrc") || hostLower.contains("vidlink") || hostLower.contains("embed") || hostLower.contains("autoembed") || hostLower.contains("bunnycdn") || hostLower.contains("cloudflare") || hostLower.contains("googlevideo") || hostLower.contains("akamaized") || hostLower.contains("jsdelivr") || hostLower.contains("2embed") || hostLower.contains("stream") || hostLower.contains("player") || hostLower.contains("rcp") || hostLower.contains("cloud") || hostLower.contains("m3u8") || hostLower.contains("ts") || hostLower.contains("mp4") || hostLower.contains("hls")) {
            return true;
        }
        for (String allowed : ALLOWED_HOSTS) {
            if (host.equalsIgnoreCase(allowed) || hostLower.endsWith("." + allowed.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private boolean isAdUrl(String urlLower) {
        if (urlLower.contains("vidsrc") || urlLower.contains("vidlink") || urlLower.contains("embed") || urlLower.contains("autoembed") || urlLower.contains("bunnycdn") || urlLower.contains("cloudflare") || urlLower.contains("googlevideo") || urlLower.contains("akamaized") || urlLower.contains("jsdelivr") || urlLower.contains("2embed") || urlLower.contains("stream") || urlLower.contains("player") || urlLower.contains("rcp") || urlLower.contains("cloud") || urlLower.contains("m3u8") || urlLower.contains("ts") || urlLower.contains("mp4") || urlLower.contains("hls")) {
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
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            hideSystemUI();

            setContentView(R.layout.activity_player);

            webViewPlayer = findViewById(R.id.webViewPlayer);
            if (webViewPlayer != null) {
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
                if (movieTitle == null) movieTitle = "Kenya Movie Box Cinema";
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

            loadVidsrcServer();
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
            webSettings.setAllowFileAccess(false);
            webSettings.setAllowContentAccess(false);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
            webSettings.setSupportMultipleWindows(false);
            webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36");

            android.webkit.CookieManager.getInstance().setAcceptCookie(true);
            android.webkit.CookieManager.getInstance().setAcceptThirdPartyCookies(webViewPlayer, true);

            webViewPlayer.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            webViewPlayer.setDownloadListener(null);

            webViewPlayer.setWebChromeClient(new WebChromeClient() {
                @Override
                public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, android.os.Message resultMsg) {
                    return false;
                }
            });

            webViewPlayer.setWebViewClient(new WebViewClient() {
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

                        if (isAdUrl(urlLower)) {
                            Log.d("ADBLOCK", "Blocked popup/ad navigation: " + url);
                            return true;
                        }

                        if (host != null && !isHostAllowed(host)) {
                            Log.d("ADBLOCK", "Blocked navigation: " + host);
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

                        if (urlLower.startsWith("file://") || urlLower.startsWith("data:") || urlLower.startsWith("blob:") || urlLower.startsWith("https://embed/") || urlLower.startsWith("http://embed/") || urlLower.contains("vidsrc") || urlLower.contains("vidlink") || urlLower.contains("embed") || urlLower.contains("autoembed") || urlLower.contains("2embed") || urlLower.contains("smashy") || urlLower.contains("multiembed") || urlLower.contains("m3u8") || urlLower.contains("ts") || urlLower.contains("mp4") || urlLower.contains("hls")) {
                            return super.shouldInterceptRequest(view, request);
                        }

                        if (isAdUrl(urlLower)) {
                            return new WebResourceResponse("text/plain", "utf-8", new ByteArrayInputStream(new byte[0]));
                        }

                        android.net.Uri uri = request.getUrl();
                        String host = uri.getHost();
                        if (host != null && !isHostAllowed(host)) {
                            return new WebResourceResponse("text/plain", "utf-8", new ByteArrayInputStream(new byte[0]));
                        }
                    }
                    return super.shouldInterceptRequest(view, request);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    try {
                        String unblockScript = "(function() { " +
                                "try { " +
                                "  if (window.top !== window.self) { " +
                                "    Object.defineProperty(window, 'top', { get: () => window.self, configurable: true }); " +
                                "    Object.defineProperty(window, 'parent', { get: () => window.self, configurable: true }); " +
                                "  } " +
                                "} catch(e) {} " +
                                "var css = '[id*=\"ad-\"],[class*=\"ad-\"],[class*=\"ads\"],[id*=\"ads\"],iframe[src*=\"ads\"],iframe[src*=\"doubleclick\"],ins.adsbygoogle,[class*=\"banner\"],[id*=\"banner\"],[class*=\"popup\"],[id*=\"popup\"],[class*=\"sponsor\"],[id*=\"sponsor\"],[class*=\"overlay\"]{display:none!important};';" +
                                "var s = document.createElement(\"style\"); s.innerHTML = css; document.head.appendChild(s);" +
                                "window.open = function() { return null; };" +
                                "})();";
                        view.evaluateJavascript(unblockScript, null);
                    } catch (Exception ignored) {}
                }
            });
        } catch (Exception ignored) {}
    }

    private void loadVidsrcServer() {
        try {
            String embedUrl;
            if ("tv".equalsIgnoreCase(mediaType)) {
                embedUrl = "https://vidsrc.to/embed/tv/" + tmdbId + "/" + seasonNumber + "/" + episodeNumber;
            } else {
                embedUrl = "https://vidsrc.to/embed/movie/" + tmdbId;
            }

            String html = "<!DOCTYPE html>" +
                    "<html><head>" +
                    "<meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no'>" +
                    "<style>html,body{margin:0;padding:0;width:100%;height:100%;background:#000;overflow:hidden;}iframe{width:100%;height:100%;border:0;}</style>" +
                    "</head><body>" +
                    "<iframe src='" + embedUrl + "' allowfullscreen='true' allow='autoplay; encrypted-media; fullscreen; picture-in-picture'></iframe>" +
                    "</body></html>";

            if (webViewPlayer != null) {
                webViewPlayer.loadDataWithBaseURL("https://filmboxproductions.blogspot.com", html, "text/html", "utf-8", null);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Stream error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (webViewPlayer != null) {
                webViewPlayer.loadUrl("about:blank");
            }
        } catch (Exception ignored) {}
    }
}
