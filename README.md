# Sentinel

[فارسی](README.fa.md)

**Sentinel** is a production-grade Android personal emergency privacy and security platform. It runs fully offline as a local security automation engine — no server required.

## Features

| Module | Capability |
|--------|------------|
| Rule Engine | Trigger → Condition → Action automation pipeline |
| Emergency Profiles | Normal, Travel, Border Crossing, Emergency, Custom |
| Duress Passwords | Multiple passwords with distinct defensive actions |
| Decoy Mode | Fake gallery, contacts, notes, files, recent activity |
| App Locker | PIN, password, biometric protection with timeout policies |
| Panic Button | Quick Settings tile, floating button, hardware combos |
| Button Triggers | Power ×5, volume combos, long-press patterns |
| Event Logger | Searchable encrypted audit trail |
| Intrusion Detection | SIM change, unlock failures, admin/accessibility tampering |
| Accessibility | App monitoring, screen state, rule execution |
| Settings | Backup/export, encryption, themes, profile management |

## Tech Stack

- **Min SDK 30** (Android 11+)
- Kotlin · Jetpack Compose · Material 3
- MVVM + Clean Architecture
- Room · DataStore · WorkManager · Hilt · Coroutines

## Project Structure

See [ARCHITECTURE.md](ARCHITECTURE.md) for the complete folder tree, data flow diagrams, and module mapping.

```
BlackShield/
├── app/                    # Application module
├── gradle/
├── ARCHITECTURE.md         # Full architecture documentation
├── README.md
└── README.fa.md
```

## Getting Started

### Prerequisites

- Android Studio Ladybug (2024.2+) or newer
- JDK 17
- Android SDK 35

### Build

Open the project in Android Studio and sync Gradle, or from the command line:

```bash
./gradlew assembleDebug
```

Install on a device or emulator (API 30+):

```bash
./gradlew installDebug
```

## Language

Go to **Settings → Language** to switch between **English** and **فارسی (Persian)**. The app restarts automatically after changing language.

## First Launch Setup

1. **Set passwords** — Settings → Duress Passwords
2. **Enable Accessibility** — Required for app monitoring and locking
3. **Enable Device Admin** — Required for intrusion detection
4. **Create rules** — Dashboard → Rules → Add rule
5. **Configure profiles** — Dashboard → Profiles
6. **Add panic tile** — Quick Settings → Edit → Sentinel Panic

## Security

- AES-256-GCM encryption via Android Keystore
- EncryptedSharedPreferences for credentials
- BiometricPrompt for app unlock
- Sensitive Room fields encrypted at rest
- Backup payloads encrypted before export

## License

Private / All rights reserved.
