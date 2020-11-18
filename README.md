# Android Wear OS watch face with Test Harness

An Android Wear OS template re-imagined with testing harness. The harness does what the emulator seems to lack

The goal is to demonstrate how to create
- a testing harness for Wear OS watch faces
- an architecture for separating concerns
- utilise kotlin as a development language

The reason for the harness is that the emulator is not really "fit for purpose" when it comes to the visual aspect of creating a watch face. The watch face platform seems to have been branched off the idea of [Android wallpapers][WALLPAPER]. This has several issues when it comes to creating a watch face with an emulator:
- Wallpapers halt refreshing after 10-15 seconds, making it difficult to tell if the Watch Face is working or not
- Pushing "Back" or sending `adb shell input keyevent KEYCODE_WAKEUP` in order to get display to wake up
- Setting the time is difficult. `adb shell su root date $(date +%m%d%H%M%Y.%S)`
- Learning about the different "modes" that a face might be in is difficult, given that the template `MyWatchFace` class:
  - Is over 500 lines long
  - Has an inner class `Engine`, which handles both user control and drawing
  - Cannot be controlled in the emulator, and are difficult to engage on a real device

The app uses [Hilt][HILT] for injecting the dependency - this enables developers to create build flavours with different watch faces. This is done in the [di][DIFOLDER] folder. Please review the [Hilt example code][HILTEXAMPLE] or go to the [Dagger site][HILT2] to read more

The harness app also uses two different layout modes, one for landscape tablets that is quite basic, and a phone view that uses a rudimentary implementation of [side sheets][SIDESHEET]. This was really great learning for [motion layout][MOTIONLAYOUT] as well.

Feel free to fork this repository and create your own faces.

### Changes
1. [Changes from template to basic architecture solution][PULL1]
1. [Move WatchFaceRenderer and example into separate module][PULL2]

### Tasks
|TYPE|EPIC|TASK|DESCRIPTION|STATUS|
|:--:|:---|:---|:----------|-----:|
| TASK | Watch Face App | Create default app |  | :ballot_box_with_check: |
| TASK | Watch Face App | Extract interface | Create an interface separate from Engine | :ballot_box_with_check: |
| TASK | Watch Face App | Scale face | Ensure tick marks scale with width / height | :ballot_box_with_check: |
| BUG | Watch Face App | Scale face | Ensure images scale with width / height | :ballot_box_with_check: |
| TASK | Renderer Module | Create dependency module | Empty module creation | :ballot_box_with_check: |
| TASK | Renderer Module | Create dependency module | Extract image renderer interface | :ballot_box_with_check: |
| TASK | Renderer Module | Create dependency module | Extract image renderer example | :ballot_box_with_check: |
| TASK | Renderer Module | Create dependency module | Inject renderer with hilt | :ballot_box_with_check: |
| TASK | Harness Module | Create harness module | Empty module creation | :ballot_box_with_check: |
| TASK | Harness Module | Create harness module | Create screens for tablet and phone | :ballot_box_with_check: |
| TASK | Harness Module | Create harness module | Create watch view | :ballot_box_with_check: |
| TASK | Harness Module | Create harness module | Create views for options | :ballot_box_with_check: |
| TASK | Harness Module | Create harness module | Inject dependency from renderer module | :ballot_box_with_check: |
| TASK | Harness Module | Harness: Wire up ViewModel | Display options  | :construction: |
| TASK | Harness Module | Harness: Wire up ViewModel | Time options  | :pushpin: |
| TASK | Harness Module | Harness: Wire up ViewModel | Support Square and Round mode  | :pushpin: |
| TASK | Harness Module | Harness: Wire up ViewModel | Support 24-Hour mode  | :pushpin: |

[WALLPAPER]: https://developer.android.com/reference/android/service/wallpaper/WallpaperService
[HILT]: https://developer.android.com/training/dependency-injection/hilt-android
[HILTEXAMPLE]: https://github.com/android/architecture-samples/tree/dev-hilt
[DIFOLDER]: ./app/src/main/java/com/balsdon/watchapplication/di/WatchFaceModule.kt
[PULL1]: https://github.com/qbalsdon/wearOS/pull/1
[PULL2]: https://github.com/qbalsdon/wearOS/pull/2
[SIDESHEET]: https://material.io/components/sheets-side#specs
[MOTIONLAYOUT]: https://developer.android.com/training/constraint-layout/motionlayout
[HILT2]: https://dagger.dev/hilt/
