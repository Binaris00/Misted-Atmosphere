# Misted Atmosphere

A Minecraft **1.20.1 Forge** mod that turns the night sky into a slow, 29 day lunar calendar and wraps the world in a permanent, moody atmosphere: overcast days, dense mist over water, a teal color grade inside the fog, and monsters that no longer burn in daylight.

Built for Forge `47.x` (`47.4.23` in development), Java 17, using Mixin.

## Features

### 1. 29 day moon cycle
Vanilla Minecraft loops through 8 moon phases every 8 days. This mod replaces that with a **29 day cycle** that matches a real lunar month:

| Day | Phase | Day | Phase |
|-----|-------|-----|-------|
| 1 | New Moon | 15-20 | Waning Gibbous |
| 2-6 | Waxing Crescent | 21 | Last Quarter |
| 7 | First Quarter | 22-28 | Waning Crescent |
| 8-13 | Waxing Gibbous | 29 | New Moon |
| 14 | Full Moon | | |

The cycle is driven by a single Mixin on `DimensionType#moonPhase(long)`, so the sky renderer, the moon brightness and every other vanilla system that reads the phase stay consistent on both client and server. Vanilla only ships 8 moon images, so the 8 textures are reused across the longer cycle.

### 2. Blood moon and blue moon
On the **full moon night** (day 14 of a cycle) special moons can appear:

- **Blood moon** — every 3rd cycle, rendered with `blood_moon_phases.png`
- **Blue moon** — every 5th cycle, rendered with `blue_moon_phases.png`
- When both land on the same night (every 15th cycle), **the blue moon wins**

Rendering works by redirecting the second `RenderSystem.setShaderTexture` call inside `LevelRenderer#renderSky` (the moon bind) to the mod texture when a special night is active. The moon texture sheet keeps the vanilla `4x2` / 8 phase layout, so all of vanilla's UV slicing keeps working untouched.

### 3. Overcast daytime
Daylight is desaturated and dimmed toward grey so the sky never looks fully bright. Purely visual: block light, crop growth and mob spawning are unaffected. Strength is configurable.

### 4. Permanent mist and teal color grading
- During the **day**, dense fog sits over oceans, rivers and beaches.
- During the **night**, the mist extends everywhere.
- While inside the mist, fog and sky are blended toward a **blue teal** color and the fog distance is shortened (default 192 → 80 blocks), which reads as a color grade without requiring shaders. The fog is transparent enough to still make out vague silhouettes; raise `mist.fogEnd` for clearer views, lower it for a denser wall of mist.
- Entering and leaving the mist (and the overcast look turning on/off) is **faded smoothly over `transitions.fadeSeconds`** instead of snapping, so walking across a coastline or shoreline does not pop.

Fog color is transformed by redirecting the single `RenderSystem.setShaderFogColor` call inside `FogRenderer#levelFogColor`, and the density is applied at the tail of `FogRenderer#setupFog`.

### 5. Monsters do not burn in the sun
`Mob#isSunBurnTick` is disabled, so zombies, skeletons and friends keep spawning only at night but never catch fire during the day.

## Project structure

```
MistedAtmosphere/
├── build.gradle                     ForgeGradle build script (Forge 47.4.23, Mixin AP, JUnit 5)
├── gradle.properties                Versions, mod id/name/description
├── settings.gradle
└── src/
    ├── main/
    │   ├── java/org/binaris/misted_atmosphere/
    │   │   ├── MistedAtmosphere.java        Mod entry point, config registration
    │   │   ├── Config.java                  ForgeConfigSpec (COMMON + CLIENT), color parsing
    │   │   ├── moon/
    │   │   │   ├── MoonCalendar.java        Pure calendar math: day/cycle/phase/special moon
    │   │   │   └── SpecialMoon.java         NONE / BLOOD / BLUE
    │   │   ├── client/
    │   │   │   ├── ClientMoonTextures.java  Picks the moon sheet for the current night
    │   │   │   ├── MistClient.java          Mist zone detection + fog color/density application
    │   │   │   └── MistColors.java          Pure color math: overcast + teal blending
    │   │   └── mixin/
    │   │       ├── DimensionTypeMixin.java  getMoonPhase / getMoonBrightness source
    │   │       ├── LevelRendererMixin.java  Swaps the moon texture on special nights
    │   │       ├── FogRendererMixin.java    Overcast, mist color and fog density
    │   │       └── MobMixin.java            Disables sun burning
    │   └── resources/
    │       ├── META-INF/mods.toml
    │       ├── misted_atmosphere.mixins.json
    │       └── assets/misted_atmosphere/textures/environment/
    │           ├── blood_moon_phases.png    (placeholder, replace with final art)
    │           └── blue_moon_phases.png     (placeholder, replace with final art)
    └── test/java/org/binaris/misted_atmosphere/
        ├── moon/MoonCalendarTest.java       29 day mapping, special moon schedule, priority
        └── client/MistColorsTest.java       Overcast and teal blend math
```

