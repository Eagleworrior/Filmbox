# Codebase-Wide Defensive Hardening & Crash Prevention Plan

We are conducting a comprehensive defensive programming audit and hardening pass across all activities (`DetailActivity`, `DownloadsActivity`, `PlayerActivity`, `MainActivity`), adapters, and network clients to ensure zero runtime exceptions, null pointer crashes, or "keeps stopping" dialogs anywhere in **FilmBox**.

---

## User Review Required

> [!IMPORTANT]
> **Defensive Hardening Strategy**
> 1. **Defensive Null-Safety & Try-Catch Wrappers**:
>    - Wrap all lifecycle methods (`onCreate`, `onResume`, network callbacks, adapter bindings) in robust try-catch exception blocks.
>    - Add explicit null checks for every `findViewById`, intent extra, and view binding.
> 2. **Network & Image Failure Isolation**:
>    - Isolate all Retrofit API calls and Coil image loaders so network drops or missing resources fail silently without crashing the main application thread.
> 3. **Global Uncaught Exception Handler**:
>    - Ensure `FilmBoxApplication` catches any residual thread exception.

---

## Proposed Changes

### Activities & Adapters (`com.filmbox`)
- **`DetailActivity.java`**: Wrap `onCreate`, intent parsing, and network callbacks in defensive try-catch guards.
- **`DownloadsActivity.java`**: Wrap offline storage loading and RecyclerView bindings in try-catch guards.
- **`PlayerActivity.java`**: Wrap WebView initialization, intent extras, and stream loaders in defensive try-catch blocks.

---

## Verification Plan

### Automated Tests
- Gradle debug build (`gradle_build("app:assembleDebug")`).

### Manual Verification
- Verify build success with zero errors.
- Confirm app launches smoothly and all activities handle navigation robustly.
