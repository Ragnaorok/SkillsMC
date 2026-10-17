# Build and delivery

The workflow in `.github/workflows/build.yml` runs Maven `clean verify` on Java 17 for pushes to main, pull requests targeting main, version tags (`v*`), and manual runs. Maven runs any tests present; this repository currently has no automated tests. A successful build verifies that the packaged JAR contains plugin.yml and saves SkillsMC.jar and a SHA-256 checksum as an Actions artifact for 14 days. It does not test gameplay or start Paper.

## Download a development build

Open the repository's Actions tab, select a successful Build and release run, and download its SkillsMC artifact. Extract the ZIP to obtain the JAR and checksum.

## Publish a release

After merging the workflow, tag a reviewed commit from main and push the tag:

```sh
git switch main
git pull --ff-only
git tag v1.0.0
git push origin v1.0.0
```

Use an unused version tag each time. After the build succeeds, the workflow creates a GitHub release with the JAR, checksum, and generated release notes. Tag creation does not change the Maven/plugin version: pom.xml still defines 1.0-SNAPSHOT. Update that version before tagging when a matching in-plugin version is needed. Release creation fails for an existing release; remove an incomplete release before retrying.

## Build compatibility

The existing pom.xml targets Paper API 1.19.2 and Java 17, while plugin.yml declares API 1.21. This pipeline preserves those settings; successful compilation does not establish Paper 1.21 runtime compatibility. Resolve and test that mismatch before using the plugin on a live server.

The build uses remote snapshot and third-party dependencies. Repository outages or removed versions can cause dependency resolution failures. The Paper repository URL is updated and EffectLib uses HTTPS so modern Maven does not reject the HTTP repository.

## Server deployment

GitHub release publication is configured. Automatic installation and server restarts are not configured yet. These require the hosting method (for example, a hosting panel, Docker, or a systemd service), transfer/access method, plugins directory, and restart procedure. Store credentials in GitHub Actions secrets, never in source files or chat.

For manual installation, stop the server cleanly, back up the existing SkillsMC JAR and plugin data, replace only the SkillsMC plugin JAR, and start the server. Check the startup logs and gameplay on a test server before production. Keep the prior JAR and data backup for rollback.
