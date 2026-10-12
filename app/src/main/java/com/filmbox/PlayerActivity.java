package com.filmbox;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        View playerView = findViewById(R.id.playerView);
        if (playerView != null) {
            playerView.setVisibility(View.GONE);
        }

        webViewPlayer = findViewById(R.id.webViewPlayer);
        webViewPlayer.setVisibility(View.VISIBLE);

        TextView tvPlayerTitle = findViewById(R.id.tvPlayerTitle);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnServer = findViewById(R.id.btnServer);
        ImageButton btnRotateScreen = findViewById(R.id.btnRotateScreen);

        tmdbId = getIntent().getIntExtra("tmdb_id", 27205);
        if (tmdbId <= 0) tmdbId = 27205;
        mediaType = getIntent().getStringExtra("media_type");
        if (mediaType == null) mediaType = "movie";
        seasonNumber = getIntent().getIntExtra("season_number", 1);
        episodeNumber = getIntent().getIntExtra("episode_number", 1);

        movieTitle = getIntent().getStringExtra("title");
        if (movieTitle != null) {
            tvPlayerTitle.setText(movieTitle);
        } else {
            movieTitle = "Kenya Movie Box Cinema";
        }

        btnBack.setOnClickListener(v -> finish());

        btnServer.setOnClickListener(v -> {
            String[] servers = {
                    "🚀 Server 1: VidLink Pro (Ultra HD)",
                    "⚡ Server 2: VidSrc Ultra (Zero Sandbox Block)",
                    "🌐 Server 3: Embed.su HD (Cinema)",
                    "🍿 Server 4: SuperStream Multi (1080p)",
                    "🎬 Server 5: AutoEmbed CC (Fast Mirror)",
                    "📺 Server 6: SmashyStream (Alternative)"
            };
            new AlertDialog.Builder(this)
                    .setTitle("Select Streaming Server")
                    .setItems(servers, (dialog, which) -> {
                        loadMovieBoxServer(which + 1);
                    })
                    .show();
        });

        btnRotateScreen.setOnClickListener(v -> {
            if (isLandscape) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                isLandscape = false;
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                isLandscape = true;
            }
        });

        setupWebView();
        loadMovieBoxServer(1);
    }

    private void setupWebView() {
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
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.contains("vidlink") || url.contains("vidsrc") || url.contains("embed.su") || url.contains("multiembed") || url.contains("autoembed") || url.contains("smashystream")) {
                    return false;
                }
                return true;
            }
        });
    }

    private void loadMovieBoxServer(int serverNumber) {
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

        webViewPlayer.loadUrl(embedUrl);
        Toast.makeText(this, "Streaming via Server " + serverNumber, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (webViewPlayer != null) {
            webViewPlayer.loadUrl("about:blank");
        }
    }
}
