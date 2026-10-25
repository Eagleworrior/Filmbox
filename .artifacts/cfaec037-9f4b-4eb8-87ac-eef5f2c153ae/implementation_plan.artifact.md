# Implementation Plan - Floating Rounded Dock Navigation Bar Matching Screenshot

Re-architect the bottom navigation bar into a **Floating Rounded Dock Bar** floating over content at the bottom of the screen, matching the exact 5-item layout and active icon box design from the user's screenshot.

## User Review Required

> [!IMPORTANT]
> - **Floating Rounded Dock Container**: `.floating-dock` floating 12px above the screen bottom with `border-radius: 28px`, `backdrop-filter: blur(20px)`, and `box-shadow: 0 15px 40px rgba(0,0,0,0.8)`.
> - **5-Item Dock Layout**:
>   1. 🏠 **Home**
>   2. 🗂️ **Library**
>   3. 🎬 **Movies**
>   4. 📥 **Download**
>   5. 👤 **Community**
> - **Active Pill Icon Box**: When selected, the active item's icon highlights inside a vibrant hot-pink rounded icon box (`background: #ff4d8d; box-shadow: 0 4px 15px rgba(255,77,141,0.5)`) with a bold white label below, matching the screenshot!

## Proposed Changes

### Web Application UI Engine
#### [MODIFY] [index.html](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/assets/index.html)
- Replace `.bottom-nav` with `.floating-dock` container and update CSS to render the floating rounded dock bar with active pill icon boxes.
- Update `go(v)` view router to handle 5 items (`home`, `library`, `movie`, `download`, `social`).

## Verification Plan

### Automated Tests
- Run Gradle build `app:assembleDebug` to verify clean compilation.

### Manual Verification
- Test opening the app to verify the floating rounded dock bar appears at the bottom.
- Test tapping each item (`Home`, `Library`, `Movies`, `Download`, `Community`) to verify active pill icon highlights and instant view routing.
