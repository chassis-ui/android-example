# AGENTS.md

Instructions for AI coding agents working in this repository.

## Project

The Chassis Android example: one Jetpack Compose app whose tokens and assets come from versioned
releases, and whose brand is one build setting. It is an example, not a package: nothing is
published, and there are no changesets or releases. `README.md` explains how it works.

Related repositories (separate git projects, under `chassis-ui` on GitHub): `website-tokens`,
`website-assets`, `assets`, `tokens`. `ios-example` is the same app on the other platform.

## Commands

- Sync: `./gradlew syncBrand [-PCHASSIS_BRAND=<brand>]` fills `Generated/res`. A build does it first.
- Lint: `scripts/lint-tokens.sh`.
- Build: `./gradlew :app:assembleDebug -PCHASSIS_BRAND=<brand>`
- Run: `./gradlew :app:installDebug -PCHASSIS_BRAND=<brand>`

Before a task is done, run the lint and build every brand of the build job in
`.github/workflows/ci.yml`. After a change to a screen, run each brand on an emulator, in light and
in dark, and look at it.

## Rules

- **No value in a view that is no token.** No color, font size, spacing, radius, duration or opacity
  as a literal. Take the closest context or unit token where a component has no group of its own.
  Do not get past the lint with a token of another kind.
- **Every asset comes from the release.** No Material icon, no image or font added to this
  repository. An icon or an image is a case of `ChassisAssets.kt`.
- **The source is the same for every brand.** Nothing in `app/` names a brand or a font family, or
  branches on a brand, and there are no product flavors. What differs comes from the tokens and the
  assets, under the same names.
- **Fonts are named by role.** A style reaches its file through `ChassisFonts`. A new text style is
  a token group added to the companion of `ChassisType`.
- **The brand, the versions and their sources are in `gradle.properties`.** No repository or archive
  name in a task or a workflow: they read the setting. Do not add a list of brands to the source.
- **Keep the checks of `SyncBrand`.** A download is checked against the digest of its release, and
  an unknown brand fails before `Generated/` changes.
- **Do not move a version** unless the task asks for it.
- **Use the native control where it can take the tokens.** A custom control is for what Material
  gives no way to style, and follows the Figma library `cx.components.UI`.
- **Changes to the tokens or the assets belong in their repositories**, and reach this one with a
  release. Report a missing token or asset there, and do not work around it here.
- **Keep the example small.** Compose, the activity and navigation are the dependencies. A reader
  should be able to follow a screen from top to bottom. Keep `README.md` true.
- **Name a sibling by its GitHub repository**, never by a local folder or a relative path.

## Cautions

- Never commit or push without being asked.
- Do not edit generated or fetched files: `Generated/`, `.cache/`.
