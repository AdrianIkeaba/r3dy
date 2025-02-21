# R3DY - A 3D Model Viewer

A native Android application built using **Kotlin**, **Jetpack Compose**, and **SceneView** for rendering and interacting with 3D models.

## Features
- Load and display `.glb` and `.gltf` 3D models.
- Support for animations within the 3D model.
- Basic touch interaction (rotation, zoom, and pan).
- Optimized rendering using **Filament**.

## Technologies Used
- **Kotlin**: For Android app development.
- **Jetpack Compose**: For modern UI development.
- **SceneView**: For rendering 3D models.
- **Filament**: For high-performance rendering.

## Installation
1. Clone the repository:
   ```sh
   git clone https://github.com/AdrianIkeaba/r3dy.git
   cd r3dy
   ```
2. Open the project in **Android Studio**.
3. Sync dependencies and build the project.
4. Run the app on an Android device or emulator.

## Usage
1. Launch the app.
2. Load a 3D model (`.glb` or `.gltf`).
3. Interact with the model using touch gestures.
4. Play animations if available in the model.

## Dependencies
Ensure the following dependencies are added in `build.gradle.kts`:
```kotlin
dependencies {
    implementation("io.github.sceneview:sceneview:latest-version")
    implementation("io.github.sceneview:filament:latest-version")
}
```
(Replace `latest-version` with the latest SceneView and Filament versions.)


## Author
Developed by **Adrian**.


