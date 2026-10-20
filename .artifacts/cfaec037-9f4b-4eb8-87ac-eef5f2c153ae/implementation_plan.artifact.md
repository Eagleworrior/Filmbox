# Implementation Plan - 4-Tab Bottom Navigation (Home, Movies, Series, Library with News/Community/Football), Full "Film Box" Rebrand

Refactor navigation to feature strictly 4 bottom tabs (**🏠 Home**, **🎬 Movies**, **📺 Series**, **📚 Library**), where Library contains sub-tabs for **📰 News**, **❤️ Community**, and **⚽ Live Sports**, and eliminate any remaining instances of "Movie Box" in favor of **"Film Box"**.

## User Review Required

> [!IMPORTANT]
> - **Strict 4-Tab Bottom Navigation**:
>   1. **🏠 Home** (`home`)
>   2. **🎬 Movies** (`movie`)
>   3. **📺 Series** (`tv`)
>   4. **📚 Library** (`library`)
> - **Library Sub-Tabs**: Tapping **📚 Library** displays top-level sub-tabs for:
>   - **📰 News**
>   - **❤️ Community** (Vibrant high-graphic social hub)
>   - **⚽ Live Sports** (Football matches & live scores)
> - **100% "Film Box" Rebrand**: Verified and replaced all remaining occurrences of "Movie Box" with "Film Box" in page titles, logo markup, and share texts.

## Proposed Changes

### Web Application UI & Navigation Restructure
#### [MODIFY] [index.html](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/assets/index.html)
- **Bottom Nav Bar**: Update to 4 tabs (`Home`, `Movies`, `Series`, `Library`).
- **Library View Sub-Tabs**: Implement `libraryView()` with sub-buttons for News (`news`), Community (`social`), and Football (`football`).
- **Rebrand Check**: Replace `<title>Movie Box...</title>`, `<span class="logo">Movie Box</span>`, and share text with "Film Box".

## Verification Plan

### Automated Tests
- Run Gradle build `app:assembleDebug` to verify clean compilation.

### Manual Verification
- Test 4 bottom tabs (Home, Movies, Series, Library) to ensure smooth transitions.
- Tap Library to verify sub-tabs (News, Community, Football) load correctly.
- Verify zero instances of "Movie Box" exist in code.
