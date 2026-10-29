<#
.SYNOPSIS
Clona los repositorios faltantes como carpetas hermanas de shipment.
.DESCRIPTION
Conserva el nombre actual de shipment (por ejemplo Spring Web). No actualiza
repositorios existentes salvo que se indique UpdateExisting. No descarta cambios.
.PARAMETER Root
Carpeta que contendrá todos los repositorios.
.PARAMETER UpdateExisting
Actualiza solo árboles limpios, sin commits divergentes, mediante fast-forward.
#>
[CmdletBinding()]
param(
    [string]$Root = (Split-Path (Split-Path $PSScriptRoot -Parent) -Parent),
    [switch]$UpdateExisting
)
$ErrorActionPreference = 'Stop'
$shipment = Split-Path $PSScriptRoot -Parent
$projects = @(
    @{ Name = 'shipment-styx'; Branch = 'develop'; Path = $shipment },
    @{ Name = 'security-styx'; Branch = 'develop'; Path = (Join-Path $Root 'security-styx') },
    @{ Name = 'courier-service'; Branch = 'develop'; Path = (Join-Path $Root 'courier-service') },
    @{ Name = 'config-server'; Branch = 'develop'; Path = (Join-Path $Root 'config-server') },
    @{ Name = 'styx-config'; Branch = 'main'; Path = (Join-Path $Root 'styx-config') }
)
foreach ($project in $projects) {
    # Shipment es el repositorio desde el cual se ejecuta este script. Su remoto
    # puede haber sido renombrado o ser un fork; se conserva su rama de trabajo.
    if ($project.Path -eq $shipment) {
        # --show-prefix devuelve vacio en la raiz. No interpretar rutas de
        # stdout nativo: Windows PowerShell 5.1 puede decodificar mal la UTF-8.
        $prefix = & git -C $shipment rev-parse --show-prefix
        if ($LASTEXITCODE -ne 0 -or ![string]::IsNullOrEmpty([string]$prefix)) {
            throw "El script debe estar dentro de la raíz del repositorio shipment"
        }
        Write-Host "Shipment actual: $shipment (se conserva su rama y remoto)"
        & git -C $shipment log -1 --format='%h %s'
        continue
    }
    if (!(Test-Path -LiteralPath $project.Path)) {
        & git clone --branch $project.Branch "https://github.com/logitrack-styx/$($project.Name).git" $project.Path
        if ($LASTEXITCODE -ne 0) { throw "No se pudo clonar $($project.Name)" }
    } else {
        $origin = & git -C $project.Path remote get-url origin
        if ($LASTEXITCODE -ne 0 -or $origin -notmatch "[:/]logitrack-styx/$($project.Name)(\.git)?$") {
            throw "La carpeta $($project.Path) no corresponde al repositorio esperado"
        }
        if ($UpdateExisting) {
            $changes = & git -C $project.Path status --porcelain
            if ($LASTEXITCODE -ne 0 -or $changes) { throw "Hay cambios locales en $($project.Path); conservalos antes de actualizar" }
            & git -C $project.Path fetch origin
            if ($LASTEXITCODE -ne 0) { throw "Falló fetch de $($project.Name)" }
            & git -C $project.Path switch $project.Branch
            if ($LASTEXITCODE -ne 0) { throw "No se pudo seleccionar $($project.Branch)" }
            & git -C $project.Path merge --ff-only "origin/$($project.Branch)"
            if ($LASTEXITCODE -ne 0) { throw "La rama diverge; revisar sin forzar cambios" }
        }
    }
    Write-Host "$($project.Name): $($project.Path)"
    & git -C $project.Path log -1 --format='%h %s'
}
