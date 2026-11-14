# Implementation Plan - AdMob Ad Unit IDs Update & Instant Pre-buffering Optimization

Update all Google AdMob Ad Unit IDs across the project to the user's new IDs, optimize eager pre-buffering on startup for faster ad delivery, and ensure clear messaging when an ad is pre-buffering.

## User Review Required

> [!IMPORTANT]
> - **New AdMob App ID**: `ca-app-pub-3715923077671486~7235293381`
> - **Banner Ad**: `ca-app-pub-3715923077671486~7235293381` (or banner unit ID)
> - **Interstitial Ad**: `ca-app-pub-3715923077671486/9321245725`
> - **Rewarded Interstitial Ad**: `ca-app-pub-3715923077671486/1791395019`
> - **Rewarded Ad**: `ca-app-pub-3715923077671486/6907990322`
> - **Native Advanced Ad**: `ca-app-pub-3715923077671486/3416127013`
> - **App Open Ad**: `ca-app-pub-3715923077671486/9538682234`

## Proposed Changes

### Manifest & Application Configuration (`AndroidManifest.xml`, `FilmBoxApplication.java`)
- Update `AndroidManifest.xml` `<meta-data>` with new App ID: `ca-app-pub-3715923077671486~7235293381`.
- Update `FilmBoxApplication.java` App Open Ad ID: `ca-app-pub-3715923077671486/9538682234`.

### Layout & Main Activity Ad Unit IDs (`activity_main.xml`, `MainActivity.java`)
- Update `activity_main.xml` AdView unit ID.
- Update `MainActivity.java` with new IDs:
  - Interstitial: `ca-app-pub-3715923077671486/9321245725`
  - Rewarded Interstitial: `ca-app-pub-3715923077671486/1791395019`
  - Rewarded Ad: `ca-app-pub-3715923077671486/6907990322`
  - Native Advanced: `ca-app-pub-3715923077671486/3416127013`
- Pre-buffer all ad units eagerly on startup for instant availability.
- Provide clear feedback ("⏳ AdMob Ad is buffering. Please try again in a few seconds or select Go VIP.") if an ad is still loading when clicked.

## Verification Plan

### Automated Tests
- Build project with Gradle (`app:assembleDebug`) to verify compilation and syntax.

### Manual Verification
- Deploy app, verify App Open Ad on launch, Banner at bottom, Interstitial before player, and Rewarded Ad flow for offline downloads using new AdMob unit IDs.
