# Chassis Android example

A Jetpack Compose app that shows how to use Chassis UI in an Android project. It is the Android
version of [`chassis-ui/ios-example`](https://github.com/chassis-ui/ios-example), an example to read
and copy from, and it is not published. Its colors, type, spacing, motion, fonts, icons and images all come
from two versioned releases, and one build setting chooses the brand:

- [`chassis-ui/website-tokens`](https://github.com/chassis-ui/website-tokens/releases): the Android
  archive of the `demo` app, with its resources.
- [`chassis-ui/website-assets`](https://github.com/chassis-ui/website-assets/releases): the Android
  archive of the `demo` app, with its fonts, icons, images and app icon.

## Run it

You need JDK 17, the Android SDK and a network connection for the first build. Open the folder in
Android Studio and run it. Or, with `ANDROID_HOME` set to the SDK and an emulator or a device
connected, install it from the command line. If a download reports GitHub's rate limit, set `GH_TOKEN`
to a token for the build to send.

```shell
./gradlew :app:installDebug
```

The first build downloads the tokens and the assets of the brand, and makes `Generated/res` from
them. Neither that folder nor the downloads in `.cache/` are committed. Gradle installs Android 37
into the SDK if it is missing, as Compose compiles against it. The app runs from Android 8.

## Switch the brand

Change one line of [`gradle.properties`](gradle.properties) and build:

```properties
CHASSIS_BRAND=sinefil
```

Or override it for one build, as CI does:

```shell
./gradlew :app:installDebug -PCHASSIS_BRAND=sinefil
```

Each brand has its own application id, so both install side by side. An unknown brand fails the
build before `Generated/` changes, and the error lists the files of the release, which name the
brands that exist.

## Adopt a new version

Change the version in `gradle.properties` and build. That line is the whole change:

```properties
CHASSIS_TOKENS_VERSION=0.2.0
CHASSIS_ASSETS_VERSION=0.4.0
```

A token, an icon or an image that was renamed or removed then fails to compile, and shows you what
to update. A font role that no longer resolves stops the app at launch. The `Update versions`
workflow checks for new releases each week and opens a pull request. The repository has to allow
Actions to create pull requests, and CI starts on such a pull request when someone closes and
reopens it.

## Use your own tokens and assets

Where the tokens and the assets come from is set in `gradle.properties` too, and a change to any of
these settings replaces `Generated/` on the next build:

| Setting                     | What it names                                                     |
| --------------------------- | ----------------------------------------------------------------- |
| `CHASSIS_APP`               | The app in the tokens and the assets, `demo` here                 |
| `CHASSIS_TOKENS_REPOSITORY` | The GitHub `team/repo` of the tokens, with a release `v<version>` |
| `CHASSIS_TOKENS_ARCHIVE`    | The name of the tokens archive of a brand on that release         |
| `CHASSIS_ASSETS_REPOSITORY` | The GitHub `team/repo` of the assets, with a release `v<version>` |
| `CHASSIS_ASSETS_ARCHIVE`    | The name of the assets archive of a brand on that release         |

The archive names are templates of `{app}`, `{brand}` and `{version}`. The resources have to keep
the names this app reads, such as `color_context_default_fg_main`, and the fonts, the icons, the
images and the app icon have to keep their names too. Each archive holds them in a `res/` folder.

## How it fits together

```
gradle.properties             The brand, the versions and where they come from
buildSrc/.../SyncBrand.kt     Downloads, checks and unpacks a release. Runs before a build
scripts/lint-tokens.sh        Fails on a value that is no token
app/src/main/kotlin/.../
  chassis/                    The layer between the tokens and the screens
  components/                 Controls and views made of tokens
  screens/                    The mobile patterns
app/src/main/res/             The theme of the window, made of tokens
Generated/res/                What the sync writes, not committed
```

`SyncBrand` checks each download against the digest that GitHub records for it. Gradle leaves
`Generated/` alone when no setting changed, and a change replaces the whole folder.

### Tokens

The resources are used as they are, so that a token in a screen can be searched for in the tokens
and in Figma:

```kotlin
Text(
    "Saved",
    style = ChassisType.labelMedium.style,
    color = colorResource(R.color.color_context_default_fg_main),
    modifier = Modifier.padding(dimensionResource(R.dimen.space_context_medium)),
)
```

The colors follow `values-night` and the sizes `values-sw600dp` and `values-sw840dp`, with no code.
The layer in `chassis/` adds only what a resource cannot do by itself:

| File            | What it adds                                                                      |
| --------------- | --------------------------------------------------------------------------------- |
| `ChassisFonts`  | Resolves a style of the tokens to its font file by role, and checks all at launch |
| `ChassisType`   | A whole text style with its line height, in `sp`                                  |
| `ChassisStyle`  | The Material theme, the choice of appearance, two-layer shadows, motion curves    |
| `ChassisAssets` | The icons and images this app uses, and an icon that scales with the font size    |

### Fonts by role

The font files are named by role and weight, `display_mass.ttf`, and the brand decides which family
a role holds. The tokens give a style its family and weight, so `ChassisFonts` looks for the role
with that family and weight and takes its file. No family name is in the source. Every style is
resolved at launch, so a style without a file stops the app at once.

### Assets

The fonts, the icons, the images and the app icon are in the `res/` folder of the archive, and
`SyncBrand` copies it over the one of the tokens, so each is a resource under its name. A file that
both hold comes from the assets. Two files of one name and different extensions fail the build:

| In `Generated/res`                                   | What it is                        |
| ---------------------------------------------------- | --------------------------------- |
| `font/ui_normal.ttf`                                 | A font, by role and weight        |
| `drawable/ic_home_solid.xml`                         | An icon, as a vector drawable     |
| `drawable-nodpi/logo.png`, `drawable-night-nodpi/logo.png` | An image of one size, in both modes |
| `drawable-xhdpi/platforms.png`                       | An image at one of its densities  |
| `mipmap-anydpi-v26/app_icon.xml`                     | The adaptive icon of the brand    |

`painterResource(R.drawable.logo)` follows light and dark, as the archive joins a pair of `_light`
and `_dark` files into one name. An icon paints in black, and takes the color of its tint. The
manifest names `@mipmap/app_icon`, which each brand holds.

### Native and custom

The navigation bar, the top bars, sheets, dialogs, swipe actions, pull to refresh and switches are
those of Material 3, styled by tokens through `ChassisTheme` and their `colors`. The segment bar,
the search field, buttons and text fields are custom, as Material gives its own no way to take these
tokens. They follow the mobile components of the `cx.components.UI` Figma library.

The tab screens but Search have large titles, and a pushed screen an inline one. Each tab keeps its
own stack, as a `NavHost` under a saved state.

The choice of appearance changes the configuration that the resources are read in, under
`ChassisTheme`. So the app needs no second set of names and no restart of the activity.

### Where the tokens have no group

The tokens have no group for a tab bar, a toolbar, an avatar or a skeleton, so these take the
closest context or unit tokens, as the bars of the Figma library do. Some values have no token:

| Value                         | What this app does                                                      |
| ----------------------------- | ----------------------------------------------------------------------- |
| The `round-44` radii of Figma | A `CircleShape`, which is what they draw, as the tokens do not export them |
| Nav bar and tab bar height    | The height Material gives them                                          |

Unlike iOS, Compose has a shadow spread, so all the values of a shadow token apply.

## The mobile patterns

| Tab          | What it shows                                                                        |
| ------------ | ------------------------------------------------------------------------------------ |
| First launch | Paged onboarding, then a form with validation                                        |
| Home         | Segment filter, skeleton loading, pull to refresh, cards, push to a detail, a toast  |
| Search       | Search field, filter chips, results, an empty state                                  |
| Inbox        | List with swipe actions, badges, a tab badge, a conversation with its message field  |
| Account      | Settings list, appearance choice, switch, a pricing sheet, a confirmation            |
| Foundations  | A sample of the colors, spacing, radii, shadows and motion, every text style and icon this app uses, and the states of the components |

Each one follows dark mode and the font size of the system, and has TalkBack labels. A segment label
shrinks to fit its part, and the initials of an avatar stay at their size. TalkBack has not been run
through these screens.

## Check it

```shell
scripts/lint-tokens.sh
./gradlew :app:assembleDebug
```

CI runs the lint and builds every brand, on the workflows of `.github/`. It does not open the app,
so after a change to a screen, run each brand on an emulator, in light and in dark.

## License

[MIT](LICENSE)
