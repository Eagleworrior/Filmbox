# Task List - App Launch Crash Resolution & Direct Home Feed Launch

- [x] Identified cause of app closing on launch: Theme mismatch between Material3 parent theme and MaterialComponents OutlinedBox TextInputLayout in login views throwing `IllegalArgumentException`
- [x] Changed parent theme in `themes.xml` and `values-night/themes.xml` to `Theme.MaterialComponents.DayNight.NoActionBar`
- [x] Configured `MainActivity.java` as primary launcher activity in `AndroidManifest.xml` and auto-logged in guest user session so app opens straight to Home Cinema Feed
- [x] Verified build (`gradle app:assembleDebug`) with 0 errors
- [x] Pushed commit directly to GitHub (`https://github.com/Eagleworrior/Filmbox.git`)
- [x] Updated walkthrough artifact
