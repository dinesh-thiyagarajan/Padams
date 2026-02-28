# Padams

A modern Android gallery application built with Jetpack Compose, Clean Architecture, and on-device face recognition. Padams provides a Google Photos-like experience with intelligent face grouping, album management, and smooth photo browsing with pagination.

---

## Screenshots

| Photos Grid | Albums | Favorites | People |
|:-----------:|:------:|:---------:|:------:|
| ![Photos](screenshots/photos.png) | ![Albums](screenshots/albums.png) | ![Favorites](screenshots/favorites.png) | ![People](screenshots/people.png) |

| Image Detail | Album Detail | Person Detail | Permissions |
|:------------:|:------------:|:-------------:|:-----------:|
| ![Detail](screenshots/detail.png) | ![Album Detail](screenshots/album_detail.png) | ![Person Detail](screenshots/person_detail.png) | ![Permissions](screenshots/permissions.png) |

> **Note:** Add screenshots to a `screenshots/` directory at the project root.

---

## Features

- **Photo Browsing** — Browse all device photos in a date-grouped grid with smooth pagination (handles 10,000+ images)
- **Zoomable Image Viewer** — Full-screen viewer with pinch-to-zoom and double-tap zoom toggle
- **Albums** — Create, manage, and delete custom photo albums
- **Favorites** — Mark and quickly access your favorite photos
- **Face Recognition & Grouping** — On-device ML pipeline detects faces, extracts embeddings, and clusters photos by person
- **Background Face Scanning** — Face detection runs in the background via WorkManager, surviving process death
- **Share & Delete** — Share photos to other apps or delete them from the device
- **Runtime Permissions** — Graceful permission handling with rationale and settings redirect
- **Edge Case Handling** — Loading, empty, and error states on every screen

---

## Architecture

Padams follows **Clean Architecture** with the **MVVM** pattern and **UseCases** as the bridge between presentation and data layers.

```
┌─────────────────────────────────────────────────┐
│                  Presentation                    │
│   (Compose Screens + ViewModels + Navigation)    │
├─────────────────────────────────────────────────┤
│                    Domain                        │
│       (Use Cases + Repository Interfaces         │
│              + Domain Models)                    │
├─────────────────────────────────────────────────┤
│                     Data                         │
│  (Repository Impls + MediaStore + Room + ML)     │
└─────────────────────────────────────────────────┘
```

### Dependency Flow

```
ViewModels → UseCases → Repository Interfaces (domain)
                              ↑
                   Repository Implementations (data)
                      ↓              ↓
                 MediaStore     Room Database
```

---

## Module Structure

All modules are flat — no nested grouping — each sitting at the same level as the `:app` module.

```
Padams/
├── app/            → Application, DI modules, Navigation, Theme, Permission screen
├── common/         → Shared utilities (DateFormatter)
├── domain/         → Domain models, repository interfaces, use cases
├── database/       → Room entities, DAOs, database, type converters
├── data/           → Repository implementations, MediaStore data sources
├── ui/             → Shared Compose components (PhotoGridItem, ZoomableImage, etc.)
├── ml/             → Face detection, embedding extraction, clustering, WorkManager
├── photos/         → Photos screen + ViewModel (paginated grid with date headers)
├── albums/         → Albums screen, Album detail, Create album dialog + ViewModels
├── favorites/      → Favorites screen + ViewModel
├── people/         → People screen, Person detail + ViewModels (face groups)
└── detail/         → Image detail screen + ViewModel (zoomable viewer)
```

### Module Dependency Graph

```
app ──→ all modules (wires DI)

photos   ──→ domain, ui, common
albums   ──→ domain, ui
favorites──→ domain, ui
people   ──→ domain, ui, ml
detail   ──→ domain, ui

data     ──→ domain, database
ml       ──→ domain, database
ui       ──→ domain
```

Feature modules **never** depend on `data` directly — they access data through domain use cases injected by Hilt.

---

## Tech Stack

| Category | Library | Version |
|----------|---------|---------|
| **Language** | Kotlin | 2.0.21 |
| **UI Framework** | Jetpack Compose (BOM) | 2024.09.00 |
| **Design System** | Material 3 | 1.3.1 |
| **DI** | Hilt | 2.51.1 |
| **Database** | Room | 2.6.1 |
| **Pagination** | Paging 3 | 3.3.2 |
| **Image Loading** | Coil | 2.7.0 |
| **Navigation** | Navigation Compose | 2.8.4 |
| **Face Detection** | ML Kit Face Detection | 16.1.7 |
| **Face Embeddings** | TensorFlow Lite | 2.14.0 |
| **Background Work** | WorkManager | 2.9.1 |
| **Permissions** | Accompanist Permissions | 0.36.0 |
| **Coroutines** | Kotlin Coroutines | 1.8.1 |
| **Annotation Processing** | KSP | 2.0.21-1.0.28 |

**Android SDK:** minSdk 24 · targetSdk 36 · compileSdk 36 · Java 11

---

## Face Recognition Pipeline

Padams uses a fully on-device ML pipeline for face recognition — no cloud APIs, no data leaves the device.

