# Implementation Plan - Play Protect Safety, Instant Typing Search & Runtime Error Elimination

Clean up high-risk manifest permissions to resolve Google Play Protect "App Blocked / Harmful" warnings, implement real-time instant typing search in `index.html`, and silence non-fatal script error dialogs on startup.

## User Review Required

> [!IMPORTANT]
> - **Google Play Protect Compliance (`AndroidManifest.xml`)**:
>   - Remove deprecated high-risk storage permissions (`WRITE_EXTERNAL_STORAGE`, `READ_EXTERNAL_STORAGE`) that trigger Play Protect flags on modern Android 10-15 (API 29-35). App storage uses Android's private app directory (`getFilesDir()`), which requires zero risk permissions.
> - **Instant Real-Time Typing Search (`index.html`)**:
>   - Movies and TV shows will appear instantly in real-time as the user types *each letter* (0ms local match + fast TMDb cloud query).
> - **Runtime Error Elimination (`index.html`)**:
>   - Non-fatal third-party script exceptions will be caught silently without displaying red error boxes on screen.

## Proposed Changes

### Manifest Permission Safety (`AndroidManifest.xml`)

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/AndroidManifest.xml)
- Remove `WRITE_EXTERNAL_STORAGE` and `READ_EXTERNAL_STORAGE` permissions to ensure clean Play Protect compliance.

### Web Asset Real-Time Search & Error Prevention (`index.html`)

#### [MODIFY] [index.html](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/assets/index.html)
- Update `searchInput.oninput` to perform instant local matching on *every single keystroke*, followed by TMDb live API queries.
- Update `window.onerror` to log minor exceptions gracefully to console rather than interrupting the user interface.

## Verification Plan

### Automated Tests
- Build project with Gradle (`app:assembleDebug`) to verify syntax, permission compilation, and clean APK output.

### Manual Verification
- Deploy app, verify Play Protect scans cleanly without high-risk permission warnings.
- Type queries letter-by-letter in search input `#q` and confirm search results update dynamically on every keystroke.
- Confirm app launches smoothly with zero red runtime error dialogs.
