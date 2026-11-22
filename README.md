# NailSky 💅☁️

A nail art inspiration app for Android, built with Kotlin, Jetpack Compose and Material 3.

**Phase 1** covers the brand logo, launcher icon, splash screen, and main UI:

- **Home**: top bar with logo and wordmark, category chips (All, French, Floral, Glitter, Minimal, Ombre, Seasonal), a "Trending" carousel, and an "Inspiration" staggered grid
- **Explore**: placeholder for now
- **Saved**: the designs you've hearted (kept in memory)
- **Profile**: avatar placeholder and an Appearance setting (System / Light / Dark), saved with DataStore

All design cards are generated gradient tiles from local sample data, so Phase 1 needs no network access or image assets.

## Tech

| | |
|---|---|
| Language | Kotlin 2.0.21 |
| UI | Jetpack Compose (BOM 2024.12.01), Material 3, navigation-compose |
| Build | AGP 8.7.3, Gradle 8.9 (wrapper), Kotlin DSL + version catalog, Java 17 |
| SDK | minSdk 24, target/compileSdk 35 |
| Other | DataStore Preferences, core-splashscreen |

## Download APK from Actions → Artifacts

GitHub Actions builds the APK on every push and pull request to `main`. You can also start a build by hand.

1. Open the repo on GitHub and go to the **Actions** tab.
2. Select the latest successful **Android CI** run. To start a new one, choose **Run workflow**.
3. Scroll to **Artifacts** and download **`nailsky-debug-apk`**.
4. Unzip the download and install `app-debug.apk` on your device. You'll need to allow installs from unknown sources.

## Local build (optional)

```bash
./gradlew assembleDebug
```

The APK is written to `app/build/outputs/apk/debug/`.

## Project structure

```
app/src/main/java/com/nailsky/app/
  MainActivity.kt
  data/            NailDesign, SampleData, ThemePreferences
  ui/theme/        Color, Theme, Type
  ui/navigation/   NailSkyNavHost (bottom nav + routes)
  ui/components/   NailSkyTopBar, CategoryChips, DesignCard
  ui/screens/      Home, Explore, Saved, Profile
```

The in-app logo (`res/drawable/nailsky_logo.xml`) is separate from the launcher icon (`ic_launcher_foreground.xml`), so you can replace either one later, for example with a PNG.

## Out of scope (for now)

Real images, a detail screen, search logic, Room, and uploads.
