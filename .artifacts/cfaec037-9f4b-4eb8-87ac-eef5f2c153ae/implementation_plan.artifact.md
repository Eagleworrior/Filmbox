# Implementation Plan - Bottom Navigation Bar, Library Tab (Movies/Series/Football), Film Box Rebrand & High-Graphic Community Styling

Restructure Film Box UI: Rename brand from "Movie Box" to **"Film Box"** everywhere, move navigation from the top header to a **Fixed Bottom Navigation Bar** with high-graphic box tabs (**🏠 Home**, **📚 Library** with sub-tabs for Movies/Series, **📰 News**, **⚽ Football**, **❤️ Community**), ensure the **★ Go VIP** button stays at the top left of every page, and style all text with vibrant gradients and colors.

## User Review Required

> [!IMPORTANT]
> - **Brand Rebrand to "Film Box"**: All titles, headers, and VIP passes now read **"Film Box"** and **"Film Box VIP Passes"**.
> - **Fixed Bottom Navigation Bar**: Replaced top navigation with a modern, high-graphic **Fixed Bottom Navigation Bar** containing gorgeous box tabs:
>   1. **🏠 Home**
>   2. **📚 Library** (Contains sub-selectors for Movies, Series, and Football at the top)
>   3. **📰 News**
>   4. **⚽ Football**
>   5. **❤️ Community** (Social media hub)
> - **Top-Left "★ Go VIP" Button**: Stays fixed at the top left of the screen on every page.
> - **Richly Colored & Decorated Text**: Applied vibrant multi-color gradients and glowing badges across all cards, news items, and VIP passes.

## Proposed Changes

### Web Application UI & Navigation Redesign
#### [MODIFY] [index.html](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/assets/index.html)
- **Rename to Film Box**: Update logo and text.
- **Fixed Bottom Tab Bar**: Add `.bottom-nav` CSS and HTML structure with 5 high-graphic box tabs.
- **Library Tab (`library`)**: Implement Library view containing sub-tabs for Movies, Series, and Football.
- **Top-Left VIP Button**: Position `"★ Go VIP"` at top left of header.
- **Rich Coloring**: Ensure all text elements are styled with vibrant colors and gradients.

## Verification Plan

### Automated Tests
- Run Gradle build `app:assembleDebug` to verify clean compilation.

### Manual Verification
- Test bottom navigation bar tabs (Home, Library, News, Football, Community) to ensure smooth transitions.
- Verify "Film Box" branding across header and VIP modal.
- Test top-left "★ Go VIP" button on all pages.
