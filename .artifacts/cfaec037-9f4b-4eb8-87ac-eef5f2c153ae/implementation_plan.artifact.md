# Implementation Plan - Central Master Film List, Individual Direct Links & Queue Download Engine

Consolidate all films into a single master list in `index.html` where each film has its own `id`, `title`, `poster`, `description`, and individual `videoUrl` (using alternating temporary test links A and B). Ensure downloading any film stores it under its own ID in `FilmBoxDB`, prevents duplicate downloads with a "Downloaded" status badge, queues multiple downloads, validates URLs via pre-flight checks, and plays offline in airplane mode.

## User Review Required

> [!IMPORTANT]
> - **Central Master Film List**: Created a single master array `MASTER_FILMS` in `index.html` where every film has its own unique `id`, `title`, `poster`, `description`, and `videoUrl` (alternating between Link A and Link B).
> - **Per-Film IndexedDB Storage**: Each downloaded film is stored under its own unique ID in `FilmBoxDB`, ensuring zero conflicts or overwrites.
> - **"Downloaded" State Badge**: Once a film is successfully completed, its download button automatically switches to `"Downloaded"` so it cannot be downloaded twice.
> - **Queue & Multiple Downloads**: Supports queuing multiple simultaneous downloads with real percentage progress (`QUEUED`, `DOWNLOADING`, `COMPLETED`, `FAILED`).
> - **Pre-Flight Validation**: Validates each film's video link before adding to downloads. If invalid or missing, shows `"Not available for download"`.

## Proposed Changes

### Web Application & Master Data Layer
#### [MODIFY] [index.html](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/assets/index.html)
- **`MASTER_FILMS` Array**: Define the single central list of films with individual `videoUrl` properties using alternating temporary test links A and B.
- **Per-Film Download & Playback**: Update `startRealDownload(id)` and `playOffline(id)` to operate on specific film IDs.
- **"Downloaded" Badge**: Check if `status === "COMPLETED"` and show `"Downloaded"` instead of the download button.
- **Pre-Flight Validation**: Verify URL with `fetch(..., {method: 'HEAD'})` before queueing.

## Verification Plan

### Automated Tests
- Run Gradle build `app:assembleDebug` to verify clean compilation.

### Manual Verification
- Test downloading three different films, turning on airplane mode, and playing all three offline.
- Verify download buttons change to "Downloaded" once completed.
