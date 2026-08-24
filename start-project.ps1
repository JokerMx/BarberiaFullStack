[CmdletBinding()]
param()

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectRoot

function Invoke-RequiredCommand {
    param(
        [string]$Command,
        [string[]]$Arguments
    )

    & $Command @Arguments
    if ($LASTEXITCODE -ne 0) {
        throw "El comando fallo con codigo ${LASTEXITCODE}: $Command $($Arguments -join ' ')"
    }
}

$composeCommand = $null
$composeArguments = @()
if (Get-Command docker -ErrorAction SilentlyContinue) {
    try {
        docker compose version *> $null
        if ($LASTEXITCODE -eq 0) {
            $composeCommand = 'docker'
            $composeArguments = @('compose')
        }
    } catch {
    }
}
if (-not $composeCommand -and (Get-Command docker-compose -ErrorAction SilentlyContinue)) {
    $composeCommand = 'docker-compose'
}
if (-not $composeCommand) {
    throw 'No se encontro Docker Compose. Instala Docker Desktop o Docker Compose.'
}

$mavenCommand = $null
$mavenArguments = @()
if (Get-Command mvn -ErrorAction SilentlyContinue) {
    $mavenCommand = 'mvn'
} elseif ($env:OS -eq 'Windows_NT' -and (Test-Path (Join-Path $projectRoot 'mvnw.cmd'))) {
    $mavenCommand = (Join-Path $projectRoot 'mvnw.cmd')
    $mavenArguments = @('-f', 'backend/pom.xml')
} elseif (Test-Path (Join-Path $projectRoot 'mvnw')) {
    $mavenCommand = (Join-Path $projectRoot 'mvnw')
    $mavenArguments = @('-f', 'backend/pom.xml')
} else {
    throw 'No se encontro Maven ni el wrapper mvnw en la raiz del proyecto.'
}

Write-Host 'Deteniendo contenedores existentes...'
Invoke-RequiredCommand $composeCommand ($composeArguments + @('down'))

Write-Host 'Compilando backend...'
Set-Location (Join-Path $projectRoot 'backend')
Invoke-RequiredCommand $mavenCommand ($mavenArguments + @('clean', 'compile', 'package', '-DskipTests'))
Set-Location $projectRoot

Write-Host 'Construyendo e iniciando la aplicacion...'
Invoke-RequiredCommand $composeCommand ($composeArguments + @('up', '-d', '--build'))

Write-Host 'Aplicacion iniciada correctamente.'
Write-Host 'Frontend: http://localhost:5173'
Write-Host 'Backend:  http://localhost:8080'
