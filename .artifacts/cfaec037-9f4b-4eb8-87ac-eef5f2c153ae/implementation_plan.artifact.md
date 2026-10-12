# Kenya Movie Box - Ultimate UI/UX & Player Refinement Plan

We are implementing a comprehensive set of improvements inside `app/src/main/assets/index.html` to make Kenya Movie Box world-class, perfectly arranged, and bug-free:

---

## User Review Required

> [!IMPORTANT]
> **Key Enhancements & Fixes**
> 1. **Status Bar Safe Area Padding (`pt-12` / `safe-area-inset-top`)**:
>    - Added top padding to the header so the app title, logo, and "PASS ACTIVE" badge sit comfortably below the phone's status bar (time and battery percentage) and are super easy to tap.
> 2. **Prominent Top Search Bar with Instant Auto-Suggestions Dropdown**:
>    - Fixed search bar at the very top of the header. As you type even a single letter, a live dropdown instantly shows matching movie suggestions with thumbnail posters, titles, ratings, and years.
> 3. **App Icon & Branding Clean-up**:
>    - Added the app icon to the top header alongside "DAILY PASS CINEMA" and removed the Kenyan flag emoji as requested.
> 4. **Dedicated Offline Downloads Tab & Offline Shelf**:
>    - Added an "📥 Downloads" tab in the header. When users download movies, they appear in this offline shelf and can be watched anytime without internet.
> 5. **White Screen Fix on App Resume**:
>    - Added auto-reload and state preservation handlers so returning from another app instantly restores the UI state instead of showing a white screen.
> 6. **Enhanced Video Player with Rotation & Episode Picker**:
>    - In the video player modal, added a rotation / full-screen button, seamless Server Switcher, and intuitive Season/Episode selector for TV series.

---

## Proposed Changes

### Assets (`app/src/main/assets`)
- **`index.html`**:
  - Add status bar safe-area padding (`pt-10 sm:pt-4`).
  - Fix header branding (App Icon + "DAILY PASS CINEMA", no flag).
  - Add instant search auto-suggestions dropdown.
  - Add dedicated **Downloads** tab in header for offline management.
  - Enhance video player modal with rotation, episode picker, and white-screen recovery.

---

## Verification Plan

### Automated Tests
- Gradle debug build (`gradle_build("app:assembleDebug")`).

### Manual Verification
- Deploy APK and verify:
  1. Header sits below status bar with zero overlap.
  2. Search bar at top shows instant auto-suggestions dropdown as you type.
  3. App icon is at top with "DAILY PASS CINEMA" (no flag).
  4. Video player has rotation control, season/episode picker, and smooth streaming.
  5. Downloads tab correctly lists offline saved movies.
