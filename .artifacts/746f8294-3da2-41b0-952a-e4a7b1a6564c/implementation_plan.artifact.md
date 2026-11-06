# Implementation Plan - Fix JS ReferenceError, Mobile Domain Layout & PayPal Button Integration

This plan resolves the startup JavaScript error, fixes mobile layout clipping in the Domain & Hosting Hub, and connects PayPal checkout:

1. **Fix Startup JS ReferenceError (`app/src/main/assets/index.html`)**:
   - **Root Cause Identified**: `if (appInitialized) return;` in `initApp()` threw `Uncaught ReferenceError: appInitialized is not defined` on initial load because `appInitialized` was missing global scope declaration.
   - **Fix Applied**: Declare `var appInitialized = false;` in global script scope. Home page movies will now render instantly on app startup without requiring tab switching.

2. **Fix Mobile Domain & Hosting Hub Layout (`app/src/main/assets/index.html`)**:
   - **Mobile Layout Responsive Fix**: Stack the domain input field and search button vertically (`flex-direction: column; width: 100%`) so the "Check Domain" button is 100% visible, centered, and perfectly formatted on all phone screens.
   - **PayPal Button Checkout**: Update `renderPayPalButton(amount)` to render a native PayPal checkout button calling `payWithPayPal()`, opening PayPal's secure checkout URL directly via `AndroidPayPal.openPayPalUrl(...)`.

3. **Background Ad-Blocking (`PlayerActivity.java` & `MainActivity.java`)**:
   - Intercept and block known popunder ad networks (`popads`, `adsterra`, `exoclick`, `propellerads`, `doubleclick`) while allowing video player sub-resources (`vidsrc`, `vidlink`, `embed`, `m3u8`, `ts`, `mp4`, `hls`).

4. **M-Pesa 100% Untouched (STRICT CONSTRAINT)**:
   - M-Pesa payment code in `MainActivity.java` (`MpesaBridge`) and `index.html` remains strictly locked and 100% untouched.

## User Review Required

> [!IMPORTANT]
> - **JS Error Fixed**: Defines `var appInitialized = false;`, eliminating the startup ReferenceError so movies load on initial app launch.
> - **Mobile Domain Layout Fixed**: Stacks input and button vertically so "Check Domain" is 100% visible and formatted on all phone screens.
> - **PayPal Checkout Connected**: Domain Hub PayPal button directly invokes native PayPal checkout.
> - **M-Pesa Locked**: M-Pesa payment code remains 100% untouched.

## Proposed Changes

### Assets & Frontend (`app/src/main/assets/`)

#### [MODIFY] [index.html](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/assets/index.html)
- Define `var appInitialized = false;` in global scope.
- Update `domainHostingHtml()` layout for mobile screens (`flex-direction: column`).
- Update `renderPayPalButton(amount)` to invoke `payWithPayPal()`.

### Java Source Code (`app/src/main/java/com/filmbox/`)

#### [MODIFY] [PlayerActivity.java](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/java/com/filmbox/PlayerActivity.java)
- Block popunder ad networks while passing through streaming sub-resources.

---

## Verification Plan

### Automated Tests
- Build project using Gradle:
  ```bash
  gradle_build(":app:assembleDebug")
  ```

### Manual Verification
- Open app, verify home page movies render immediately without ReferenceError. Open Domain & Hosting Hub, verify "Check Domain" button is 100% visible and formatted, and test PayPal checkout button.
