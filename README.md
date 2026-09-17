# SpikeStats

An Android companion app for Valorant: look up rank and recent match history by Riot ID,
and browse the full weapon skins catalog. Built with Kotlin + Jetpack Compose.

**Heads up**: rank/match history requires a Riot API key with approved `val/match/v1`
access, which Riot grants on a case-by-case basis (see below) — it's not available on a
plain personal key out of the box. The skins catalog and Riot ID lookup work immediately
with any personal key.

## Important: how "login" actually works here

Riot does not offer public OAuth login ("Sign in with Riot" / RSO) to third-party developers
by default — it's granted only to hand-approved partners through an application process on
the [Riot Developer Portal](https://developer.riotgames.com/). Because of that, this app does
**not** ask for your Riot username or password anywhere. Instead:

- You type your **Riot ID** (`Name#Tag`) and pick your region.
- The app looks up that Riot ID's public match history and rank via Riot's official REST API,
  using a personal developer API key you generate yourself.
- No credentials are collected, stored, or transmitted by this app, ever.

If you get approved for RSO later, `LoginViewModel`/`UserPreferences` are the two places to
swap in a real OAuth flow — the rest of the app (repository, screens, nav) doesn't need to change.

### Match history and rank need an approved key, not just any personal key

Confirmed against Riot's live API: a personal **Development API Key** gets you Riot ID
lookup (`riot/account/v1`) and the content/skins catalog (`val/content/v1`) with no extra
steps — both return normal 200s. `val/match/v1` (matchlists, match detail — everything the
**Profile** tab needs for rank and recent matches) returns a flat `403 Forbidden` on every
region shard for a personal key, with no other detail in the body. Unlike League of Legends,
where match data is open to any dev key, Riot gates Valorant's match/ranked endpoints behind
a **Register Product** application on the [developer portal](https://developer.riotgames.com/)
that Riot reviews and approves case by case. Until that's approved for your key, the Profile
tab will show an explanatory error instead of your stats — this is expected, not a bug in the
app or a problem with your key.

### Why there's no personal shop/store screen

Your personal daily item shop is tied to your real Riot session (an "entitlements token"),
which is only obtainable via an actual Riot client login — not through any sanctioned
third-party API or RSO scope. Apps that show your real store rotation typically do so by
sending your Riot password directly to Riot's undocumented internal auth endpoints, which
is both a Terms of Service gray area and a real account-security risk. This app intentionally
avoids that: the **Skins** tab shows the full public catalog instead of your personal rotation.

## Setup

1. **Get a Riot API key**: sign in at https://developer.riotgames.com/ and copy your
   **Development API Key** (it's free, but expires every 24 hours — regenerate as needed
   while developing).
2. Open `local.properties` and paste in your key (this file is gitignored):
   ```
   RIOT_API_KEY=RGAPI-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
   ```
3. Open the project in **Android Studio** (Koala/2024.1+ recommended) — it'll fix up
   `sdk.dir` for your machine automatically. Run on an emulator or device (minSdk 26 /
   Android 8.0+).

Alternatively, from the command line with a device/emulator connected:
```
./gradlew installDebug
```

### Verified build

This project was built end-to-end (`./gradlew assembleDebug`) during development —
Kotlin/Compose compiles clean, resources merge, and a debug APK packages successfully.
That confirms the code compiles and links; it does **not** confirm on-device UI behavior,
since no emulator was available in that environment. Test the actual screens (search a
real Riot ID, check match history renders, browse skins) before relying on it.

## Architecture

- `data/remote` — Retrofit interfaces + DTOs for Riot's Account-V1 and Valorant
  match/content APIs, with one `X-Riot-Token` interceptor.
- `data/local` — DataStore-backed storage for the saved Riot ID/region/PUUID (no
  credentials).
- `data/repository` — `RiotRepository`, the single point of contact for the network layer.
- `domain` — `ContentLookup` turns raw content data (map/agent/rank-tier ids, skins) into
  display-ready names and a browsable skin list.
- `ui` — Compose screens (`login`, `profile`, `skins`) each with a small `ViewModel`,
  wired together in `ui/navigation/SpikeNavGraph.kt`.

### Known simplifications (MVP scope)

- **DI is manual** (`remember { ... }` in the nav graph) rather than a
  `ViewModelProvider.Factory` — screen state won't survive a configuration change like
  rotation. Fine for trying it out; worth fixing before shipping.
- **Current rank** is inferred from the most recent *competitive* match in your last 10
  games (Riot's public API has no direct "my current rank" endpoint) — if you haven't
  played ranked recently it'll show "No ranked matches yet" even if you have a rank.
- **Region** is asked once at login. Valorant match data is siloed per platform shard
  (na/eu/ap/kr/latam/br); if you queue cross-region this app won't find those matches.

## Data sources (all official, no scraping)

- `riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}` — Riot ID -> PUUID
- `val/match/v1/matchlists/by-puuid/{puuid}` — recent match IDs
- `val/match/v1/matches/{matchId}` — full match detail (map, agent, KDA, rank tier, result)
- `val/content/v1/contents` — maps/agents/rank-tier names, and the skins catalog
