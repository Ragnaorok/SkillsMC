$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest
Add-Type -AssemblyName System.IO.Compression.FileSystem
$root = Join-Path ([System.IO.Path]::GetTempPath()) ('skillsmc-stage-test-' + [Guid]::NewGuid().ToString('N'))
$server = Join-Path $root 'server with spaces'
$delivery = Join-Path $root 'delivery'
$plugins = Join-Path $server 'plugins'
$jarName = 'SkillsMC-1.21.11.jar'
$commit = '1234567890123456789012345678901234567890'
$script = Join-Path $PSScriptRoot 'Stage-SkillsMC.ps1'
function Assert-True($Condition, [string]$Message) { if (-not $Condition) { throw $Message } }
function Assert-Rejected([scriptblock]$Action, [string]$Message) {
    $rejected = $false
    try { & $Action | Out-Null } catch { $rejected = $true }
    Assert-True $rejected $Message
}
function Update-Checksum {
    ((Get-FileHash -LiteralPath (Join-Path $delivery $jarName) -Algorithm SHA256).Hash.ToLowerInvariant() + "  $jarName") |
        Set-Content -LiteralPath (Join-Path $delivery "$jarName.sha256") -Encoding ASCII
}
try {
    New-Item -ItemType Directory -Path $plugins, $delivery -Force | Out-Null
    Set-Content -LiteralPath (Join-Path $server 'server.properties') -Value 'server-port=25565'
    Set-Content -LiteralPath (Join-Path $server 'bukkit.yml') -Value "settings:`n  update-folder: update"
    Set-Content -LiteralPath (Join-Path $plugins $jarName) -Value 'original installed JAR'
    $originalHash = (Get-FileHash -LiteralPath (Join-Path $plugins $jarName)).Hash
    $jarPath = Join-Path $delivery $jarName
    $zip = [System.IO.Compression.ZipFile]::Open($jarPath, [System.IO.Compression.ZipArchiveMode]::Create)
    try {
        $writer = New-Object System.IO.StreamWriter($zip.CreateEntry('plugin.yml').Open())
        try { $writer.Write("name: SkillsMC`napi-version: '1.21.11'`n") } finally { $writer.Dispose() }
    } finally { $zip.Dispose() }
    Update-Checksum
    & $script -ServerDirectory $server -DeliveryDirectory $delivery -BuildCommit $commit
    $staged = Join-Path (Join-Path $plugins 'update') $jarName
    $stagedHash = (Get-FileHash -LiteralPath $staged).Hash
    Assert-True ($stagedHash -eq (Get-FileHash -LiteralPath $jarPath).Hash) 'New build was not staged correctly.'
    Assert-True ((Get-FileHash -LiteralPath (Join-Path $plugins $jarName)).Hash -eq $originalHash) 'Running plugin JAR was modified.'
    $backups = @(Get-ChildItem -LiteralPath (Join-Path $server 'skillsmc-backups') -Directory)
    Assert-True ($backups.Count -eq 1) 'Expected an installed-plugin backup.'
    Assert-True ((Get-FileHash -LiteralPath (Join-Path $backups[0].FullName $jarName)).Hash -eq $originalHash) 'Backup bytes do not match original.'
    # A bad delivery must leave the working and pending JARs untouched.
    Set-Content -LiteralPath (Join-Path $delivery "$jarName.sha256") -Value ('0' * 64)
    Assert-Rejected { & $script -ServerDirectory $server -DeliveryDirectory $delivery -BuildCommit $commit } 'Bad checksum accepted.'
    Assert-True ((Get-FileHash -LiteralPath $staged).Hash -eq $stagedHash) 'Bad delivery changed the staged JAR.'
    Update-Checksum
    Set-Content -LiteralPath (Join-Path $plugins 'SkillsMC-old.jar') -Value 'duplicate'
    Assert-Rejected { & $script -ServerDirectory $server -DeliveryDirectory $delivery -BuildCommit $commit } 'Duplicate installed plugin accepted.'
    Remove-Item -LiteralPath (Join-Path $plugins 'SkillsMC-old.jar')
    Set-Content -LiteralPath (Join-Path $server 'bukkit.yml') -Value "settings:`n  update-folder: custom"
    Assert-Rejected { & $script -ServerDirectory $server -DeliveryDirectory $delivery -BuildCommit $commit } 'Custom update directory silently accepted.'
    Set-Content -LiteralPath (Join-Path $server 'bukkit.yml') -Value "settings:`n  update-folder: update"
    # Even a checksummed artifact must identify itself as the expected plugin/API.
    Remove-Item -LiteralPath $jarPath
    $zip = [System.IO.Compression.ZipFile]::Open($jarPath, [System.IO.Compression.ZipArchiveMode]::Create)
    try {
        $writer = New-Object System.IO.StreamWriter($zip.CreateEntry('plugin.yml').Open())
        try { $writer.Write("name: OtherPlugin`napi-version: '1.21.11'`n") } finally { $writer.Dispose() }
    } finally { $zip.Dispose() }
    Update-Checksum
    Assert-Rejected { & $script -ServerDirectory $server -DeliveryDirectory $delivery -BuildCommit $commit } 'Wrong plugin accepted.'
    Assert-True ((Get-FileHash -LiteralPath $staged).Hash -eq $stagedHash) 'Rejected artifact changed pending plugin.'
    Write-Output 'PASS: paths with spaces, checksum and metadata validation, duplicate/custom-directory rejection, installed JAR preservation, and backup integrity.'
} finally {
    if (Test-Path -LiteralPath $root) { Remove-Item -LiteralPath $root -Recurse -Force }
}