## How it works

| Feature | Hook | Notes |
|---------|------|-------|
| 29 day cycle | `@Inject` HEAD of `DimensionType#moonPhase(long)` | Feeds both `getMoonPhase()` and `getMoonBrightness()`, so the renderer and gameplay agree |
| Blood / blue moon | `@Redirect` on the moon `setShaderTexture` in `LevelRenderer#renderSky` | Runtime check makes sure only the moon bind (not the sun) is replaced |
| Overcast | `@Redirect` on `setShaderFogColor` in `FogRenderer#levelFogColor` | The sky dome is fogged, so changing the fog color also changes the sky |
| Mist density | `@Inject` TAIL of `FogRenderer#setupFog` | Only ever shortens the fog distance, never lengthens it |
| No sun burn | `@Inject` HEAD of `Mob#isSunBurnTick`, cancellable | Returns `false` when enabled |

All gameplay values are read from the cached config fields in `Config`.

> Note: `Level#isDay()` is intentionally **not** used on the client. Its backing `skyDarken` field is only refreshed in the `ClientLevel` constructor in 1.20.1, so the client computes night from `dayTime` directly (`13000..23000` ticks).

## Configuration

Two config files are generated under `config/`:

`misted_atmosphere-common.toml`

| Key | Default | Description |
|-----|---------|-------------|
| `moon.cycleEnabled` | `true` | Enable the 29 day cycle |
| `moon.bloodMoonEnabled` | `true` | Enable blood moons |
| `moon.bloodMoonCycleInterval` | `3` | Blood moon every Nth cycle |
| `moon.blueMoonEnabled` | `true` | Enable blue moons |
| `moon.blueMoonCycleInterval` | `5` | Blue moon every Nth cycle |
| `gameplay.noSunBurn` | `true` | Disable mob sun burning |

`misted_atmosphere-client.toml`

| Key | Default | Description |
|-----|---------|-------------|
| `overcast.enabled` | `true` | Overcast daylight |
| `overcast.strength` | `0.6` | `0` = vanilla, `1` = heavily overcast |
| `mist.enabled` | `true` | Mist over water |
| `mist.nightEnabled` | `true` | Extend mist everywhere at night |
| `mist.fogStart` | `8.0` | Fog start distance inside the mist (blocks) |
| `mist.fogEnd` | `80.0` | Fog end distance inside the mist (blocks); higher = more transparent |
| `mist.tealTintEnabled` | `true` | Teal grading inside the mist |
| `mist.tealStrength` | `0.55` | `0` = none, `1` = full teal |
| `mist.tealColor` | `#12545F` | Teal color, `#RRGGBB` |
| `transitions.fadeSeconds` | `1.5` | Fade time when entering/leaving the mist or overcast; `0` disables the fade |

## Moon assets

The mod expects the same `4 columns x 2 rows` sheet layout as vanilla `moon_phases.png` (8 phases, full moon in the top-left cell). **Any image size works** because the UVs are normalized, but `128x64` (32x32 cells, vanilla size) is recommended.

Place the final artwork at:

```
src/main/resources/assets/misted_atmosphere/textures/environment/blood_moon_phases.png
src/main/resources/assets/misted_atmosphere/textures/environment/blue_moon_phases.png
```

The two files currently in the repository are **recolored placeholders generated from the vanilla texture** so the feature is testable out of the box. Replace them with the final assets whenever they are ready.

## Building and testing

```bash
./gradlew build        # compiles, runs the unit tests and builds the jar
./gradlew test         # unit tests only (16 tests)
./gradlew runClient    # launches a dev client
./gradlew runServer    # launches a dev server
```

The built mod jar is written to `build/libs/misted_atmosphere-<version>.jar`.

Unit tests cover the pure logic (`MoonCalendar`, `MistColors`). The Mixins were additionally verified in a dev client and dedicated server: mixin targets resolve in the generated refmap, a blood moon night swaps to the correct texture, day/night fog states and densities apply, and a zombie summoned at noon survives.

## In-game QA checklist

Use `/time set <ticks>` to jump around the calendar:

| Scenario | Command (dayTime) |
|----------|-------------------|
| Full moon (normal, midnight of day 14) | `/time set 330000` |
| Blood moon (cycle 3, midnight) | `/time set 1722000` |
| Blue moon (cycle 5, midnight) | `/time set 3114000` |
| Overlap (cycle 15, blue wins, midnight) | `/time set 10074000` |
| Overcast day | `/time set 6000` |
| Night mist everywhere | `/time set 18000` |
| Water mist | stand in an ocean/river/beach biome during the day |
| No sun burn | `/summon zombie` at noon and watch it |

## Compatibility

- Minecraft **1.20.1**, Forge **47.x**, Java **17**
- Client and server both need the mod (moon phases affect gameplay brightness/spawning)
- No external dependencies; works with shader mods since it only touches vanilla fog/sky colors and the moon texture
