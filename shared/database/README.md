
### Migration DB Manual
1. Update version in `build.gradle.kts`.

```
sqldelight {
    databases {
        create("AniflowDatabase") {
            version = 2 // Update this version
        }
    }
}
```

2. Create a new migration file in `src/commonMain/sqldelight/migrations/` named <version to upgrade from>.sqm

3. Confirm new db file generated in `src/commonMain/sqldelight/databases` after successful build.

4. run `./gradlew verifySqlDelightMigration` to verify the migration of SQLDelight databases.