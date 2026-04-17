# Run in PowerShell as Administrator (right-click -> Run as administrator).
# Quits WSL (stops Docker's Linux VM), compacts Docker Desktop VHDXs, then you can start Docker again.

$ErrorActionPreference = "Stop"
Write-Host "Shutting down WSL..."
wsl --shutdown
Start-Sleep -Seconds 5

$vhds = @(
    "$env:LOCALAPPDATA\Docker\wsl\disk\docker_data.vhdx",
    "$env:LOCALAPPDATA\Docker\wsl\main\ext4.vhdx"
)

foreach ($v in $vhds) {
    if (-not (Test-Path -LiteralPath $v)) {
        Write-Host "Skip (not found): $v"
        continue
    }
    $before = (Get-Item -LiteralPath $v).Length
    Write-Host "Compacting ($([math]::Round($before/1GB,2)) GB): $v"
    $script = @"
select vdisk file="$v"
attach vdisk readonly
compact vdisk
detach vdisk
"@
    $script | diskpart
    $after = (Get-Item -LiteralPath $v).Length
    Write-Host "  -> $([math]::Round($after/1GB,2)) GB"
}

Write-Host "Done. Start Docker Desktop again when ready."
