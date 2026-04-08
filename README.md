# StatTier

A Fabric client-side mod that displays PvP tier rankings directly in player nametags.

**Note:** This mod requires [UkuLib](https://modrinth.com/mod/ukulib) to function.

## What it does

StatTier pulls tier data from a custom leaderboard API and shows each player's ranking next to their name — both above their head and in the tab player list. It supports multiple game modes including Sword, Crystal, Nethpot, Diapot, SMP, Mace, Axe, and UHC.

If a player isn't ranked in the currently selected game mode, StatTier can optionally display their highest tier across all modes instead.

### Points and Titles

Every tier carries a point value. Points are summed across all game modes to determine a player's title:

| Tier | Points |
|------|--------|
| High Tier 1 | 60 |
| Low Tier 1 | 45 |
| High Tier 2 | 30 |
| Low Tier 2 | 20 |
| High Tier 3 | 10 |
| Low Tier 3 | 6 |
| High Tier 4 | 4 |
| Low Tier 4 | 3 |
| High Tier 5 | 2 |
| Low Tier 5 | 1 |

| Title | Points Required |
|-------|----------------|
| Rookie | < 10 |
| Combat Novice | 10+ |
| Combat Cadet | 20+ |
| Combat Specialist | 50+ |
| Combat Ace | 100+ |
| Combat Master | 250+ |
| Combat Grandmaster | 400+ |

### Commands

- `/stattier <player>` — View a player's full tier breakdown, title, and leaderboard rank.

### Config

All settings are accessible through the UkuLib config screen:

- Toggle mod on/off
- Select which game mode to display
- Show highest tier when unranked in current mode (never / when not found / always)
- Toggle gamemode icons
- Toggle player list tiers
- Customize tier colors
- Search any player in the leaderboard

A keybind is also available to cycle through game modes in-game.

## Supported Versions

Minecraft 1.21 and 1.21.1 on Fabric.

## Building from source

```
git clone https://github.com/HackerADF/StatTierTagger.git
cd StatTierTagger
./gradlew build
```

The built jar will be in `build/libs/`.

## License

This project is licensed under MPL-2.0.
