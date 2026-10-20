# Implementation Plan - PayPal JavaScript SDK v6 & REST API Integration (Live USD Payments)

Integrate PayPal JavaScript SDK v6 (`https://www.paypal.com/web-sdk/v6/core`) and REST API using the user's Live Credentials (`lomalianorman@gmail.com`, Client ID: `BAA1dtlx0lmxerhVWLeU2FXAPXyAoQyFzrGWJIc_djIp8oT3y54X1sLX2GR1XWZnloh7yqTc4j57vB-bKQ`, Secret: `EIhtPvussfV5KhcSTIPj7gs2jcOeoQ6G6KW3uFE8YH9wqfFg8hu-m9YU5lBsS7pRdd2ZTR5lhnF5I-Hj`) to enable USD ($) payments alongside M-Pesa, with real-time order creation, payment capture, and instant VIP pass activation.

## User Review Required

> [!IMPORTANT]
> - **PayPal JavaScript SDK v6**: Configured `<script src="https://www.paypal.com/web-sdk/v6/core"></script>`.
> - **Java Native REST Token & Order Bridge (`MainActivity.java`)**:
>   - Generates browser-safe client token via PayPal OAuth REST API (`https://api-m.paypal.com/v1/oauth2/token`).
>   - Creates PayPal Orders (`https://api-m.paypal.com/v2/checkout/orders`) with exact USD amounts ($1.00, $2.50, $10.00, $25.00, $99.00).
>   - Captures PayPal Orders (`https://api-m.paypal.com/v2/checkout/orders/{id}/capture`).
> - **Dual Payment Selector**: Added payment tabs in `openVip()` so users choose **M-Pesa ⚡ (KES)** or **PayPal / Credit Card 💳 (USD $)**.
> - **USD Pricing Matrix**:
>   - Trial Pass (1 Hour): **$1.00 USD**
>   - Daily Pass (24 Hours): **$2.50 USD**
>   - Weekly Master (7 Days ⭐): **$10.00 USD**
>   - Monthly Pass (30 Days): **$25.00 USD**
>   - Lifetime VIP (Permanent): **$99.00 USD**
> - **Real-Time Payment Capture & VIP Activation**: Upon order capture completion (`status === "COMPLETED"`), VIP access is activated immediately in real-time.

## Proposed Changes

### Android Native Bridge
#### [MODIFY] [MainActivity.java](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/java/com/filmbox/MainActivity.java)
- Add `PayPalBridge` class with JavascriptInterface methods:
  - `getPayPalClientToken(callbackName)`
  - `createPayPalOrder(amountUsdStr, callbackName)`
  - `capturePayPalOrder(orderId, callbackName)`
- Uses `HttpURLConnection` for secure server-side PayPal REST API requests to `https://api-m.paypal.com`.

### Web Application UI & Payment Engine
#### [MODIFY] [index.html](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/assets/index.html)
- Include PayPal v6 SDK script (`https://www.paypal.com/web-sdk/v6/core`).
- Add payment method selector tabs in `openVip()` (M-Pesa vs PayPal).
- Connect `window.paypal.createInstance` and `createPayPalOneTimePaymentSession` returning `{ orderId }` on `createOrder()`.
- On `onApprove`, call `capturePayPalOrder` and activate VIP pass on `status === "COMPLETED"`.

## Verification Plan

### Automated Tests
- Run Gradle build `app:assembleDebug` to verify clean compilation.

### Manual Verification
- Open VIP modal to verify dual payment tabs (M-Pesa and PayPal USD).
- Test PayPal SDK v6 button initialization and USD pricing display.
