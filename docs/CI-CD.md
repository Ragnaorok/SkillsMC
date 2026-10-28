# CI and Windows test delivery

The workflow builds Paper 1.21.11 with Java 21 on GitHub-hosted Ubuntu runners for pushes and pull requests targeting `main` or `upgrade/1.21.11`. The repository has no automated Java tests; Maven verifies compilation and packaging. A separate GitHub-hosted Windows job tests staging behavior (backup integrity, preservation of the installed JAR, and rejection of invalid checksums, plugin metadata, duplicate JARs, or custom update directories). Both jobs must pass before staging. Successful builds save the plugin, checksum, and staging script as a downloadable artifact for 14 days.

## Current validation

The migration built successfully with Java 21. On 2026-10-20 the owner reported successful startup on Paper 1.21.11 with EffectLib 10.12, castability of implemented skills for all four classes, and player-data persistence checks passing. These are manual checks, not comprehensive automated gameplay tests.

## Register the Windows runner

1. Open https://github.com/Ragnaorok/SkillsMC/settings/actions/runners/new and choose Windows / x64.
2. Open PowerShell as your normal `revup` account. Use a dedicated folder such as `C:\actions-runner-skillsmc`, outside the server folder and repository. Execute the Download commands shown by GitHub.
3. Execute GitHub's Configure command locally. The registration token is temporary: do not commit it or paste it into chat. Choose runner name `skillsmc-test-pc`, add custom label `skillsmc-test`, and accept the default work folder. For initial setup, decline running as a service.
4. Run `.\run.cmd` and keep the window open. Confirm the runner shows Idle in GitHub. It needs no inbound connection or router port forwarding.

SkillsMC is a public repository. This workflow only uses the PC runner for explicitly enabled pushes to the two named branches or manually requested deliveries; pull requests always build on GitHub-hosted runners. Do not add self-hosted runner labels to PR jobs. Anyone able to change trusted workflow code can execute commands as the runner account. Keep workflow write access limited to people you trust with that account.

## Enable delivery

The configured server folder is:

```text
C:\Users\revup\OneDrive\Desktop\SkillsMC Test environment
```

It must contain `server.properties`, `bukkit.yml` with `settings.update-folder: update`, and `plugins\SkillsMC-1.21.11.jar`. EffectLib 10.12 must already be installed separately.

After the runner is Idle, open Settings > Secrets and variables > Actions > Variables and add repository variable `ENABLE_TEST_DEPLOYMENT` with value `true`. Successful future pushes to `upgrade/1.21.11` or `main` then queue staging on your PC. With the variable absent or false, only the build runs. This initial commit builds without needing a registered runner.

To trigger another build on the upgrade branch after setup:

```sh
git switch upgrade/1.21.11
git pull --ff-only
git commit --allow-empty -m "Trigger Windows test delivery"
git push origin upgrade/1.21.11
```

Once the workflow exists on the default branch, Actions > SkillsMC CI and test delivery > Run workflow also supports selecting a branch and checking `deploy_test`. GitHub does not expose manual dispatch for a workflow that only exists on a non-default branch.

## What delivery does

The Windows runner downloads only the artifact from the successful build in the same workflow run. It does not check out the repository, build Java, restart the server, or open network ports. Deliveries are serialized and never cancel an in-progress copy.

The PowerShell script validates the plugin metadata and SHA-256, checks for duplicate SkillsMC JARs, backs up the currently installed JAR under `skillsmc-backups`, and stages the new JAR at `plugins\update\SkillsMC-1.21.11.jar`. Previously staged JARs are also backed up before replacement. Paper applies the staged JAR on the next startup. Type `stop` in the server console and then run `start.bat`; avoid `/reload`.

A backup here contains the plugin JAR only, not worlds, configurations, or player data. Back up those separately before changes that affect data formats. Existing SkillsMC player files are UUID-named `.dat` files in the server root, not under `plugins`.

## Rollback

Stop the server, remove any pending `plugins\update\SkillsMC-1.21.11.jar`, copy the desired saved JAR from `skillsmc-backups` over `plugins\SkillsMC-1.21.11.jar`, and run `start.bat`. Restore separate data backups too if a future change migrates data.

## Troubleshooting

- Queued staging: the matching Windows x64 runner with label `skillsmc-test` must be online; keep `.\run.cmd` open.
- No staging job: check `ENABLE_TEST_DEPLOYMENT=true`, or use a manual dispatch with `deploy_test` after the workflow reaches the default branch.
- Path/access errors: the runner must run as a Windows account with access to the configured folder. Do not use a service account without configuring access to the user profile/OneDrive path.
- Plugin unchanged: a green staging job only prepares the next startup. Stop and restart, then check the startup log. `1.0-SNAPSHOT` is still the plugin's own version.
- Moving the server folder: update `SERVER_DIR` in the workflow before the next delivery.

No automatic server restart or GitHub Release publishing is configured in this workflow. Those can be added separately after runner delivery is verified. The earlier Java 17 pipeline draft is superseded by this Java 21 configuration and should not be merged as-is.
