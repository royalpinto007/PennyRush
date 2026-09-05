# Contributing To Pennyrush

Thanks for helping make Pennyrush better. This project is privacy-first personal finance software, so contributions should preserve trust before chasing convenience.

## Repository

Primary repository: https://github.com/royalpinto007/PennyRush

## Development Setup

```bash
npm install
npm run web:dev
```

Useful checks:

```bash
npm run web:typecheck
npm run web:lint
npm run web:build
```

Android currently uses the scaffold in `android/`. Run Android checks from Android Studio or a local Gradle install once the Gradle wrapper is added.

### Android tests

Place Android unit tests in the module that owns the code being tested. For
example, tests for `:core:common` should live under that module's `src/test`
directory rather than in `:app` or another feature module.

Run all Android unit tests from the `android/` directory with:

```bash
./gradlew testDebugUnitTest
```

CI runs unit tests across all applicable Android modules and assembles the debug
variants so that every module is compiled.

## Privacy Rules

- Do not persist uploaded bank statements, receipt images, PDFs, or CSV files.
- Do not add Supabase Storage usage for imported files.
- Do not put AI provider keys in Android or web client code.
- Do not log raw financial files, account numbers, emails, auth tokens, or complete transaction exports.
- Keep AI payloads minimal and routed through server-side functions.
- Keep RLS policies on every user-owned table.

## Branches And Commits

Use small branches with clear names:

- `feature/import-preview`
- `fix/rls-category-ownership`
- `docs/privacy-policy`

Write commits in plain language and keep unrelated changes separate.

## Pull Requests

Before opening a PR:

1. Run the relevant checks.
2. Update docs for behavior or privacy changes.
3. Add screenshots for UI work.
4. Explain any schema or auth changes.
5. Call out any security or migration risk.

## Design Contributions

Pennyrush should feel clean, minimal, and premium. Prefer whitespace, clear typography, one primary action per screen, accessible contrast, and predictable workflows.

## Dependency Policy

The v1 stack should stay free to run at small scale. Avoid paid SDKs, ad SDKs, tracking SDKs, and dependencies that require hosted paid services for core functionality.

CI blocks pull requests on high or critical vulnerabilities in production dependencies; the full dependency audit runs as a non-blocking informational check.

## Claiming an issue

Want to pick something up? Just comment on the issue saying you'd like to work on
it. A workflow adds the `claimed` label so nobody else duplicates your effort.

Two small rules keep things fair:

- **Two open claims per person.** If you already hold two claimed issues, we'll ask
  you to finish one first so other people get a turn. Comment again once one lands
  and the next is yours.
- **Claims go stale after 14 days.** If a claimed issue sees no activity for two
  weeks, the label is removed and it goes back in the pool. No hard feelings, and
  you can always claim it again.

Link your pull request to the issue in the PR description (for example
`Closes #12`). When that PR is merged the issue closes itself, and if the PR is
closed without being merged the claim is released so someone else can pick it up.

No pressure on timelines otherwise. Ask questions in the issue thread any time.

