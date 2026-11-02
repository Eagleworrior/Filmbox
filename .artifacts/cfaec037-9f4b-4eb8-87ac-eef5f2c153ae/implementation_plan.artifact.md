# Implementation Plan - Full Feature Set Restoration with Preserved Bottom Dock

Restore the complete, feature-rich Film Box application engine while preserving the exact fixed bottom navigation dock bar and multi-colored box buttons.

## User Review Required

> [!IMPORTANT]
> - **Preserved Bottom Navigation Dock**:
>   - Keeps `.floating-dock` fixed at the bottom viewport (`z-index: 2147483647`).
>   - Keeps individual colored boxes for Home (Pink), Movies (Cyan), Series (Green), and Library (Gold).
> - **Restored Core Feature Engines**:
>   1. **1-Character Live Search Autocomplete**: Instant search matching local master catalog and TMDb multi-search API.
>   2. **TV Series Season & Episode Picker (`openTitle`)**: Load seasons, episodes, runtime, and episode play/download links.
>   3. **M-Pesa STK Push with Real-Time Safaricom Polling**: Phone input, `queryMpesaSTKStatus` polling every 3 seconds, and live `ResultCode` error messages (insufficient funds, wrong PIN, cancelled).
>   4. **PayPal VIP Checkout Links**: Direct plan mapping to official PayPal payment links.
>   5. **30-Minute Free Trial Watch Timer**: Recurring 5-minute VIP upsell popups during video playback.
>   6. **Video Player Overlay Controls**: Top-left back arrow (`‹`), 3-second auto-hide timer, and tap-to-vanish controls.
>   7. **High-Graphic Community Hub & Support Sub-Tabs**: SVG brand icons for Facebook, WhatsApp, Instagram, Blogger, and PayPal, plus Contact Us (`filmboxindustries@gmail.com`), Privacy Policy, and About Us.

## Proposed Changes

### Web Application UI Engine
#### [MODIFY] [index.html](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/assets/index.html)
- Combine the complete JS application engines (`openTitle`, `startMpesaPolling`, `payWithPayPal`, `startWatchTimer`, `fetchNews`, `search`) with the preserved fixed bottom navigation dock CSS and HTML markup.

## Verification Plan

### Automated Tests
- Run Gradle build `app:assembleDebug` to verify clean compilation with zero syntax errors.

### Manual Verification
- Test bottom dock tabs to verify `Home`, `Movies`, `Series`, and `Library` switch views instantly.
- Test typing in search bar to verify 1-character live autocomplete.
- Test tapping a TV show card to verify seasons and episodes load in the details sheet.
- Test playing a video to verify player controls, back arrow `‹`, and 3s auto-hide timer.
- Test tapping "★ Go VIP" to verify M-Pesa STK push and PayPal plan cards.
