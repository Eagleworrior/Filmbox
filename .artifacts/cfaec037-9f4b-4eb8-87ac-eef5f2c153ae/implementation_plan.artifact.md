# Implementation Plan - Immersive Edge-to-Edge Fullscreen, Native Browser Lockdown, Ad-Shield & Minimalist Rotate Icon

Implement immersive system bar hiding (hiding phone status bar and navigation bar completely during video playback), strict native browser lockdown in `MainActivity.java` to prevent external browser launches, transparent ad-shield touch overlay, and a minimalist full-screen icon button (`⛶`).

## User Review Required

> [!IMPORTANT]
> - **Immersive Edge-to-Edge Fullscreen (`MainActivity.java`)**: Added native system bar hiding (`getWindow().getInsetsController().hide(...)` / `SYSTEM_UI_FLAG_FULLSCREEN` & `HIDE_NAVIGATION`). When a video plays, the phone status bar and navigation bar disappear completely, filling the entire screen.
> - **Strict Native Browser Lockdown**: Configured `shouldOverrideUrlLoading` to consume and block any attempt to open external browsers (Chrome, Safari, etc.), ensuring users never get redirected outside the app.
> - **Transparent Ad-Shield Touch Overlay**: Intercepts tap events on the player screen to toggle controls while protecting against underlying iframe ad click-traps.
> - **Minimalist Full-Screen Icon Button (`⛶`)**: Replaced text/rotate buttons with a sleek, minimalist square fullscreen/rotation icon button in `.pbar` that auto-hides after 3 seconds.

## Proposed Changes

### Android Native System & Security
#### [MODIFY] [MainActivity.java](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/java/com/filmbox/MainActivity.java)
- Add `setFullscreenMode(boolean enabled)` JavaScript Interface method to hide/show status bar and navigation bar and lock orientation to landscape.
- Update `shouldOverrideUrlLoading` to strictly block all external browser intents.

### Web Application & Player UI
#### [MODIFY] [index.html](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/assets/index.html)
- Call `AndroidKYC.setFullscreenMode(true)` on `showPlayer()` and `false` on `closePlayer()`.
- Replace text/rotate buttons in `.pbar` with minimalist square icon button (`⛶`).

## Verification Plan

### Automated Tests
- Run Gradle build `app:assembleDebug` to verify clean compilation.

### Manual Verification
- Stream a movie to verify phone status bar and navigation buttons hide completely (edge-to-edge fullscreen).
- Tap the player screen to verify controls appear with minimalist `⛶` button and auto-hide after 3 seconds.
- Test tapping pause/play to verify no external browser or ad redirect occurs.
