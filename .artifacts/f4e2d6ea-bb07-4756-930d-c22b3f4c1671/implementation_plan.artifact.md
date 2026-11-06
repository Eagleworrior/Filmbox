# Implementation Plan - Advanced Ad-Blocking & Domain Hosting PayPal Plan Integration

Implement aggressive network/WebView ad-blocking for streaming video players and update domain hosting PayPal checkout links with the exact user-specified tiers and URLs.

## User Review Required

> [!IMPORTANT]
> - All ad domains, tracking popups, and ad scripts will be intercepted and blocked at both the network request (`shouldInterceptRequest`) level and the WebView injection (`onPageFinished` MutationObserver / `window.open` override) level.
> - Domain hosting plans will be updated with the exact PayPal button links and descriptions provided by the user.

## Proposed Changes

### Android Native Ad-Blocking

#### [MODIFY] [MainActivity.java](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/java/com/filmbox/MainActivity.java)
- Expand `AD_KEYWORDS` array to include streaming and embed ad networks (`monetag`, `clickadu`, `tsyndicate`, `popads`, `propellerads`, `adsterra`, `exoclick`, `onclickads`, `popup`, `popunder`, `banner`, `tracker`, `sponsor`, `ads`, `adserver`, `stat`, `counter`, etc.).
- Enhance `shouldInterceptRequest` and `shouldOverrideUrlLoading` to aggressively return empty responses or block navigation to ad landing pages while ensuring smooth video streaming playback.
- Upgrade the `onPageFinished` JavaScript injection to strip ad iframes, prevent popups/redirects, and neutralize background click-jacking overlays.

---

### Web Assets & Domain Hosting PayPal Links

#### [MODIFY] [index.html](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/assets/index.html)
- Update `filmboxTiers` and PayPal plan mapping to use the exact names, descriptions, prices, and PayPal payment links:
  - **Trial VIP** ($0.10 USD): `https://www.paypal.com/ncp/payment/JLPJPBUSHK9CW`
  - **Daily Pass** ($0.40 USD): `https://www.paypal.com/ncp/payment/M9BV72TDYB58Y`
  - **Weekend Pass** ($0.90 USD): `https://www.paypal.com/ncp/payment/54NYU4YGSXCE6`
  - **Weekly Master** ($1.90 USD): `https://www.paypal.com/ncp/payment/76FTUPBM6QGA8`
  - **Monthly VIP** ($5.80 USD): `https://www.paypal.com/ncp/payment/DBB7WCAA366XN`
  - **Annual Pass** ($42.30 USD): `https://www.paypal.com/ncp/payment/RA4B5C8BK9WHL`
  - **Lifetime VIP** ($76.90 USD): `https://www.paypal.com/ncp/payment/KBLXJ4GJW5P5G`

## Verification Plan

### Automated Tests
- Run Gradle build (`app:assembleDebug`) to verify compilation.

### Manual Verification
- Deploy to device/emulator, tap to play movies/series, verify that all player ad popups/redirects are blocked and background ads do not appear.
- Open Domain & Hosting Hub in the app, test plan selection and check that the correct PayPal payment link is invoked.
