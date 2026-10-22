# Implementation Plan - Official Brand Colors for Community Hub & Removal of Live Sports (Football)

Remove the Live Sports (Football) tab and feature set from the app; upgrade the Community Hub (`socialCommunityHtml()`) with official brand colors, SVGs, and glowing neon CSS gradients for Facebook, WhatsApp, Instagram, and PayPal; and ensure clean navigation across Home, Movies, Series, and Library (News & Community).

## User Review Required

> [!IMPORTANT]
> - **Removal of Live Sports (Football)**: Completely removed the Football match API and Live Sports tab from the app and bottom navigation / Library sub-tabs as requested.
> - **Official Brand Colors & High-Graphic CSS**: Upgraded the Community Hub cards with authentic brand gradients and glowing icons:
>   - **Facebook Page & Group**: Official Facebook Blue (`#1877f2` / `#0084ff`).
>   - **WhatsApp Lounge**: Official WhatsApp Green (`#25d366`).
>   - **Instagram**: Official Instagram Gradient (`#f58529` to `#dd2a7b` to `#8134af`).
>   - **PayPal Support**: Official Gold/Blue (`#f59e0b` / `#0070ba`).
> - **Streamlined Library Tab**: Library view now focuses on **News** and **Community** for a cleaner, professional movie app experience.

## Proposed Changes

### Web Application UI Engine
#### [MODIFY] [index.html](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/assets/index.html)
- **Remove Football**: Remove football match loading functions, API tokens, and library sports sub-tabs.
- **Streamline Library Tab**: Library view now contains News and Community sub-tabs.
- **Brand Colors & Hub Styles**: Ensure all community cards use authentic brand colors, SVG icons, and high-graphic styling.

## Verification Plan

### Automated Tests
- Run Gradle build `app:assembleDebug` to verify clean compilation.

### Manual Verification
- Test opening the Library tab to verify Football is removed and Community / News are present.
- Verify community cards display official brand colors and icons.
