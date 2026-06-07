# Sentinel — Project Architecture

Personal emergency privacy and security platform for Android 11+ (API 30+).
Fully offline. No server dependencies.

## Technology Stack

| Layer | Technology |
|-------|------------|
| UI | Jetpack Compose, Material 3 |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt |
| Database | Room |
| Preferences | DataStore |
| Background | WorkManager, Foreground Services |
| Security | Android Keystore, EncryptedSharedPreferences, BiometricPrompt |
| Async | Kotlin Coroutines + Flow |

## Module Overview

```
Sentinel/
├── app/                          # Application module (single-module MVP, modular packages)
├── gradle/
│   └── libs.versions.toml        # Version catalog
├── build.gradle.kts
├── settings.gradle.kts
└── ARCHITECTURE.md               # This document
```

## Package Structure (com.sentinel)

```
com.sentinel/
├── SentinelApplication.kt        # @HiltAndroidApp entry point
├── MainActivity.kt               # Single-activity Compose host
│
├── di/                           # Hilt dependency injection modules
│   ├── AppModule.kt
│   ├── DatabaseModule.kt
│   ├── RepositoryModule.kt
│   ├── SecurityModule.kt
│   └── EngineModule.kt
│
├── navigation/                   # Compose Navigation graph
│   ├── SentinelRoutes.kt
│   └── SentinelNavHost.kt
│
├── domain/                       # Business logic (framework-agnostic)
│   ├── model/                    # Domain models & enums
│   │   ├── Rule.kt
│   │   ├── Trigger.kt
│   │   ├── Condition.kt
│   │   ├── Action.kt
│   │   ├── Profile.kt
│   │   ├── SecurityEvent.kt
│   │   ├── LockedApp.kt
│   │   ├── DecoyContent.kt
│   │   ├── PasswordConfig.kt
│   │   ├── ButtonTrigger.kt
│   │   └── AppSettings.kt
│   └── usecase/                  # Single-responsibility use cases
│       ├── rule/
│       ├── profile/
│       ├── security/
│       ├── decoy/
│       ├── applocker/
│       └── backup/
│
├── data/                         # Data layer
│   ├── local/
│   │   ├── entity/               # Room @Entity classes
│   │   ├── dao/                  # Room @Dao interfaces
│   │   ├── database/             # SentinelDatabase
│   │   ├── datastore/            # PreferencesDataStore
│   │   └── converter/            # Room TypeConverters
│   ├── mapper/                   # Entity ↔ Domain mappers
│   └── repository/               # Repository implementations
│
├── engine/                       # MODULE 1: Rule Engine
│   ├── RuleEngine.kt             # Orchestrates trigger→condition→action
│   ├── TriggerEvaluator.kt       # Evaluates trigger types
│   ├── ConditionEvaluator.kt     # Evaluates condition logic
│   └── ActionExecutor.kt         # Executes defensive actions
│
├── security/                     # Encryption & authentication
│   ├── CryptoManager.kt          # AES-GCM via Android Keystore
│   ├── SecurePreferences.kt      # EncryptedSharedPreferences wrapper
│   └── BiometricHelper.kt        # BiometricPrompt wrapper
│
├── service/                      # Android system integration
│   ├── accessibility/
│   │   └── SentinelAccessibilityService.kt   # MODULE 10
│   ├── deviceadmin/
│   │   ├── SentinelDeviceAdminReceiver.kt    # MODULE 9
│   │   └── DeviceAdminManager.kt
│   ├── panic/
│   │   ├── PanicTileService.kt               # MODULE 6: Quick Settings
│   │   ├── PanicOverlayService.kt            # Floating panic button
│   │   └── ButtonTriggerService.kt           # MODULE 7: Hardware triggers
│   └── receiver/
│       ├── BootReceiver.kt
│       ├── SimChangeReceiver.kt                # MODULE 9: SIM detection
│       └── ScreenStateReceiver.kt
│
├── worker/                       # WorkManager background tasks
│   ├── SecurityMonitorWorker.kt
│   └── BackupWorker.kt
│
├── ui/                           # Presentation layer (MVVM)
│   ├── theme/                    # Cyber-security Material 3 theme
│   ├── components/               # Reusable Compose components
│   ├── dashboard/                # Main dashboard
│   ├── rules/                    # Rule builder
│   ├── profiles/                 # MODULE 2: Emergency profiles
│   ├── decoy/                    # MODULE 4: Decoy environment
│   ├── applocker/                # MODULE 5: App locker
│   ├── panic/                    # MODULE 6: Panic control center
│   ├── events/                   # MODULE 8: Security event log
│   └── settings/                 # MODULE 11: Settings & backup
│
└── util/
    ├── Constants.kt
    ├── JsonUtil.kt
    └── Extensions.kt
```

## Feature Module Mapping

