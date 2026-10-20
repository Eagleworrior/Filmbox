# Implementation Plan - Social Media Hub, Blogger Integration, Strict Link Whitelist & 10-Minute Renewal Banner

Integrate FilmBox official social media links (Facebook Page, Facebook Group, WhatsApp Group, Blogger), strictly limit external browser launches to approved destinations in `MainActivity.java`, and implement a 10-minute VIP Renewal Banner with a ✕ dismiss button.

## User Review Required

> [!IMPORTANT]
> - **Strict External Intent Whitelist (`MainActivity.java`)**:
>   - **Allowed External Destinies**:
>     1. **WhatsApp**: `whatsapp.com`, `chat.whatsapp.com`, `whatsapp://`
>     2. **PayPal**: `paypal.com`, `paypal.me`
>     3. **Facebook**: `facebook.com`, `https://www.facebook.com/profile.php?id=61595278122419`, `https://www.facebook.com/share/g/19zsQAbGu7/`
>     4. **Blogger**: `blogspot.com`, `https://filmboxproductions.blogspot.com`
>   - **All other external browser links**: Strictly blocked.
> - **10-Minute VIP Renewal Banner (`index.html`)**:
>   - Every 10 minutes when VIP is expired, displays a bottom banner: *"⭐ Your VIP Access has expired! Renew now for smooth, uninterrupted streaming."*
>   - Includes a **`Renew VIP ⚡`** button (opening `openVip()`) and a **`✕`** close button so the user can easily dismiss it.
> - **Complete Social Media & Blog Hub**:
>   - Added buttons with vector icons and glowing badges for WhatsApp Group, Facebook Page, Facebook Group, and the official FilmBox Blogger site.

## Proposed Changes

### Android Native Intent Whitelist
#### [MODIFY] [MainActivity.java](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/java/com/filmbox/MainActivity.java)
- Update `shouldOverrideUrlLoading` to allow Intents ONLY for WhatsApp, PayPal, Facebook (Page & Group), and Blogger (`filmboxproductions.blogspot.com`). Strictly block all others.

### Web Application UI & Renewal Banner
#### [MODIFY] [index.html](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/assets/index.html)
- Add 10-minute VIP renewal timer (`initVipRenewTimer()`) and floating banner `#vipRenewBanner` with ✕ close button and `Renew VIP ⚡` button.
- Integrate Facebook Page, Facebook Group, and Blogger links into the Community Hub container in `openVip()`.

## Verification Plan

### Automated Tests
- Run Gradle build `app:assembleDebug` to verify clean compilation.

### Manual Verification
- Test tapping Facebook Page, Facebook Group, WhatsApp, and Blogger links to confirm they launch correctly.
- Test 10-minute renewal banner display and ✕ close button dismissal.
