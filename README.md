# FFmpeg Kit Android Distribution

This repository contains prebuilt FFmpeg Kit AAR files with GPL support for Android.

## Usage

Add this to your app's `build.gradle`:

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.StudioLapsus:lapsus-ffmpeg-android:6.0.0'
}
```

## Building

1. Copy your built `ffmpeg-kit-full-gpl.aar` to `ffmpeg-kit-full-gpl/libs/`
2. Run `./gradlew build`
3. Create a GitHub release with tag `6.0.0`
4. JitPack will automatically build and host the dependency

## Version History

- **6.0.0**: Initial release with FFmpeg Kit 6.0 GPL support 