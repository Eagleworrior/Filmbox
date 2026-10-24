# Implementation Plan - Strict Real-Time Safaricom M-Pesa STK Polling & Verification

Eliminate premature VIP activation upon initial STK push dispatch; enforce strict real-time Safaricom polling (`queryMpesaSTKStatus`), activating VIP **only** when Safaricom confirms `ResultCode === 0`, and displaying live error feedback for insufficient balance, incorrect PIN, and phone cancellation.

## User Review Required

> [!IMPORTANT]
> - **Eliminate Premature VIP Activation**: `onMpesaSTKResponse` will no longer grant VIP access when Safaricom merely acknowledges receiving the STK request (`ResponseCode === "0"`).
> - **Real-Time Safaricom STK Status Polling**: `startMpesaPolling` queries Safaricom's `stkpushquery` API every 3 seconds while showing a dedicated full-screen waiting modal.
> - **Strict Real-Time Status Verification**:
>   - **`ResultCode === "0"` (Confirmed)**: VIP is activated (`mb_vip_expiry`), success screen displays.
>   - **`ResultCode === "1"` (Insufficient Funds)**: Shows live error *"M-Pesa Payment Failed: Insufficient Balance. VIP not activated."*
>   - **`ResultCode === "1032"` (Cancelled on Phone)**: Shows live error *"M-Pesa Prompt Cancelled on Phone. VIP not activated."*
>   - **`ResultCode === "1001" / "2001"` (Incorrect PIN / Error)**: Shows live error *"M-Pesa Payment Failed: Incorrect PIN or Transaction Error."*

## Proposed Changes

### Web Application UI Engine
#### [MODIFY] [index.html](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/assets/index.html)
- Update `onMpesaSTKResponse` to extract `CheckoutRequestID` and launch real-time Safaricom polling without granting VIP access.
- Implement `startMpesaPolling` with real-time `ResultCode` inspection and live user error messaging.

## Verification Plan

### Automated Tests
- Run Gradle build `app:assembleDebug` to verify clean compilation.

### Manual Verification
- Test M-Pesa STK push and verify that VIP is NOT activated upon initial prompt.
- Verify live polling messages and accurate feedback when cancelling or failing payment.
