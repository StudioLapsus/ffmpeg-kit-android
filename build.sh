#!/bin/bash

set -e

# Function to check if a command exists
check_command() {
  if ! command -v "$1" &> /dev/null; then
    echo "❌ Error: $1 is not installed or not found in PATH"
    exit 1
  fi
}

# Function to check if required Android tools are available
check_android_tools() {
  if [ -z "$ANDROID_HOME" ]; then
    echo "❌ Error: Android SDK not found. Please set ANDROID_HOME"
    echo "💡 Install Android Studio or Android SDK and set the ANDROID_HOME environment variable"
    exit 1
  fi
}

echo "🔍 Checking prerequisites..."

# Check required tools
check_android_tools
check_command "git"
check_command "cmake"
check_command "yasm"
check_command "nasm"
check_command "automake"
check_command "libtool"
check_command "pkg-config"

echo "✅ Prerequisites met!"

# Absolute output path
OUTPUT_DIR="$(pwd)/output"

# Clone FFmpegKit if it doesn't exist
if [ ! -d "ffmpeg-kit" ]; then
  echo "📥 Cloning FFmpegKit..."
  if ! git clone --branch main git@github.com:StudioLapsus/ffmpeg-kit.git; then
    echo "❌ Error: Failed to clone FFmpegKit repository"
    exit 1
  fi
else
  echo "📁 FFmpegKit directory already exists, using existing clone"
fi

# Copy the prebuilt AAR files
echo "📦 Copying prebuilt AAR files to output directory..."
mkdir -p "$OUTPUT_DIR"

