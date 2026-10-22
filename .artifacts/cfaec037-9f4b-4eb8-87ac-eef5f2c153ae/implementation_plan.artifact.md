# Implementation Plan - Strict Browser Intent Whitelisting & Ad Redirect Blocker

Enforce strict URL and browser intent restrictions in `MainActivity.java` (`shouldOverrideUrlLoading`), allowing external browser launches **ONLY** for approved community & payment destinations (PayPal, Facebook, WhatsApp, Instagram, and FilmBox Blogger), while strictly blocking all other external URLs and ad pop-unders; and add a JavaScript redirect killer in `index.html` to prevent video player clickjack overlays.

## User Review Required

> [!IMPORTANT]
> - **Strict Browser Intent Whitelisting (`MainActivity.java`)**: `shouldOverrideUrlLoading` now checks every outgoing URL:
>   - **Allowed External Browsers**: Strictly restricted to PayPal (`paypal.com`, `paypal.me`), Facebook (`facebook.com`, `fb.me`), WhatsApp (`whatsapp.com`, `wa.me`), Instagram (`instagram.com`), and Blogger (`blogspot.com`).
>   - **Blocked**: All other external URLs, ad popups, and unknown redirects are unconditionally blocked (`return true;`).
> - **In-App Whitelist**: TMDb, Football-Data, SportSRC, and Vidsrc playback hosts load inside the app.
> - **JavaScript Redirect Killer (`index.html`)**: Intercepts `window.open` and unexpected top-level navigation attempts to prevent ad overlay redirects when tapping play buttons.

## Proposed Changes

### Android Native Core
#### [MODIFY] [MainActivity.java](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/java/com/filmbox/MainActivity.java)
- Restrict `shouldOverrideUrlLoading` to allow external browser intents exclusively for PayPal, Facebook, WhatsApp, Instagram, and Blogger; block all other unknown external browsers and ad redirects.

### Web Application Engine
#### [MODIFY] [index.html](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/assets/index.html)
- Add runtime script interceptor to block unapproved `window.open` or redirect attempts when clicking player buttons.

## Verification Plan

### Automated Tests
- Run Gradle build `app:assembleDebug` to verify clean compilation.

### Manual Verification
- Test tapping community links (WhatsApp, Facebook, Instagram, PayPal) to confirm they open successfully in external browsers.
- Test playing movies to confirm zero ad redirects or pop-under browser tabs open.
