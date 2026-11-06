# Walkthrough - Advanced Ad-Blocking & Domain Hosting PayPal Tiers Integration

Implemented comprehensive background and network ad-blocking for video streaming players, and updated the domain hosting hub with the exact user-specified PayPal plan links and descriptions.

## Changes

### Android Ad-Blocking (`MainActivity.java`)
- Expanded `AD_KEYWORDS` to block ad networks (`monetag`, `clickadu`, `tsyndicate`, `popads`, `propellerads`, `adsterra`, `exoclick`, `onclickads`, `popunder`, `ads`, `adserver`, `stat`, `counter`, etc.).
- Strengthened `shouldInterceptRequest` and `shouldOverrideUrlLoading` to intercept and block third-party popup/redirect requests while keeping video streaming streams (`vidsrc`, `vidlink`, `embed.su`, `bunnycdn`, etc.) fully operational.
- Upgraded JavaScript injection in `onPageFinished` to remove ad iframes, overlays, neutralise popup/new tab windows (`window.open`), and intercept click-jacking redirects when users tap to play movies.

---

### Domain Hosting & PayPal Links (`index.html`)
- Updated `filmboxTiers` with the exact 7 pricing tiers, descriptions, and user-provided PayPal payment links:
  - **Trial VIP** ($0.10 USD): `https://www.paypal.com/ncp/payment/JLPJPBUSHK9CW`
  - **Daily Pass** ($0.40 USD): `https://www.paypal.com/ncp/payment/M9BV72TDYB58Y`
  - **Weekend Pass** ($0.90 USD): `https://www.paypal.com/ncp/payment/54NYU4YGSXCE6`
  - **Weekly Master** ($1.90 USD): `https://www.paypal.com/ncp/payment/76FTUPBM6QGA8`
  - **Monthly VIP** ($5.80 USD): `https://www.paypal.com/ncp/payment/DBB7WCAA366XN`
  - **Annual Pass** ($42.30 USD): `https://www.paypal.com/ncp/payment/RA4B5C8BK9WHL`
  - **Lifetime VIP** ($76.90 USD): `https://www.paypal.com/ncp/payment/KBLXJ4GJW5P5G`

## Verification Results

### Automated Tests
- Gradle build (`app:assembleDebug`) completed successfully with zero errors.
