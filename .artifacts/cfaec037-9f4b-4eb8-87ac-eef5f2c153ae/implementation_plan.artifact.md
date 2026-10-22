# Implementation Plan - Strict Ad Interception, Browser Intent Whitelisting & Instant Master Catalog Rendering

Implement background network-level ad blocking in `WebViewClient.shouldInterceptRequest`, enforce strict browser intent whitelisting (allowing only PayPal, Facebook Page/Group, WhatsApp, and Blogger), explain the cause of the dark screen, and ensure `home()` renders local master catalog movies immediately.

## Explanation: Why the Screen Appeared Dark

The screen appeared dark because `home()` previously rendered empty loading messages (`<p class="msg">Loading live trending movies…</p>`) while waiting for the TMDb network request to complete. If the TMDb network call took time or failed, `#h1`, `#h2`, `#h4`, and `#h5` remained blank over the dark background (`#0b0b1a`).

**The Solution**: Modify `home()` so that `LOCAL_MASTER_MOVIES` and `LOCAL_MASTER_SERIES` render **synchronously on 0ms page load**, displaying 20+ blockbuster movie posters immediately, and then updating smoothly in the background when TMDb network data arrives.

## User Review Required

> [!IMPORTANT]
> - **Strict Network Ad Interception (`MainActivity.java`)**: Added `shouldInterceptRequest` to cancel and block requests to ad networks, pop-unders, and tracking domains (`popads.net`, `propellerads.com`, `exoclick.com`, `adsterra.com`, `alwingulla.com`, `doubleclick.net`, `clickadu.com`, etc.) before they load.
> - **Strict External Browser Intent Whitelist**: `shouldOverrideUrlLoading` allows ONLY approved community and payment links:
>   - **PayPal**: `paypal.com`, `sandbox.paypal.com`
>   - **Facebook**: `facebook.com`, `fb.com`, `m.facebook.com` (Official Page & Group)
>   - **WhatsApp**: `chat.whatsapp.com`, `wa.me` (VIP Group)
>   - **Blogger**: `filmboxproductions.blogspot.com`, `blogspot.com` (Official Blog)
>   - *All other external URL redirects and popup requests are strictly blocked.*
> - **Instant Master Movie Catalog Rendering**: `home()` renders 20+ blockbuster movie/series posters synchronously on startup so movies are visible immediately with zero black screens.

## Proposed Changes

### Android Native Core
#### [MODIFY] [MainActivity.java](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/java/com/filmbox/MainActivity.java)
- Implement `shouldInterceptRequest` with an extensive ad & tracking domain blocklist to kill ad scripts before they execute.
- Enforce strict domain whitelisting in `shouldOverrideUrlLoading` allowing only PayPal, Facebook, WhatsApp, and Blogger.

### Web Application UI Engine
#### [MODIFY] [index.html](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/assets/index.html)
- Synchronously render `LOCAL_MASTER_MOVIES` and `LOCAL_MASTER_SERIES` inside `home()` on page load so movies display instantly.

## Verification Plan

### Automated Tests
- Run Gradle build `app:assembleDebug` to verify clean compilation.

### Manual Verification
- Test opening the app to verify movie cards appear immediately on Home.
- Test tapping external links to confirm only PayPal, Facebook, WhatsApp, and Blogger open externally, while ads and unknown redirects are blocked.
