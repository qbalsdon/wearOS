# Android Wear OS watch face with Test Harness

An Android Wear OS template re-imagined with testing harness. The harness does what the emulator seems to lack

The goal is to demonstrate how to create
- a testing harness for Wear OS watch faces
- an architecture for separating concerns
- utilise kotlin as a development language
- minimise the barrier to entry for creating watch faces ([Google codelab][CODELAB])

The reason for the harness is that the emulator is not really "fit for purpose" when it comes to the visual aspect of creating a watch face. It was last [updated in January 2018][WEAROSEMULATOR] and is just a skin of a normal phone emulator. The watch face platform seems to have been branched off the idea of [Android wallpapers][WALLPAPER]. This has several issues when it comes to creating a watch face with an emulator:
- Wallpapers halt refreshing after 10-15 seconds, making it difficult to tell if the Watch Face is working or not
- Pushing "Back" or sending `adb shell input keyevent KEYCODE_WAKEUP` in order to get display to wake up
- Setting the time is difficult. `adb shell su root date $(date +%m%d%H%M%Y.%S)`
  - I have attempted scripting this with limited success
- Learning about the different "modes" that a face might be in is difficult, given that the template `MyWatchFace` class:
  - Is over 500 lines long
  - Has an inner class `Engine`, which handles both user control and drawing
  - Cannot be controlled in the emulator, and are difficult to engage on a real device
  - Uses deprecated [Handler][HANDLER] as part of the example and templates, a [known issue][HANDLERISSUE]

The app uses [Hilt][HILT] for injecting the dependency - this enables developers to create build flavours with different watch faces. This is done in the [di][DIFOLDER] folder. Please review the [Hilt example code][HILTEXAMPLE] or go to the [Dagger site][HILT2] to read more. The only issue with injecting a view that draws on the canvas is that it can't render in preview :disappointed:

The harness app also uses two different layout modes, one for landscape tablets that is quite basic, and a phone view that uses a rudimentary implementation of [side sheets][SIDESHEET]. This was really great learning for [motion layout][MOTIONLAYOUT] as well.

Feel free to fork this repository and create your own faces.

### Changes
1. [Changes from template to basic architecture solution][PULL1]
1. [Move WatchFaceRenderer and example into separate module][PULL2]
1. [Created a test harness with more granular controls][PULL3]
1. Create a build variant to demonstrate utility

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
| TASK | Harness Module | Harness: Wire up ViewModel | Display options  | :ballot_box_with_check: |
| TASK | Harness Module | Harness: Wire up ViewModel | Time options  | :ballot_box_with_check: |
| TASK | Harness Module | Harness: Wire up ViewModel | Support size changes  | :ballot_box_with_check: |
| TASK | Harness Module | Harness: Wire up ViewModel | Support Square and Round mode  | :ballot_box_with_check: |
| BUG | Harness Module | Harness | Display 12 instead of "00" | :ballot_box_with_check: |
| BUG | Harness Module | Harness: TimePickerView | Allow users to see the date | :ballot_box_with_check: |
| BUG | Harness Module | Harness: TimePickerView | Allow users to modify the date | :ballot_box_with_check: |
| TASK | Harness Module | Harness: Wire up ViewModel | Support 24-Hour mode  | :ballot_box_with_check: |
| TASK | Harness Module | Harness Activity | Inject the coroutineTimerTicker | :ballot_box_with_check: |
| TASK | Renderer Module | Build Flavours | Add an example (Not useful as DI does that work) | :x: |
| TASK | Renderer Module | Build Flavours | JetPack compose watch face (Jetpack compose not currently compatible with WatchFace engine) | :construction: |
| TASK | Watch Face App | Watch Face | Complications example | :pushpin: |
| TASK | Watch Face App | Watch Face | Remove the Handler in the engine | :ballot_box_with_check: |
| TASK | Project | General | Update to [ViewBinding][VIEWBINDING] | :ballot_box_with_check: |
| BUG | Harness | View | Calendar not showing / hiding | :ballot_box_with_check: |
| TASK | Harness | View | Go back in time option? | :pushpin: |

[WALLPAPER]: https://developer.android.com/reference/android/service/wallpaper/WallpaperService
[HILT]: https://developer.android.com/training/dependency-injection/hilt-android
[HILTEXAMPLE]: https://github.com/android/architecture-samples/tree/dev-hilt
[DIFOLDER]: ./app/src/main/java/com/balsdon/watchapplication/di/WatchFaceModule.kt
[PULL1]: https://github.com/qbalsdon/wearOS/pull/1
[PULL2]: https://github.com/qbalsdon/wearOS/pull/2
[PULL3]: https://github.com/qbalsdon/wearOS/pull/4
[SIDESHEET]: https://material.io/components/sheets-side#specs
[MOTIONLAYOUT]: https://developer.android.com/training/constraint-layout/motionlayout
[HILT2]: https://dagger.dev/hilt/
[WEAROSEMULATOR]: https://developer.android.com/wear/releases?authuser=3#Jan-25-2018-release
[HANDLER]: https://developer.android.com/reference/android/os/Handler
[HANDLERISSUE]: https://github.com/android/wear-os-samples/issues/45
[VIEWBINDING]: https://developer.android.com/topic/libraries/view-binding
[CODELAB]: https://developer.android.com/codelabs/watchface#0