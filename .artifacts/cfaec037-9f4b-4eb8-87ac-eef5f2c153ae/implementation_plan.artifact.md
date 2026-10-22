# Implementation Plan - Master Catalog Rendering Fix, Real-Time M-Pesa STK Polling & Fullscreen Video Player Upgrades

Fix movie rendering so blockbusters display immediately on Home, Movies, and Series tabs; enforce real-time M-Pesa STK push polling with live error messages (wrong PIN, insufficient funds, cancellation); restrict external browser intents to approved domains (PayPal, Facebook, WhatsApp, Blogger); and upgrade the HTML5 video player with perfect screen fit, top-left back arrow, and auto-hiding controls.

## User Review Required

> [!IMPORTANT]
> - **Master Catalog Rendering (`home()` & `browse()`)**: Fully populate Home and catalogue views with the 20+ embedded blockbuster movies and series so films appear instantly with gorgeous posters.
> - **Real-Time M-Pesa STK Polling**: Full-screen STK waiting screen that polls transaction status every 3 seconds, showing precise Safaricom error messages (wrong PIN, insufficient balance, cancelled on phone) if unconfirmed.
> - **Strict Browser Whitelisting**: Ensure official external links (PayPal checkout, Facebook Page/Group, WhatsApp lounge, Blogger) open correctly in external browsers/intents.
> - **Fullscreen Video Player (`showPlayer`)**: Perfect screen fit, top-left back arrow (`‹`), duration display, and controls that auto-hide after 2 seconds or on screen tap.

## Proposed Changes

### Web Application Core Engine
#### [MODIFY] [index.html](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/assets/index.html)
- **Instant Movie Catalog Rendering**: Ensure `home()`, `browse()`, and `fetchPage()` render local master catalog items instantly.
- **Real-Time M-Pesa Polling**: Robust STK status polling with detailed Safaricom error descriptions.
- **Fullscreen Player Enhancements**: Top-left back arrow, auto-hiding control bar (2 seconds timeout), and exact screen fit.

## Verification Plan

### Automated Tests
- Run Gradle build `app:assembleDebug` to verify clean compilation.

### Manual Verification
- Test opening Home and Movies tabs to verify instant movie posters.
- Test M-Pesa payment prompt and observe real-time status feedback.
- Test video player playback, back arrow, and auto-hiding controls.
