# Kenya Movie Box - UI Overhaul & Responsive Layout Plan

We are redesigning the frontend interface inside `app/src/main/assets/index.html` into a professional, Netflix/MovieBox-grade streaming layout that fixes all overlapping text and button alignment issues across all mobile devices.

---

## User Review Required

> [!IMPORTANT]
> **UI & Layout Fixes**
> 1. **Zero Text Overlaps**: Restructured the header, hero banner, and movie cards with proper Tailwind CSS flex/grid spacing and padding so title text and poster artwork never overlap.
> 2. **Visible & Responsive Category Tabs**: Added interactive category filter pills (✨ All, 🔥 Trending, 🇰🇪 Kenyan Hits, 🌍 African Movies, 🎬 Hollywood, 📺 Series, 💥 Action) right below the search bar.
> 3. **Instant 1-Tap Response**: All movie cards, Play buttons, and search results have precise touch targets (`cursor-pointer`, z-index layering) so they respond instantly on every tap.
> 4. **Clean Video Player Modal**: Full-screen cinema modal with embedded high-speed streaming (`vidlink.pro` / `vidsrc.to`), Server Switcher, and Close button.

---

## Proposed Changes

### Assets (`app/src/main/assets`)
- **`index.html`**: Completely overhauled Tailwind UI single-page application with responsive header, search bar, category filter pills, featured hero carousel, responsive movie cards grid, and video player modal.

---

## Verification Plan

### Automated Tests
- Gradle debug build (`gradle_build("app:assembleDebug")`).

### Manual Verification
- Deploy app and verify UI renders cleanly with zero text overlap, visible category tabs, and instant playback response on tapping movies.
