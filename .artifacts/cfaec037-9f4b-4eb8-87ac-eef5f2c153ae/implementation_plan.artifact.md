# FilmBox App - Hybrid WebView Architecture Plan

To resolve all startup crashes and ensure the app displays the exact **Kenya Movie Box web interface** (with all React components, M-Pesa STK push modal, video player modal, download modal, and movie catalog) exactly as designed, we are converting FilmBox into a **High-Performance Hybrid WebView Android App**.

---

## User Review Required

> [!IMPORTANT]
> **Why Hybrid WebView Architecture?**
> 1. **Zero XML Inflation Crashes**: Eliminates all Android XML layout inflation deadlocks and "keeps stopping" errors.
> 2. **Exact Web App UI**: Renders the complete React/Tailwind frontend (`index.html`, Header, HeroBanner, MovieCard, VideoPlayerModal, PaywallModal, DownloadModal) natively inside Android.
> 3. **Instant Startup**: Loads instantly via local asset bundle (`file:///android_asset/index.html`) with full JavaScript, DOM storage, media autoplay, and M-Pesa STK push support.

---

## Proposed Changes

### Assets (`app/src/main/assets`)
- **`index.html`**: Bundled HTML entry point containing the full Kenya Movie Box web application with Tailwind CSS, React, and video streaming embed servers.

### Layout (`app/src/main/res/layout`)
- **`activity_main.xml`**: Single full-screen hardware-accelerated `WebView`.

### Activity (`com.filmbox`)
- **`MainActivity.java`**: Hosts the full-screen `WebView`, configures JavaScript, DOM storage, geolocation, media playback, and local asset loading (`file:///android_asset/index.html`).

---

## Verification Plan

### Automated Tests
- Gradle debug build (`gradle_build("app:assembleDebug")`).

### Manual Verification
- Deploy APK to verify the Kenya Movie Box web UI loads instantly on app launch without any freeze or crash.
