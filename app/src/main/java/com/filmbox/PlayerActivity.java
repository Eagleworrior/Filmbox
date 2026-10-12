package com.filmbox;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class PlayerActivity extends AppCompatActivity {

    private WebView webViewPlayer;
    private Button btnServer;
    private boolean isLandscape = false;

    private int tmdbId = 27205;
    private String mediaType = "movie";
    private int seasonNumber = 1;
    private int episodeNumber = 1;
    private String movieTitle = "Movie";

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_player);

            View playerView = findViewById(R.id.playerView);
            if (playerView != null) {
                playerView.setVisibility(View.GONE);
            }

            webViewPlayer = findViewById(R.id.webViewPlayer);
            if (webViewPlayer != null) {
                webViewPlayer.setVisibility(View.VISIBLE);
                setupWebView();
            }

            TextView tvPlayerTitle = findViewById(R.id.tvPlayerTitle);
            ImageButton btnBack = findViewById(R.id.btnBack);
            btnServer = findViewById(R.id.btnServer);
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

            if (btnServer != null) {
                btnServer.setOnClickListener(v -> {
                    String[] servers = {
                            "🚀 Server 1: VidLink Pro (Ultra HD)",
                            "⚡ Server 2: VidSrc Ultra",
                            "🌐 Server 3: Embed.su HD",
                            "🍿 Server 4: SuperStream Multi",
                            "🎬 Server 5: AutoEmbed CC",
                            "📺 Server 6: SmashyStream"
                    };
                    new AlertDialog.Builder(this)
                            .setTitle("Select Streaming Server")
                            .setItems(servers, (dialog, which) -> {
                                loadMovieBoxServer(which + 1);
                            })
                            .show();
                });
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

            handler.postDelayed(() -> loadMovieBoxServer(1), 150);
        } catch (Exception e) {
            Toast.makeText(this, "Error starting player: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
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
            webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

            webViewPlayer.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            webViewPlayer.setWebChromeClient(new WebChromeClient());
            webViewPlayer.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    try {
                        String unblockScript = "try { " +
                                "if (window.top !== window.self) { " +
                                "  Object.defineProperty(window, 'top', { get: () => window.self, configurable: true }); " +
                                "  Object.defineProperty(window, 'parent', { get: () => window.self, configurable: true }); " +
                                "} " +
                                "} catch(e) {}";
                        view.evaluateJavascript(unblockScript, null);
                    } catch (Exception ignored) {}
                }
            });
        } catch (Exception ignored) {}
    }

    private void loadMovieBoxServer(int serverNumber) {
        try {
            String embedUrl;
            if ("tv".equalsIgnoreCase(mediaType)) {
                if (serverNumber == 1) {
                    embedUrl = "https://vidlink.pro/tv/" + tmdbId + "/" + seasonNumber + "/" + episodeNumber;
                } else if (serverNumber == 2) {
                    embedUrl = "https://vidsrc.to/embed/tv/" + tmdbId + "/" + seasonNumber + "/" + episodeNumber;
                } else if (serverNumber == 3) {
                    embedUrl = "https://embed.su/embed/tv/" + tmdbId + "/" + seasonNumber + "/" + episodeNumber;
                } else if (serverNumber == 4) {
                    embedUrl = "https://multiembed.mov/?video_id=" + tmdbId + "&tmdb=1&s=" + seasonNumber + "&e=" + episodeNumber;
                } else if (serverNumber == 5) {
                    embedUrl = "https://player.autoembed.cc/embed/tv/" + tmdbId + "/" + seasonNumber + "/" + episodeNumber;
                } else {
                    embedUrl = "https://player.smashystream.xyz/tv/" + tmdbId + "?s=" + seasonNumber + "&e=" + episodeNumber;
                }
            } else {
                if (serverNumber == 1) {
                    embedUrl = "https://vidlink.pro/movie/" + tmdbId;
                } else if (serverNumber == 2) {
                    embedUrl = "https://vidsrc.to/embed/movie/" + tmdbId;
                } else if (serverNumber == 3) {
                    embedUrl = "https://embed.su/embed/movie/" + tmdbId;
                } else if (serverNumber == 4) {
                    embedUrl = "https://multiembed.mov/?video_id=" + tmdbId + "&tmdb=1";
                } else if (serverNumber == 5) {
                    embedUrl = "https://player.autoembed.cc/embed/movie/" + tmdbId;
                } else {
                    embedUrl = "https://player.smashystream.xyz/movie/" + tmdbId;
                }
            }

            if (webViewPlayer != null) {
                webViewPlayer.loadUrl(embedUrl);
            }
            Toast.makeText(this, "Streaming via Server " + serverNumber, Toast.LENGTH_SHORT).show();
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
