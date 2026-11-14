# Implementation Plan - Refactor Package Name to `com.filmboxapp`

Refactor the Android application package name and namespace from `com.filmbox` to `com.filmboxapp` across build scripts, directory structures, Java package statements, and imports.

## User Review Required

> [!IMPORTANT]
> - **New Application ID & Namespace**: `com.filmboxapp`
> - **Java Package Refactoring**: Moves directory `app/src/main/java/com/filmbox` -> `app/src/main/java/com/filmboxapp` and updates all Java `package` and `import` statements.

## Proposed Changes

### Build Scripts & Manifest (`app/build.gradle.kts`, `AndroidManifest.xml`)
- Update `namespace = "com.filmboxapp"` and `applicationId = "com.filmboxapp"` in `app/build.gradle.kts`.
- Update activity and service action constants in `AndroidManifest.xml` / `TorrentDownloadService.java`.

### Java Source Code Refactoring (`app/src/main/java/com/filmboxapp/`)
- Rename directory structure `com/filmbox` to `com/filmboxapp`.
- Update package declarations (`package com.filmboxapp;`) and all references/imports (`import com.filmboxapp.R;`).

## Verification Plan

### Automated Tests
- Sync Gradle and build project (`app:assembleDebug`) to verify clean compilation with the new `com.filmboxapp` package name.

### Manual Verification
- Deploy app to device/emulator and verify package name is `com.filmboxapp`.
