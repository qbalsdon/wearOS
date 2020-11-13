# wearOS

An Android Wear OS template with testing harness

The goal is to demonstrate how to create
- a testing harness for Wear OS watch faces
- an architecture for separating concerns

The reason for the harness is that the emulator is not really "fit for purpose" when it comes to the visual aspect of creating a watch face. The watch face platform seems to have been branched off the idea of [Android wallpapers][WALLPAPER]. This has several issues when it comes to creating a watch face with an emulator:
- Wallpapers halt refreshing after 10-15 seconds, making it difficult to tell if the Watch Face is working or not
- Setting the time is difficult. `adb shell su root date $(date +%m%d%H%M%Y.%S)`
- Learning about the different "modes" that a face might be in is difficult, given that the template `MyWatchFace` class:
  - Is over 500 lines long
  - Has an inner class `Engine`, which handles both user control and drawing
  - Cannot be controlled in the emulator, and are difficult to engage on a real device

Feel free to fork this repository and create your own faces.

[WALLPAPER]: https://developer.android.com/reference/android/service/wallpaper/WallpaperService