# Check for prebuilt AAR files
PREBUILT_AAR_DIR="ffmpeg-kit/prebuilt/bundle-android-aar"
if [ -d "$PREBUILT_AAR_DIR" ] && [ "$(ls -A $PREBUILT_AAR_DIR 2>/dev/null)" ]; then
  echo "📦 Found AAR files in: $PREBUILT_AAR_DIR"
  echo "📦 Copying all AAR files..."
  if ! cp -R "$PREBUILT_AAR_DIR"/* "$OUTPUT_DIR"/; then
    echo "❌ Error: Failed to copy AAR files to output directory"
    exit 1
  fi
  echo "🎉 Android AAR files copied successfully!"
  echo "📁 Output location: $OUTPUT_DIR/"

else
  echo "⚠️  No prebuilt AAR files found. Building from source..."
  echo "🔍 Checking what's available in prebuilt directory..."
  find ffmpeg-kit/prebuilt -name "*.aar" -type f 2>/dev/null || echo "No .aar files found"
  
  # Build AAR files from source
  echo "🛠️  Building FFmpegKit Android with GPL support..."
  echo "⏱️  This may take 45-90 minutes depending on your machine..."
  
  cd ffmpeg-kit
  
  # Clean up any previous modifications to android.sh
  if [ -f "android.sh.backup" ]; then
    echo "🔄 Restoring original android.sh from backup..."
    mv android.sh.backup android.sh
  else
    # If no backup exists, check if android.sh looks modified and re-clone if needed
    if grep -q "NDK_APPLICATION_MK" android.sh 2>/dev/null; then
      echo "⚠️  android.sh appears to be modified, re-cloning fresh copy..."
      cd ..
      rm -rf ffmpeg-kit
      echo "📥 Re-cloning FFmpegKit for clean build..."
      if ! git clone --branch main git@github.com:StudioLapsus/ffmpeg-kit.git; then
        echo "❌ Error: Failed to clone FFmpegKit repository"
        exit 1
      fi
      cd ffmpeg-kit
    fi
  fi
  
  # Remove any temporary files from previous patches
  rm -f android.sh.tmp android.sh.tmp.tmp *.tmp
  
  # Fix the bitfield issue directly in the source code
  echo "🔧 Fixing bitfield conversion errors in source code..."
  
  # The problematic file that needs to be patched
  PROBLEM_FILE="android/ffmpeg-kit-android-lib/src/main/cpp/fftools_ffmpeg_mux_init.c"
  
  # Check if we need to patch the source file
  if [ -f "$PROBLEM_FILE" ]; then
    echo "📝 Patching $PROBLEM_FILE to fix bitfield issues..."
    
    # Create backup
    cp "$PROBLEM_FILE" "$PROBLEM_FILE.backup"
    
    # Show the problematic lines for debugging
    echo "🔍 Current problematic lines:"
    echo "File exists: $(ls -la "$PROBLEM_FILE" 2>/dev/null || echo 'NOT FOUND')"
    echo "Lines 300-310:"
    sed -n '300,310p' "$PROBLEM_FILE" 2>/dev/null || echo "Could not read lines 300-310"
    echo "Looking for ENC_STATS lines with 1:"
    grep -n "ENC_STATS_.*1" "$PROBLEM_FILE" 2>/dev/null || echo "No ENC_STATS lines with 1 found"
    
    # Use a more direct approach - create a proper patch
    cat > /tmp/bitfield_fix.patch << 'EOF'
--- a/android/ffmpeg-kit-android-lib/src/main/cpp/fftools_ffmpeg_mux_init.c
+++ b/android/ffmpeg-kit-android-lib/src/main/cpp/fftools_ffmpeg_mux_init.c
@@ -291,16 +291,16 @@ static const EncStatsComponent enc_stats_components[] = {
     [ENC_STATS_LITERAL]         = { LATENCY_NOT_AVAILABLE, "literal", 0, 0, print_str },
     [ENC_STATS_FILE_IDX]        = { LATENCY_NOT_AVAILABLE, "fidx",    0, 0, print_int },
     [ENC_STATS_STREAM_IDX]      = { LATENCY_NOT_AVAILABLE, "sidx",    0, 0, print_int },
-    [ENC_STATS_FRAME_NUM]       = { LATENCY_NOT_AVAILABLE, "n",       0, 0, 1         },
-    [ENC_STATS_FRAME_NUM_IN]    = { LATENCY_NOT_AVAILABLE, "ni",      0, 0, 1         },
+    [ENC_STATS_FRAME_NUM]       = { LATENCY_NOT_AVAILABLE, "n",       0, 0, 0         },
+    [ENC_STATS_FRAME_NUM_IN]    = { LATENCY_NOT_AVAILABLE, "ni",      0, 0, 0         },
     [ENC_STATS_TIMEBASE]        = { LATENCY_NOT_AVAILABLE, "tb",      0, 0, print_timebase },
-    [ENC_STATS_TIMEBASE_IN]     = { LATENCY_NOT_AVAILABLE, "tbi",     0, 0, 1         },
+    [ENC_STATS_TIMEBASE_IN]     = { LATENCY_NOT_AVAILABLE, "tbi",     0, 0, 0         },
     [ENC_STATS_PTS]             = { LATENCY_NOT_AVAILABLE, "pts",     0, 0, print_int64 },
-    [ENC_STATS_PTS_IN]          = { LATENCY_NOT_AVAILABLE, "ptsi",    0, 0, 1         },
-    [ENC_STATS_PTS_TIME_IN]     = { LATENCY_NOT_AVAILABLE, "ti",      0, 0, 1         },
-    [ENC_STATS_DTS]             = { LATENCY_NOT_AVAILABLE, "dts",     0, 1            },
-    [ENC_STATS_DTS_TIME]        = { LATENCY_NOT_AVAILABLE, "dt",      0, 1            },
-    [ENC_STATS_SAMPLE_NUM]      = { LATENCY_NOT_AVAILABLE, "sn",      1               },
-    [ENC_STATS_NB_SAMPLES]      = { LATENCY_NOT_AVAILABLE, "samp",    1               },
-    [ENC_STATS_PKT_SIZE]        = { LATENCY_NOT_AVAILABLE, "size",    0, 1            },
-    [ENC_STATS_BITRATE]         = { LATENCY_NOT_AVAILABLE, "br",      0, 1            },
-    [ENC_STATS_AVG_BITRATE]     = { LATENCY_NOT_AVAILABLE, "abr",     0, 1            },
+    [ENC_STATS_PTS_IN]          = { LATENCY_NOT_AVAILABLE, "ptsi",    0, 0, 0         },
+    [ENC_STATS_PTS_TIME_IN]     = { LATENCY_NOT_AVAILABLE, "ti",      0, 0, 0         },
+    [ENC_STATS_DTS]             = { LATENCY_NOT_AVAILABLE, "dts",     0, 0            },
+    [ENC_STATS_DTS_TIME]        = { LATENCY_NOT_AVAILABLE, "dt",      0, 0            },
+    [ENC_STATS_SAMPLE_NUM]      = { LATENCY_NOT_AVAILABLE, "sn",      0               },
+    [ENC_STATS_NB_SAMPLES]      = { LATENCY_NOT_AVAILABLE, "samp",    0               },
+    [ENC_STATS_PKT_SIZE]        = { LATENCY_NOT_AVAILABLE, "size",    0, 0            },
+    [ENC_STATS_BITRATE]         = { LATENCY_NOT_AVAILABLE, "br",      0, 0            },
+    [ENC_STATS_AVG_BITRATE]     = { LATENCY_NOT_AVAILABLE, "abr",     0, 0            },
 };
EOF
    
    # Skip the patch approach and use direct sed targeting the specific error lines
    echo "⚠️  Using direct line targeting approach..."
    
    # Target exactly lines 303 and 304 (the ones mentioned in the error)
    echo "🎯 Fixing line 303 (ENC_STATS_SAMPLE_NUM):"
    sed -i.backup303 '303s/1               }/0               }/' "$PROBLEM_FILE"
    
    echo "🎯 Fixing line 304 (ENC_STATS_NB_SAMPLES):"  
    sed -i.backup304 '304s/1               }/0               }/' "$PROBLEM_FILE"
    
    # Also fix any other similar patterns just in case
    echo "🔧 Applying comprehensive fixes:"
    sed -i.backupall 's/{ ENC_STATS_.*".*", *1 *}/{ ENC_STATS_.*".*", 0 }/g' "$PROBLEM_FILE"
    sed -i.backupall2 's/, 1[ ]*}/, 0               }/g' "$PROBLEM_FILE"
    sed -i.backupall3 's/"sn", *1/"sn",      0/g' "$PROBLEM_FILE"
    sed -i.backupall4 's/"samp", *1/"samp",    0/g' "$PROBLEM_FILE"
    
    echo "✅ Direct line fixes applied"
    
    # Show the result
    echo "🔍 Checking lines 303-304 after fix:"
    sed -n '303,304p' "$PROBLEM_FILE" 2>/dev/null || echo "Could not read fixed lines"
    
    # Clean up
    rm -f /tmp/bitfield_fix.patch
    
    echo "✅ Source code patched successfully"
  else
    echo "ℹ️  Source file not found"
  fi
  
  # Clean any previous build artifacts to ensure fresh build
  if [ -d "prebuilt" ]; then
    echo "🧹 Cleaning previous build artifacts..."
    rm -rf prebuilt
  fi
  
  # Run the build with custom configuration to handle compiler warnings
  if ./android.sh \
    --enable-gpl \
    --disable-arm-v7a-neon \
    --enable-android-media-codec \
    --enable-android-zlib; then
    echo "✅ Build completed successfully!"
    
    # Go back to original directory and copy the built AAR files
    cd ..
    if [ -d "$PREBUILT_AAR_DIR" ] && [ "$(ls -A $PREBUILT_AAR_DIR 2>/dev/null)" ]; then
      echo "📦 Copying newly built AAR files..."
      if ! cp -R "$PREBUILT_AAR_DIR"/* "$OUTPUT_DIR"/; then
        echo "❌ Error: Failed to copy AAR files to output directory"
        exit 1
      fi
      echo "🎉 Android AAR files built and copied successfully!"
      echo "📁 Output location: $OUTPUT_DIR/"
    else
      # Fallback to old location for backward compatibility
      echo "📦 Checking for AAR files in legacy location..."
      if [ -f "ffmpeg-kit/prebuilt-binaries/android-archives/ffmpeg-kit-release.aar" ]; then
        echo "📦 Found AAR file in legacy location, copying..."
        if ! cp ffmpeg-kit/prebuilt-binaries/android-archives/ffmpeg-kit-release.aar "$OUTPUT_DIR"/; then
          echo "❌ Error: Failed to copy AAR file to output directory"
          exit 1
        fi
        echo "🎉 Android AAR file copied successfully!"
      else
        echo "❌ Error: Build completed but no AAR files found"
        exit 1
      fi
    fi
  else
    echo "❌ Error: Failed to build FFmpegKit Android"
    exit 1
  fi
fi

# Optional cleanup
echo ""
echo "🧹 Cleaning up..."
if [ -d "ffmpeg-kit" ]; then
  echo "🗑️  The ffmpeg-kit repository ($(du -sh ffmpeg-kit | cut -f1)) can be safely deleted now."
  echo "💭 The AAR files are copied to the output directory."
  read -p "🤔 Would you like to delete the ffmpeg-kit directory to save space? (y/N): " -n 1 -r
  echo
  if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "🗑️  Deleting ffmpeg-kit directory..."
    rm -rf ffmpeg-kit
    echo "✅ Cleanup complete!"
  else
    echo "⏭️  Keeping ffmpeg-kit directory (you can delete it manually later)"
  fi
fi

echo ""
echo "🎯 Setup complete! You can now integrate the AAR files into your Android project."
echo "💡 The script copied these AAR files for use with React Native:"
echo "   - ffmpeg-kit-*.aar (FFmpeg Kit library with all dependencies)"

# Prepare GitHub repository structure
echo ""
echo "📦 Preparing GitHub repository structure..."
GITHUB_REPO_DIR="$OUTPUT_DIR/github-repo"
mkdir -p "$GITHUB_REPO_DIR/ffmpeg-kit-android/libs"

# Copy template files from the correct location
TEMPLATE_DIR="./github-repo-template"
if [ -d "$TEMPLATE_DIR" ]; then
  echo "📋 Copying template files..."
  cp -R "$TEMPLATE_DIR"/* "$GITHUB_REPO_DIR/"
elif [ -d "github-repo-template" ]; then
  echo "📋 Copying template files from current directory..."
  cp -R github-repo-template/* "$GITHUB_REPO_DIR/"
else
  echo "⚠️  Template files not found, skipping GitHub repo preparation"
fi

# Copy the AAR file to the GitHub repo structure
if [ -f "$OUTPUT_DIR/ffmpeg-kit-android.aar" ]; then
  echo "📦 Copying AAR to GitHub repo structure..."
  cp "$OUTPUT_DIR/ffmpeg-kit-android.aar" "$GITHUB_REPO_DIR/ffmpeg-kit-android/libs/"
  echo "✅ GitHub repository structure ready at: $GITHUB_REPO_DIR"
  echo ""
  echo "🚀 Next steps for GitHub setup:"
  echo "1. Create a new GitHub repository (e.g., 'lapsus-ffmpeg-android')"
  echo "2. Copy contents of $GITHUB_REPO_DIR to your repository"
  echo "3. Create a release with tag '6.0.0'"
  echo "4. JitPack will automatically build the dependency"
  echo "5. Use: implementation 'com.github.YourUsername:lapsus-ffmpeg-android:6.0.0'"
else
  echo "⚠️  AAR file not found, skipping GitHub repo preparation"
fi