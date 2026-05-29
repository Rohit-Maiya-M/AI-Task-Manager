$ErrorActionPreference = "Stop"

$Root = Split-Path -Parent $MyInvocation.MyCommand.Path
$BackendDir = Join-Path $Root "aitaskmanager"
$FrontendDir = Join-Path $Root "frontend"

function Test-Command {
    param([string]$Name)
    return [bool](Get-Command $Name -ErrorAction SilentlyContinue)
}

if (-not (Test-Command "npm")) {
    throw "npm was not found. Install Node.js before running the frontend."
}

if (-not (Test-Command "java")) {
    throw "java was not found. Install Java 21 before running the Spring Boot backend."
}

$jobs = @()

try {
    Write-Host "Starting spring-backend..." -ForegroundColor Cyan
    $jobs += Start-Job -Name "spring-backend" -ArgumentList $BackendDir -ScriptBlock {
        param($Dir)
        Set-Location $Dir
        .\mvnw.cmd spring-boot:run
    }

    Write-Host "Starting react-frontend..." -ForegroundColor Cyan
    $jobs += Start-Job -Name "react-frontend" -ArgumentList $FrontendDir -ScriptBlock {
        param($Dir)
        Set-Location $Dir
        npm start
    }

    Write-Host ""
    Write-Host "All services are starting." -ForegroundColor Green
    Write-Host "Spring backend:  http://localhost:8080"
    Write-Host "React frontend:  http://localhost:3000"
    Write-Host ""
    Write-Host "Keep this window open. Press Ctrl+C to stop all services." -ForegroundColor Yellow
    Write-Host ""

    while ($true) {
        foreach ($job in $jobs) {
            Receive-Job -Job $job -ErrorAction Continue
        }

        $failed = $jobs | Where-Object { $_.State -in @("Failed", "Stopped", "Completed") }
        if ($failed) {
            Write-Host ""
            Write-Host "One or more services stopped:" -ForegroundColor Red
            $failed | ForEach-Object { Write-Host "- $($_.Name): $($_.State)" }
            break
        }

        Start-Sleep -Seconds 2
    }
}
finally {
    Write-Host ""
    Write-Host "Stopping services..." -ForegroundColor Yellow
    $jobs | Stop-Job -ErrorAction SilentlyContinue
    $jobs | Remove-Job -Force -ErrorAction SilentlyContinue
}
