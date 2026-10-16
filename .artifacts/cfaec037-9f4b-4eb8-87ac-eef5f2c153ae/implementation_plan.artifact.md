# Implementation Plan - Native Android DownloadManager File Downloads & Player Back Button

Implement real, physical media file downloads directly into device storage using Android's native `DownloadManager` in `MainActivity.java`, while enforcing the 3 Free Downloads credit limit with KES 20 M-Pesa Download Pass, and refine the tap-to-show `<` Back arrow button on the video player.

## User Review Required

> [!IMPORTANT]
> - **Real Physical File Download Engine (`MainActivity.java`)**: Integrated Android's native system `DownloadManager`. Tapping `📥 Download` now triggers a real physical file download (`.mp4`) directly into your phone's Downloads directory with native status bar progress notifications.
> - **3 Free Downloads Limit & KES 20 Download Pass**: Maintained the credit check logic. After 3 real physical downloads are used (0/3 credits left), downloading any additional movie/episode prompts the KES 20 M-Pesa Download Pass.
> - **Tap-To-Show `<` Player Back Button**: Positioned a prominent `<` back arrow button at the top-left of the player control bar that appears when tapping the video screen and auto-fades after 3 seconds.

## Proposed Changes

### Android Native Download Engine
#### [MODIFY] [MainActivity.java](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/java/com/filmbox/MainActivity.java)
- Add `@JavascriptInterface public void downloadMediaFile(String fileUrl, String fileName)` using `DownloadManager.Request` to download physical `.mp4` files to device storage with status bar notifications.

### Web Application UI & Credits Logic
#### [MODIFY] [index.html](file:///C:/Users/EAGLE/AndroidStudioProjects/FilmBox/app/src/main/assets/index.html)
- Call `AndroidKYC.downloadMediaFile(url, title)` inside `startDownloadCurrent()` and `downloadEpisode()`.
- Ensure player control bar `.pbar` displays `<` back arrow icon button.

## Verification Plan

### Automated Tests
- Run Gradle build `app:assembleDebug` to verify clean compilation.

### Manual Verification
- Tap `📥 Download` on a title to verify Android's native system `DownloadManager` launches a real file download with status bar notification.
- Verify 3 free downloads credit countdown and KES 20 M-Pesa Download Pass prompt.
