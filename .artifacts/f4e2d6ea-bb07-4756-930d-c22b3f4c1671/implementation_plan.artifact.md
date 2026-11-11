# Implementation Plan - Google AdMob & Download Choice Dialog Integration

Integrate Google Mobile Ads (AdMob) SDK into Film Box with all user-specified ad unit IDs, and implement a VIP-styled Download Choice Dialog when users click any movie download button.

## User Review Required

> [!IMPORTANT]
> - **AdMob App ID**: `ca-app-pub-3715923077671486~4364312374`
> - **Banner Ad**: `ca-app-pub-3715923077671486/9860840778`
> - **Interstitial Ad**: `ca-app-pub-3715923077671486/7150257811`
> - **Native Advanced Ad**: `ca-app-pub-3715923077671486/9041923984`
> - **App Open Ad**: `ca-app-pub-3715923077671486/9202365229`
> - **Rewarded Interstitial Ad**: `ca-app-pub-3715923077671486/6052798563`
> - **Rewarded Ad (Downloads)**: `ca-app-pub-3715923077671486/1845639445`

> [!NOTE]
> - **Download Choice Dialog**: Clicking a download button presents two options matching the VIP styling:
>   1. **Watch Ads to Unlock Download**: Requires watching 2 rewarded ads (`ca-app-pub-3715923077671486/1845639445`). Shows progress ("Ad 1/2 complete") and triggers download upon completion.
>   2. **Go VIP**: Opens existing VIP pass/subscription page (`openVip()`).

## Proposed Changes

### Dependencies & Manifest (`app/build.gradle.kts`, `AndroidManifest.xml`)
- Add Google Mobile Ads SDK dependency (`play-services-ads`).
- Add AdMob App ID `<meta-data>` in `AndroidManifest.xml`.

### Application & Initialization (`FilmBoxApplication.java`)
- Initialize `MobileAds.initialize()` in `FilmBoxApplication.onCreate()`.
- Implement `AppOpenAd` manager to display App Open Ad (`ca-app-pub-3715923077671486/9202365229`) on app launch.

### Layout & Admob View Loading (`activity_main.xml`, `MainActivity.java`)
- Add `AdView` for Banner Ad (`ca-app-pub-3715923077671486/9860840778`) at the bottom of the screen.
- Load Interstitial Ad (`ca-app-pub-3715923077671486/7150257811`) for navigation / playback.
- Load Rewarded Interstitial (`ca-app-pub-3715923077671486/6052798563`) for background rewards.

### Download Button Choice Dialog (`index.html`)
- Hook into download button clicks (`quickDownloadCard`, `downloadEpisode`, `startDownloadCurrent`).
- Display a modal matching the VIP gradient theme offering:
  - Option 1: "Watch Ads to Unlock Download" (triggers 2 rewarded ads `ca-app-pub-3715923077671486/1845639445` with progress tracking before starting download).
  - Option 2: "Go VIP" (triggers `openVip()`).

## Verification Plan

### Automated Tests
- Build project with Gradle (`app:assembleDebug`) to verify syntax, compilation, and SDK linking.

### Manual Verification
- Verify App Open Ad on launch.
- Verify Banner Ad at screen bottom.
- Verify Download Choice Dialog appearance on clicking download.
- Verify 2-ad sequence completion unlocks download.
- Verify "Go VIP" button opens VIP page.
