# Silent-Overrider

Android application that allows authorized SMS commands to force a loud alert on a target device, even when silent mode is enabled.

## Key features

- SMS trigger detection via `SMS_RECEIVED` broadcast receiver
- Whitelist authorization persisted in `SharedPreferences`
- Phone normalization before sender comparison
- Foreground ring service that:
  - switches ringer mode to normal
  - sets ring/alarm volume to max
  - plays looping alarm sound
  - auto-stops after configurable timeout
- Minimal local whitelist management UI (add/list/remove)

## Configuration

- Trigger keyword is controlled by Gradle property `triggerKeyword` in `gradle.properties`.
- Default auto-stop is 30 seconds (`BuildConfig.ALERT_DURATION_SECONDS`).

## Build

```bash
./gradlew :app:assembleDebug
```
