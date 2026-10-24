# Implementation Plan - Video Player Controls Upgrade & High-Graphic Offline Screen

Upgrade the video player control overlay (`showPlayer`) with a prominent top-left back arrow button (`‹`), 3-second auto-hide timer, and tap-to-toggle vanish behavior; and redesign the offline screen with multi-color glowing neon typography and animated radar rings.

## User Review Required

> [!IMPORTANT]
> - **Video Player Back Arrow & Tap Controls (`showPlayer`)**:
>   - **Prominent Back Arrow (`‹`)**: Placed at the top-left of the player overlay with a glowing cyan border (`border: 2px solid #38bdf8; box-shadow: 0 0 15px rgba(56,189,248,0.6)`).
>   - **3-Second Auto-Hide**: Controls auto-hide after 3 seconds of inactivity.
>   - **Tap-to-Toggle / Tap-to-Vanish**: Tapping the screen when controls are visible hides all overlays immediately. Tapping when hidden brings back the controls and resets the 3-second timer.
> - **Strict Online-Only & High-Graphic Offline Screen**:
>   - The app strictly requires an online network connection (`checkOnlineStatus()`).
>   - Render a high-graphic, neon-decorated offline screen featuring multi-color gradient title (**`📡 NO INTERNET CONNECTION`**), animated pulsing radar ring, and a glowing `Retry Connection 🔄` pill button.

## Proposed Changes

### Web Application UI Engine
#### [MODIFY] [index.html](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/assets/index.html)
- **Player Controls (`showPlayer`)**: Update `#pbar` styling, `z-index: 99999`, 3-second timer, tap-to-toggle vanish listener, and prominent back arrow button.
- **High-Graphic Offline Screen (`checkOnlineStatus`)**: Replace plain offline message with glowing multi-color gradient typography and animated radar rings.

## Verification Plan

### Automated Tests
- Run Gradle build `app:assembleDebug` to verify clean compilation.

### Manual Verification
- Test playing a movie to confirm the top-left back arrow is visible, auto-hides in 3 seconds, and vanishes on screen tap.
- Test offline state to verify the multi-color glowing offline screen.
