[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)][string]$ServerDirectory,
    [Parameter(Mandatory = $true)][string]$DeliveryDirectory,
    [Parameter(Mandatory = $true)][ValidatePattern('^[a-f0-9]{40}$')][string]$BuildCommit
)
$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest

$jarName = 'SkillsMC-1.21.11.jar'
$source = Join-Path $DeliveryDirectory $jarName
$checksum = Join-Path $DeliveryDirectory "$jarName.sha256"
$plugins = Join-Path $ServerDirectory 'plugins'
$installed = Join-Path $plugins $jarName
$update = Join-Path $plugins 'update'
$destination = Join-Path $update $jarName

if (-not (Test-Path -LiteralPath (Join-Path $ServerDirectory 'server.properties'))) {
    throw "Server folder not found or not initialized: $ServerDirectory"
}
if (-not (Test-Path -LiteralPath $installed)) {
    throw "Expected installed plugin at $installed. Install the first build manually or check the filename."
}
$duplicates = @(Get-ChildItem -LiteralPath $plugins -Filter 'SkillsMC*.jar' -File | Where-Object { $_.Name -ne $jarName })
if ($duplicates.Count -gt 0) { throw 'Multiple or differently named SkillsMC JARs found; resolve them before deploying.' }

# Paper must use the default update folder for this staging operation.
$bukkitConfig = Join-Path $ServerDirectory 'bukkit.yml'
if (-not (Test-Path -LiteralPath $bukkitConfig)) { throw 'Missing bukkit.yml; verify the server directory.' }
$config = Get-Content -LiteralPath $bukkitConfig -Raw
if ($config -notmatch '(?m)^\s*update-folder:\s*(?:update|''update''|"update")\s*(?:#.*)?$') {
    throw 'Expected settings.update-folder: update in bukkit.yml. No files have been changed.'
}

$expected = ((Get-Content -LiteralPath $checksum -Raw).Trim() -split '\s+')[0]
if ($expected -notmatch '^[a-fA-F0-9]{64}$') { throw 'Invalid SHA-256 checksum.' }
if ((Get-FileHash -LiteralPath $source -Algorithm SHA256).Hash -ne $expected) { throw 'Build checksum mismatch.' }

Add-Type -AssemblyName System.IO.Compression
Add-Type -AssemblyName System.IO.Compression.FileSystem
$zip = [System.IO.Compression.ZipFile]::OpenRead($source)
try {
    $entry = $zip.GetEntry('plugin.yml')
    if ($null -eq $entry) { throw 'Artifact is missing plugin.yml.' }
    $reader = New-Object System.IO.StreamReader($entry.Open())
    try { $metadata = $reader.ReadToEnd() } finally { $reader.Dispose() }
    if ($metadata -notmatch '(?m)^name:\s*SkillsMC\s*$' -or
        $metadata -notmatch '(?m)^api-version:\s*[''"]?1\.21\.11[''"]?\s*$') {
        throw 'Artifact is not the expected SkillsMC Paper 1.21.11 build.'
    }
} finally { $zip.Dispose() }

$backupDirectory = Join-Path (Join-Path $ServerDirectory 'skillsmc-backups') ((Get-Date -Format 'yyyyMMdd-HHmmss-fff') + '-' + $BuildCommit.Substring(0, 12))
New-Item -ItemType Directory -Path $backupDirectory -Force | Out-Null
Copy-Item -LiteralPath $installed -Destination (Join-Path $backupDirectory $jarName)
if (Test-Path -LiteralPath $destination) {
    Copy-Item -LiteralPath $destination -Destination (Join-Path $backupDirectory 'previously-staged.jar')
}
New-Item -ItemType Directory -Path $update -Force | Out-Null
$tempJar = Join-Path $update ('.skillsmc-' + [Guid]::NewGuid().ToString('N') + '.tmp')
try {
    Copy-Item -LiteralPath $source -Destination $tempJar
    if ((Get-FileHash -LiteralPath $tempJar -Algorithm SHA256).Hash -ne $expected) { throw 'Staged copy checksum mismatch.' }
    Move-Item -LiteralPath $tempJar -Destination $destination -Force
} finally {
    if (Test-Path -LiteralPath $tempJar) { Remove-Item -LiteralPath $tempJar }
}
@{ commit = $BuildCommit; sha256 = $expected; staged_at = (Get-Date).ToString('o'); destination = $destination } |
    ConvertTo-Json | Set-Content -LiteralPath (Join-Path $backupDirectory 'delivery.json') -Encoding UTF8
Write-Output "Staged $BuildCommit at $destination"
Write-Output "Previous plugin backed up at $backupDirectory"
Write-Output 'Apply it by typing stop in the server console, then run start.bat. The running server has not been restarted.'
