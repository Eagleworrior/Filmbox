# Implementation Plan - Dynamic Heuristic Ad Blocker & Guaranteed Instant Home Movie Rendering

Implement a runtime heuristic ad blocker in JavaScript and Android `MainActivity.java` that dynamically detects and destroys ad iframes, malicious overlays, popups, and tracking scripts regardless of domain name changes; and fix home screen movie rendering so master catalog films display instantly with zero reliance on external network availability.

## User Review Required

> [!IMPORTANT]
> - **Dynamic Heuristic Ad Interception**: Injects a real-time MutationObserver and script scanner into WebView that automatically detects, hides, and destroys any dynamic ad iframes, malicious popups, and transparent overlay clickjackers in the background even if ad domains change.
> - **Guaranteed Instant Home Movies**: Ensures `home()` renders master catalog movies synchronously and robustly without being cleared by failed asynchronous network calls, guaranteeing movies appear immediately the moment the app opens.

## Proposed Changes

### Android Native Core
#### [MODIFY] [MainActivity.java](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/java/com/filmbox/MainActivity.java)
- Enhance `shouldInterceptRequest` and inject JavaScript ad-removal filters to block ads at runtime.

### Web Application Engine
#### [MODIFY] [index.html](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/assets/index.html)
- **MutationObserver Ad Killer**: Add real-time DOM mutation observer to instantly strip ad elements.
- **Robust Home Rendering**: Ensure master catalog items populate `#h1`, `#h2`, `#h4`, and `#h5` robustly.

## Verification Plan

### Automated Tests
- Run Gradle build `app:assembleDebug` to verify clean compilation.

### Manual Verification
- Test opening the app to confirm movies display instantly on Home.
- Test video playback to verify zero ad overlays or pop-ups.
