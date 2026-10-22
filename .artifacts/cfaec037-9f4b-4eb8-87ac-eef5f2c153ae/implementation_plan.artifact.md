# Implementation Plan - Fix Runtime ReferenceError (downloadView), Ad Overlays, and Community Hub

Resolve the `Uncaught ReferenceError: downloadView is not defined` at line 578 which caused the red runtime error screen; define `downloadView()` and `downloadPassModal()` cleanly; implement an aggressive MutationObserver script in `index.html` to destroy any transparent overlay layers blocking the play button; and ensure the Community hub renders correctly in the Library tab.

## User Review Required

> [!IMPORTANT]
> - **Runtime Error Fix (`downloadView is not defined`)**: Defined `downloadView()` and related offline download / saved items functions so `render()` succeeds with zero script errors.
> - **Ad Overlay / Clickjack Blocker**: Added a real-time DOM scanner that instantly removes transparent invisible overlay layers, ad iframes, and pop-unders so play buttons and video controls are always 100% clickable.
> - **Library Community Hub**: Ensure `libraryView()` and `socialCommunityHtml()` display the full community cards (Facebook Page, Facebook Group, WhatsApp, Instagram, PayPal Support).

## Proposed Changes

### Web Application Engine
#### [MODIFY] [index.html](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/assets/index.html)
- **Define `downloadView()`**: Restore the offline downloads view function to eliminate the ReferenceError.
- **Aggressive Ad Killer MutationObserver**: Automatically scan and destroy any ad popups or transparent overlay blockers on video player click.
- **Community Hub & Master Catalog**: Ensure zero runtime errors on start and instant movie rendering.

## Verification Plan

### Automated Tests
- Run Gradle build `app:assembleDebug` to verify clean compilation.

### Manual Verification
- Test opening the app to verify movies load instantly without any red error boxes.
- Test tapping Library to confirm Community hub cards render.
- Test playing a movie to confirm no ad layers block the play button.