| Module | Package(s) | Key Components |
|--------|-----------|----------------|
| 1 Rule Engine | `engine/`, `domain/usecase/rule/` | RuleEngine, TriggerEvaluator, ConditionEvaluator, ActionExecutor |
| 2 Emergency Profiles | `domain/model/Profile.kt`, `ui/profiles/` | ProfileEntity, ProfileRepository, ProfileManagerScreen |
| 3 Duress Passwords | `security/`, `domain/model/PasswordConfig.kt` | SecurePreferences, DuressPasswordUseCase |
| 4 Decoy Mode | `ui/decoy/`, `data/local/entity/DecoyDataEntity.kt` | DecoyGalleryScreen, DecoyContactsScreen, DecoyNotesScreen |
| 5 App Locker | `ui/applocker/`, `service/accessibility/` | AppLockerScreen, LockOverlayActivity |
| 6 Panic Button | `service/panic/`, `ui/panic/` | PanicTileService, PanicOverlayService, PanicCenterScreen |
| 7 Button Triggers | `service/panic/ButtonTriggerService.kt` | Hardware key combination detection |
| 8 Event Logger | `ui/events/`, `data/local/entity/LogEntity.kt` | SecurityEventRepository, EventLogScreen |
| 9 Intrusion Detection | `service/receiver/`, `worker/` | SimChangeReceiver, SecurityMonitorWorker |
| 10 Accessibility | `service/accessibility/` | SentinelAccessibilityService |
| 11 Settings | `ui/settings/` | SettingsScreen, BackupUseCase |

## Data Flow

```
UI (Compose) → ViewModel → UseCase → Repository → Room/DataStore/Keystore
                              ↓
                         RuleEngine ← Trigger Events (Accessibility, Receivers, Services)
                              ↓
                         ActionExecutor → Profile Switch / App Lock / Decoy / Log Event
```

## Room Database Schema

```
sentinel.db
├── users              # Local user identity & auth hashes
├── rules              # Automation rules (enabled, priority)
├── triggers           # Rule trigger definitions (JSON payload)
├── conditions         # Rule condition definitions (JSON payload)
├── actions            # Rule action definitions (JSON payload)
├── profiles           # Emergency profile configurations
├── logs               # Security event audit trail
├── locked_apps        # Protected application registry
├── decoy_data         # Fake content for decoy mode
└── settings           # Key-value app settings
```

## Security Architecture

```
┌─────────────────────────────────────────────────┐
│                  Application Layer               │
├─────────────────────────────────────────────────┤
│  BiometricHelper  │  SecurePreferences          │
│  (BiometricPrompt)│  (EncryptedSharedPreferences)│
├─────────────────────────────────────────────────┤
│              CryptoManager (AES-256-GCM)         │
│              Android Keystore backed keys        │
├─────────────────────────────────────────────────┤
│  Room DB (SQLCipher-ready schema) │  DataStore   │
└─────────────────────────────────────────────────┘
```

Sensitive fields (passwords, PINs, decoy content) are encrypted at rest via `CryptoManager` before Room persistence.

## Rule Engine Pipeline

```
Event Source → TriggerEvaluator.match(event)
                    ↓ (matched triggers)
              ConditionEvaluator.evaluate(conditions)
                    ↓ (all conditions met)
              ActionExecutor.execute(actions)
                    ↓
              SecurityEventLogger.log(event)
```

## Navigation Routes

| Route | Screen |
|-------|--------|
| `dashboard` | DashboardScreen |
| `rules` | RuleListScreen |
| `rules/builder/{id?}` | RuleBuilderScreen |
| `profiles` | ProfileManagerScreen |
| `profiles/edit/{id}` | ProfileEditScreen |
| `decoy` | DecoyConfigScreen |
| `decoy/gallery` | DecoyGalleryScreen |
| `decoy/contacts` | DecoyContactsScreen |
| `decoy/notes` | DecoyNotesScreen |
| `applocker` | AppLockerScreen |
| `panic` | PanicCenterScreen |
| `events` | EventLogScreen |
| `settings` | SettingsScreen |
| `settings/duress` | DuressPasswordScreen |
| `settings/backup` | BackupScreen |

## Permissions Required

| Permission | Purpose |
|------------|---------|
| `RECEIVE_BOOT_COMPLETED` | Restore services after reboot |
| `FOREGROUND_SERVICE` | Panic overlay & button trigger monitoring |
| `SYSTEM_ALERT_WINDOW` | Floating panic button |
| `QUERY_ALL_PACKAGES` | App locker app selection |
| `USE_BIOMETRIC` | Biometric app unlock |
| Accessibility Service | App monitoring, screen state, automation |
| Device Admin | Intrusion detection, lock policies |

## Build Requirements

- Android Studio Ladybug or newer
- JDK 17
- Android SDK 35
- Min SDK 30 (Android 11)

```bash
./gradlew assembleDebug
```
