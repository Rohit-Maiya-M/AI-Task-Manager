$ErrorActionPreference = "Stop"

$Root = Split-Path -Parent $MyInvocation.MyCommand.Path
$AiDir = Join-Path $Root "ai_backend"
$BackendDir = Join-Path $Root "aitaskmanager"
$FrontendDir = Join-Path $Root "frontend"

function Test-Command {
    param([string]$Name)
    return [bool](Get-Command $Name -ErrorAction SilentlyContinue)
}

function Start-ServiceJob {
    param(
        [string]$Name,
        [string]$WorkingDirectory,
        [string[]]$Command
    )

    Write-Host "Starting $Name..." -ForegroundColor Cyan
    return Start-Job -Name $Name -ArgumentList $WorkingDirectory, $Command -ScriptBlock {
        param($Dir, $Cmd)
        Set-Location $Dir
        $Args = if ($Cmd.Count -gt 1) { $Cmd[1..($Cmd.Count - 1)] } else { @() }
        & $Cmd[0] @Args
    }
}

if (-not (Test-Command "npm")) {
    throw "npm was not found. Install Node.js before running the frontend."
}

if (-not (Test-Command "java")) {
    throw "java was not found. Install Java 21 before running the Spring Boot backend."
}

$PythonExe = Join-Path $AiDir ".venv\Scripts\python.exe"
if (-not (Test-Path $PythonExe)) {
    $PythonExe = "python"
}

if ($PythonExe -eq "python" -and -not (Test-Command "python")) {
    throw "python was not found. Install Python or create ai_backend\.venv before running the AI service."
}

$jobs = @()

try {
    $jobs += Start-ServiceJob -Name "ai-service" -WorkingDirectory $AiDir -Command @(
        $PythonExe, "-m", "uvicorn", "main:app", "--reload", "--host", "127.0.0.1", "--port", "8000"
    )

    $jobs += Start-ServiceJob -Name "spring-backend" -WorkingDirectory $BackendDir -Command @(
        ".\mvnw.cmd", "spring-boot:run"
    )

    $jobs += Start-ServiceJob -Name "react-frontend" -WorkingDirectory $FrontendDir -Command @(
        "npm", "start"
    )

    Write-Host ""
    Write-Host "All services are starting." -ForegroundColor Green
    Write-Host "AI service:      http://127.0.0.1:8000/docs"
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
