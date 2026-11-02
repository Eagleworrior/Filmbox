# Implementation Plan - Blogger-Identical Iframe Embedding for `vidsrc.to` (`https://filmboxproductions.blogspot.com`)

This plan mirrors the exact working iframe embed configuration from your Blogger website (`filmboxproductions.blogspot.com`), enabling `vidsrc.to` to play instantly without black screens:

1. **Blogger-Identical Iframe Wrapper (`PlayerActivity.java`)**:
   - Wrap `https://vidsrc.to/embed/movie/${tmdbId}` (and `tv/${tmdbId}/${season}/${episode}`) inside an `<iframe>` identical to the Blogger post layout:
     ```html
     <!DOCTYPE html>
     <html>
     <head>
     <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
     <style>
     html, body { margin:0; padding:0; width:100%; height:100%; background:#000; overflow:hidden; }
     iframe { width:100%; height:100%; border:0; }
     </style>
     </head>
     <body>
     <iframe src="https://vidsrc.to/embed/movie/${tmdbId}" allowfullscreen="true" allow="autoplay; encrypted-media; fullscreen; picture-in-picture"></iframe>
     </body>
     </html>
     ```
   - Load this HTML using `webViewPlayer.loadDataWithBaseURL("https://filmboxproductions.blogspot.com", html, "text/html", "utf-8", null)`.
   - By declaring `https://filmboxproductions.blogspot.com` as the base domain, `vidsrc.to` recognizes the valid website origin (matching Blogger) and plays the movie instantly!

2. **Server 2 & Server Selection**:
   - Default to `vidsrc.to` (`https://vidsrc.to/embed/movie/${tmdbId}`).
   - Pass through all `vidsrc.to` player scripts and HLS sub-resources uninterrupted.

3. **M-Pesa Locked**:
   - M-Pesa payment code in `MainActivity.java` remains strictly locked and untouched.

## User Review Required

> [!IMPORTANT]
> - **Blogger-Identical Origin**: Sets `https://filmboxproductions.blogspot.com` as the WebView base origin, giving `vidsrc.to` the exact same website context as your Blogger site and enabling instant video playback.
> - **M-Pesa Locked**: M-Pesa payment code remains 100% untouched.

## Proposed Changes

### Java Source Code (`app/src/main/java/com/filmbox/`)

#### [MODIFY] [PlayerActivity.java](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/java/com/filmbox/PlayerActivity.java)
- Update `loadVidsrcServer()` to render the Blogger iframe wrapper and load it via `loadDataWithBaseURL("https://filmboxproductions.blogspot.com", ...)`.

---

## Verification Plan

### Automated Tests
- Build project using Gradle:
  ```bash
  gradle_build(":app:assembleDebug")
  ```

### Manual Verification
- Tap play on any movie or TV show, verify `PlayerActivity` loads `vidsrc.to` with `filmboxproductions.blogspot.com` origin, and plays the movie instantly just like on your Blogger site.
