# Implementation Plan - AdMob Rewarded Ad Display Fix & App Icon Update to `111.jfif`

Fix the AdMob Rewarded Ad flow in `MainActivity.java` so actual video ads load and display to completion instead of auto-bypassing, and update the app launcher icon to `C:\Users\EAGLE\Downloads\111.jfif`.

## User Review Required

> [!IMPORTANT]
> - **AdMob Rewarded Ad Fix (`MainActivity.java`)**:
>   - Previously, if the Rewarded Ad (`ca-app-pub-3715923077671486/1845639445`) was still pre-buffering when tapped, the fallback immediately reported `SUCCESS` without showing an ad.
>   - We will fix this so `showRewardedAdForDownload` preloads the ad eagerly on startup and only triggers reward credit after the user successfully watches the video ad to completion.
> - **App Icon Update**:
>   - Convert `C:\Users\EAGLE\Downloads\111.jfif` into high-resolution PNGs across all mipmap density buckets (`mipmap-mdpi`, `mipmap-hdpi`, `mipmap-xhdpi`, `mipmap-xxhdpi`, `mipmap-xxxhdpi`) and update `ic_app_logo.png`.

## Proposed Changes

### AdMob Rewarded Ad Handling (`MainActivity.java`, `index.html`)

#### [MODIFY] [MainActivity.java](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/java/com/filmbox/MainActivity.java)
- Eagerly load `mRewardedAdDownload` (`ca-app-pub-3715923077671486/1845639445`) on app launch (`onCreate`).
- In `showRewardedAdForDownload`:
  - If `mRewardedAdDownload` is ready, display the full-screen AdMob Rewarded Ad and issue reward credit upon completion.
  - If `mRewardedAdDownload` is still loading, inform the user with a toast ("⏳ Loading Rewarded Ad from Google AdMob...") and trigger reload without auto-bypassing.

#### [MODIFY] [index.html](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/assets/index.html)
- Handle `NOT_READY` callback status in `onRewardedAdCallback` to notify the user that AdMob is pre-buffering, requiring them to complete the ad view to unlock download.

### App Icon Swap

- Convert and resize `C:\Users\EAGLE\Downloads\111.jfif` to generate all launcher icon densities (`ic_launcher.png`, `ic_launcher_round.png`, `ic_app_logo.png`).

## Verification Plan

### Automated Tests
- Build project with Gradle (`app:assembleDebug`) to verify compilation and syntax.

### Manual Verification
- Deploy app to device/emulator, tap Download, click "Watch Ads to Unlock Download", and confirm that the full AdMob Rewarded Ad displays and tracks completion.
- Verify the new `111` app icon renders sharply on the Android launcher home screen.
