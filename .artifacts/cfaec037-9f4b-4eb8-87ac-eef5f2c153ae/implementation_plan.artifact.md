# Implementation Plan - Support & Policies Sub-Tab in Library & Whitelist Verification

Add a dedicated **Support & Policies** sub-tab in the Library view containing high-graphic neon pill buttons for Contact Us (`filmboxindustries@gmail.com`), Privacy Policy, and About Us; whitelist these official Blog links in `MainActivity.java`; and re-confirm all platform features (Community links, real-time VIP verification, player back arrow `‹`, colored typography, and startup script error filter).

## User Review Required

> [!IMPORTANT]
> - **Support & Policies Sub-Tab (`libraryView()`)**:
>   - **📩 Contact Us**: `https://filmboxproductions.blogspot.com/p/contact-us.html` (Displaying Email: `filmboxindustries@gmail.com`).
>   - **🔒 Privacy Policy**: `https://filmboxproductions.blogspot.com/p/privacy-policy.html`
>   - **🎮 About Us**: `https://filmboxproductions.blogspot.com/p/about-us.html`
>   - Styled with glowing neon pill containers, vibrant brand icons, and gradient typography matching the Community Hub design.
> - **Whitelisted Browser Intents (`MainActivity.java`)**: `filmboxproductions.blogspot.com` URLs are whitelisted in `shouldOverrideUrlLoading` so they open smoothly in external browsers when tapped.
> - **Full System Re-Confirmation**:
>   - **Community Links**: Facebook Page, Facebook Group, WhatsApp, Instagram, Blogger, PayPal.
>   - **Real-Time VIP Activation**: M-Pesa STK polling & PayPal REST verification; VIP is activated **only** upon confirmation.
>   - **Video Player Controls**: Top-left back arrow (`‹`), 3-second auto-hide timer, and tap-to-toggle vanish controls.
>   - **Startup Script Error Filter**: `window.onerror` suppresses cross-origin CDN `Line 0` errors.

## Proposed Changes

### Web Application Engine
#### [MODIFY] [index.html](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/assets/index.html)
- Add **📜 Support & Policies** sub-tab in `libraryView()` with styled neon pill buttons for Contact Us, Privacy Policy, and About Us.

### Android Native Core
#### [MODIFY] [MainActivity.java](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/java/com/filmbox/MainActivity.java)
- Confirm `filmboxproductions.blogspot.com` is whitelisted in `shouldOverrideUrlLoading`.

## Verification Plan

### Automated Tests
- Run Gradle build `app:assembleDebug` to verify clean compilation.

### Manual Verification
- Test opening Library -> Support & Policies sub-tab to verify Contact Us, Privacy Policy, and About Us buttons load properly.
