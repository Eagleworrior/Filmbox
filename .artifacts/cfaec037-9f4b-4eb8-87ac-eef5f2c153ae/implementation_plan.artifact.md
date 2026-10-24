# Implementation Plan - Map Official PayPal Payment Links to VIP Plans

Integrate official individual PayPal Checkout links mapped to each VIP plan in `index.html` and update `MainActivity.java` `shouldOverrideUrlLoading` to allow seamless checkout redirection.

## User Review Required

> [!IMPORTANT]
> - **PayPal Plan Link Mapping**: Each selected VIP plan now routes to its exact official PayPal Checkout page:
>   - **Trial VIP Pass (p1 - KES 10)**: `https://www.paypal.com/ncp/payment/GGNT396SFSNCC`
>   - **Daily VIP Pass (p2 - KES 50)**: `https://www.paypal.com/ncp/payment/NUZ4E4JMHTB92`
>   - **Weekend VIP Pass (p3 - KES 120)**: `https://www.paypal.com/ncp/payment/LMANQFKU3MMNS`
>   - **Weekly Master VIP (p4 - KES 250)**: `https://www.paypal.com/ncp/payment/K8PHR3W33B83A`
>   - **Monthly VIP Pass (p8 - KES 750)**: `https://www.paypal.com/ncp/payment/6JY7ZUPLGM78U`
>   - **Annual Diamond VIP (p12 - KES 5500)**: `https://www.paypal.com/ncp/payment/V3V43NQ97MAWC`
>   - **Lifetime VIP Pass (p13 - KES 9999)**: `https://www.paypal.com/ncp/payment/EDTSB5JV9CC8G`
> - **Android Intent Authorization (`MainActivity.java`)**: Whitelists `paypal.com/ncp/` URLs to ensure smooth external browser launching.

## Proposed Changes

### Web Application UI Engine
#### [MODIFY] [index.html](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/assets/index.html)
- Add `PAYPAL_PLAN_URLS` map and update `payWithPayPal()` to redirect users to the exact PayPal payment link for their selected plan.

### Android Native Core
#### [MODIFY] [MainActivity.java](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/java/com/filmbox/MainActivity.java)
- Ensure `paypal.com/ncp/` checkout URLs are authorized in `shouldOverrideUrlLoading`.

## Verification Plan

### Automated Tests
- Run Gradle build `app:assembleDebug` to verify clean compilation.

### Manual Verification
- Test selecting each VIP plan and tapping "CREDIT / DEBIT CARD / PAYPAL" to verify redirection to the corresponding PayPal link.
