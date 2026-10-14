# Implementation Plan - Fix M-Pesa Invalid Amount & Restore CSS Grid Layouts

Fix the M-Pesa Daraja `invalid Amount` error (caused by commas in price values like `"2,500"`) and restore robust Tailwind CSS styling to fix the vertical text layout collapse.

## User Review Required

> [!IMPORTANT]
> - **M-Pesa Amount Cleaning**: Cleaned amount parsing in `triggerMpesaPayment()` to remove commas and non-numeric characters (e.g., converting `"2,500"` to `"2500"`), resolving Safaricom's `bad request - invalid Amount` error.
> - **Restore Tailwind CSS CDN & Robust Grid Styles**: Restored `@tailwindcss/browser@4` CDN script and added comprehensive native CSS rules in `<style>` for `.movie-grid`, `.horizontal-row`, `.movie-card`, etc., ensuring the UI never collapses into vertical text.

## Proposed Changes

### Web UI & M-Pesa Fixes
#### [MODIFY] [index.html](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/assets/index.html)
- **Amount Sanitization**: Clean `amountVal` using `.replace(/[^0-9]/g, '')` before passing to `AndroidKYC.triggerMpesaSTKPush`.
- **Restore Tailwind CDN Script**: Add back `@tailwindcss/browser@4` script.
- **Robust Grid CSS Fallbacks**: Ensure native CSS handles grids and flex wrapping even if offline.

## Verification Plan

### Automated Tests
- Gradle build `app:assembleDebug`.

### Manual Verification
- Deploy app to verify gorgeous dark cinema CSS styling and movie grid layout.
- Test M-Pesa payment with various VIP prices to ensure no `invalid Amount` errors occur.
