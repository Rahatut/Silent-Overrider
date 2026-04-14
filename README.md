# Silent-Overrider

Production-grade Kotlin implementation of an SMS-triggered silent override ring system.

## Implemented Components

- **SMS layer**
  - `SmsMessage`
  - `PhoneNumberNormalizer`
  - `SmsTriggerValidator`
  - `SmsTriggerProcessor`
- **Storage layer**
  - `WhitelistStore`
  - `SharedPreferencesWhitelistStore` (via `KeyValueStore` abstraction)
- **Ring execution layer**
  - `RingService`
  - `AudioController` abstraction
  - `ForegroundExecutionController` abstraction
  - `TaskScheduler` abstraction for auto-stop timeout
- **Whitelist management layer**
  - `WhitelistManager`

## Coverage

Unit tests cover:

- phone number normalization
- sender whitelist behavior
- trigger keyword validation
- ring auto-stop scheduling and execution

## Build and test

```bash
./gradlew test
```
