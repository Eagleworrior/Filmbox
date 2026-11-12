# Implementation Plan - Dynamic TMDb Search & App Icon Update to `Gemini_Generated_Image_4v09g74v09g74v09`

Transition search and movie discovery in `index.html` to be 100% dynamically handled by the TMDb API (removing hardcoded local arrays), and update the app launcher icon to `C:\Users\EAGLE\Downloads\Gemini_Generated_Image_4v09g74v09g74v09.jfif`.

## User Review Required

> [!IMPORTANT]
> - **100% Dynamic TMDb API Integration**: All search queries (both autocomplete dropdown and Enter-key grid search) and category listings will be handled dynamically by live TMDb API calls (`/search/multi`, `/search/movie`, `/search/tv`).
> - **App Icon Update**:
>   - Convert `C:\Users\EAGLE\Downloads\Gemini_Generated_Image_4v09g74v09g74v09.jfif` into high-resolution PNGs across all mipmap density buckets (`mipmap-mdpi`, `mipmap-hdpi`, `mipmap-xhdpi`, `mipmap-xxhdpi`, `mipmap-xxxhdpi`) and update `ic_app_logo.png`.

## Proposed Changes

### Web Asset Dynamic Search (`index.html`)

#### [MODIFY] [index.html](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/assets/index.html)
- Streamline `index.html` so search inputs (`#q`) fetch live results directly from TMDb API (`tmdb('/search/multi', ...)`).
- Remove hardcoded movie arrays in favor of pure dynamic API discovery and rendering.

### App Icon Swap

- Convert and resize `C:\Users\EAGLE\Downloads\Gemini_Generated_Image_4v09g74v09g74v09.jfif` to generate all launcher icon densities (`ic_launcher.png`, `ic_launcher_round.png`, `ic_app_logo.png`).

## Verification Plan

### Automated Tests
- Build project with Gradle (`app:assembleDebug`) to verify compilation and syntax.

### Manual Verification
- Deploy app to device/emulator, type queries into search bar, press Enter, and confirm live TMDb search results appear dynamically.
- Verify the new `Gemini_Generated_Image_4v09g74v09g74v09` app icon renders sharply on the Android launcher.
