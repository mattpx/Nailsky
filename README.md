# NailSky 💅☁️

A native nail art studio, lookbook, and salon passport app for Android — built with Kotlin, Jetpack Compose, Material 3, Room, and Coil.

## Features (v0.3.0 — Studio Edition)

- **Floating Pill Dock + Center Nail Studio**: Translucent floating bottom dock anchored by a glowing gradient **Studio** button, paired with editorial Serif headings and warm pearl light / glazed obsidian dark themes
- **Interactive Hand Studio**: Live custom-drawn 5-finger hand canvas where you can tap individual fingers (or paint all 5 at once), switch skin tones, morph nail shapes (`Almond`, `Coffin`, `Oval`, `Square`, `Stiletto`, `Short`), slide nail length (`Short` → `XL`), test 14 glossy polish shades and 6 live effects (`Glossy`, `Chrome`, `French`, `Aura`, `Glitter`, `Matte`), randomize looks, and save custom sets to Room
- **Editorial Lookbook (Home)**: Borderless full-bleed photography with a **Look of the Week** hero card (with 1-tap `Try Studio`), **Today's Nail Mood** glossy 3D polish droplets, **Visual Nail-Shape Silhouettes**, and animated photo-first masonry
- **Color-First Discover (Explore)**: Glossy **Pick Your Shade** palette bar, **Weekly Top Looks** (`#1–#6` rank badges), expandable Finish & Shape tray, Browse by Style collections, Featured Nail Artists, and live multi-keyword search
- **Design Detail + "Show My Nail Tech" Brief**: Full-bleed hero photo, 1-tap full-screen **Salon Brief** card ready to hand to your nail tech, glossy 3D **Polish Formula** droplets (tap to copy `#HEX`), `Try in Studio` hand-off, numbered salon steps, persisted personal notes, and Share sheet
- **2×2 Collage Moodboards & Custom Sets (Saved)**: Room-persisted moodboards with 2×2 photo-collage covers plus a **My Studio Sets** gallery (tap any saved set to reopen it in the Studio)
- **Nail Passport (Profile)**: Animated circular **Fill-In Countdown Ring** (*Day X of 21 · Fill in Y days*) with `Log Fresh Manicure`, **Daily Cuticle Care** streak check-in, Recently Viewed carousel, editable display name, and Appearance switcher

## Tech

| | |
|---|---|
| Language | Kotlin 2.0.21 + KSP |
| UI | Jetpack Compose (BOM 2024.12.01), Material 3, Custom Canvas Studio, navigation-compose |
| Images | Coil 2.7.0 (`coil-compose`) with gradient fallback |
| Persistence | Room 2.6.1 (saved looks, moodboards, custom studio sets, notes, history), DataStore Preferences (theme, display name, Nail Passport & cuticle streak) |
| Build | AGP 8.7.3, Gradle 8.9 (wrapper), Kotlin DSL + version catalog, Java 17 |
| SDK | minSdk 24, target/compileSdk 35 |
| Other | core-splashscreen, JUnit 4 unit tests |

## Download APK from Actions → Artifacts

GitHub Actions runs unit tests and builds the APK on every push and pull request to `main`.

1. Open the repo on GitHub and go to the **Actions** tab.
2. Select the latest successful **Android CI** run.
3. Scroll to **Artifacts** and download **`nailsky-debug-apk`**.
4. Unzip and install `app-debug.apk` on your device.

## Project structure

```
app/src/main/java/com/nailsky/app/
  MainActivity.kt
  data/
    NailDesign.kt, SampleData.kt, DesignFilter.kt
    ThemePreferences.kt, NailSkyRepository.kt
    local/           NailSkyEntities, NailSkyDao, NailSkyDatabase (Room v2)
  ui/theme/          Color, Theme, Type
  ui/navigation/     NailSkyNavHost (floating frosted dock + routes)
  ui/components/     NailSkyTopBar, CategoryChips, DesignCard, NailVisuals, BoardSheet
  ui/screens/        Home, Explore, Studio, Saved, Profile (Nail Passport), DesignDetail
app/src/test/java/com/nailsky/app/
  data/              DesignFilterTest
```