```
Image
  │
  ▼
┌──────────────────────┐
│  ML Kit Face Detector │  ← Detects face bounding boxes
│  (PERFORMANCE_MODE_   │    (min face size: 0.1)
│   ACCURATE)           │
└──────────┬───────────┘
           │ Cropped faces (112×112 px, 20% padding)
           ▼
┌──────────────────────┐
│  MobileFaceNet       │  ← TFLite model extracts 192-dim
│  (mobilefacenet.     │    face embeddings, L2-normalized
│   tflite)            │
└──────────┬───────────┘
           │ Embedding vectors
           ▼
┌──────────────────────┐
│  Agglomerative       │  ← Clusters embeddings by cosine
│  Clustering          │    similarity (threshold: 0.65,
│  (Average Linkage)   │    average linkage)
└──────────┬───────────┘
           │
           ▼
     Face Groups (People)
```

- **FaceScanWorker** processes images in batches of 20, downscaling to 1024px max
- Runs via **WorkManager** with battery constraints and `KEEP` existing work policy
- Results stored in Room DB (`FaceGroupEntity`, `FaceOccurrenceEntity`, `ProcessedImageEntity`)

> **Note:** The `ml/src/main/assets/mobilefacenet.tflite` file is a placeholder. Replace it with a real [MobileFaceNet](https://github.com/sirius-ai/MobileFaceNet_TF) model for face recognition to work.

---

## Screens & Navigation

### Bottom Navigation (4 tabs)

| Tab | Route | Description |
|-----|-------|-------------|
| Photos | `/photos` | Date-grouped photo grid with Paging 3 pagination |
| Albums | `/albums` | Album grid with FAB for creating new albums |
| Favorites | `/favorites` | Grid of favorited photos |
| People | `/people` | Face group circles with scan trigger |

### Detail Screens

| Screen | Route | Description |
|--------|-------|-------------|
| Image Detail | `/image/{uri}` | Full-screen zoomable viewer with share/favorite/delete |
| Album Detail | `/album/{id}` | Photo grid within a specific album |
| Person Detail | `/person/{id}` | Photos of a specific person, with rename support |
| Permission | `/permission` | Runtime permission request with rationale |

---

## Database Schema

```
┌─────────────────┐     ┌──────────────────┐     ┌─────────────────┐
│  FavoriteEntity  │     │   AlbumEntity    │     │ AlbumImageEntity│
│─────────────────│     │──────────────────│     │─────────────────│
│ imageUri (PK)   │     │ id (PK, auto)    │     │ albumId (PK,FK) │
│ addedAt         │     │ name             │     │ imageUri (PK)   │
└─────────────────┘     │ coverImageUri    │     │ addedAt         │
                        │ photoCount       │     └─────────────────┘
                        │ createdAt        │
                        │ updatedAt        │
                        └──────────────────┘

┌─────────────────┐     ┌───────────────────┐    ┌──────────────────┐
│ FaceGroupEntity  │     │FaceOccurrenceEntity│   │ProcessedImageEntity│
│─────────────────│     │───────────────────│    │──────────────────│
│ id (PK, auto)   │◄────│ id (PK, auto)     │    │ imageUri (PK)    │
│ name            │     │ groupId (FK)      │    │ processedAt      │
│ representativeUri│     │ imageUri          │    │ faceCount        │
│ centroidEmbedding│     │ faceRect          │    └──────────────────┘
│ photoCount      │     │ embedding         │
│ createdAt       │     │ confidence        │
└─────────────────┘     │ createdAt         │
                        └───────────────────┘
```

---

## Image Loading & Caching

Coil is configured with optimized caching in `PadamsApplication`:

- **Memory Cache:** 25% of available app memory
- **Disk Cache:** 250 MB at `image_cache/` directory
- **Crossfade:** Enabled for smooth transitions
- **Thumbnails:** 300px request size for grid items

---

## Pagination Strategy

Photos are loaded using Paging 3 with a custom `MediaStorePagingSource`:

- **Page Size:** 80 images per page
- **Prefetch Distance:** 40 images ahead
- **Placeholders:** Disabled for smooth scrolling
- **Date Grouping:** `insertSeparators` injects date headers between photos from different days
- **API Compatibility:** Uses `Bundle` query args on API 26+, falls back to legacy `LIMIT/OFFSET` sort order

---

## Getting Started

### Prerequisites

- Android Studio Ladybug (2024.2.1) or newer
- JDK 11+
- Android device or emulator running API 24+

### Build & Run

```bash
# Clone the repository
git clone https://github.com/your-username/Padams.git
cd Padams

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug
```

### Face Recognition Setup

1. Download a MobileFaceNet TFLite model (112x112 input, 192-dim output)
2. Place it at `ml/src/main/assets/mobilefacenet.tflite`
3. Rebuild the project

---

## Project Configuration

### Version Catalog

All dependencies are managed centrally in `gradle/libs.versions.toml` — no hardcoded version strings in build files.

### Git Ignore

The `.gitignore` covers all module build directories, IDE files, and sensitive local properties.

---

## License

```
MIT License

Copyright (c) 2024 Padams

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
