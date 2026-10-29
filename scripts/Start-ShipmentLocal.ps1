<#
.SYNOPSIS
Inicia shipment con credenciales de proceso y una clave pública externa.
.DESCRIPTION
No modifica MySQL ni habilita baseline. Para la base heredada ya migrada,
indicar -Database logitrack; una instalación aislada usa logitrack_shipment.
.PARAMETER Database
Nombre de la base existente y autorizada para shipment.
.PARAMETER DatabaseUser
Cuenta MySQL de la aplicación, nunca la cuenta root.
.PARAMETER PublicKey
Archivo PEM público del mismo par utilizado por security.
#>
[CmdletBinding()]
param(
    [ValidatePattern('^[A-Za-z0-9_]+$')]
    [string]$Database = 'logitrack_shipment',
    [Parameter(Mandatory = $true)]
    [string]$DatabaseUser,
    [string]$PublicKey = '',
    [string]$ConfigServerUrl = 'http://localhost:8888',
    [string]$CourierUrl = 'http://localhost:8082'
)
$ErrorActionPreference = 'Stop'
$repo = Split-Path $PSScriptRoot -Parent
if (!$PublicKey) { $PublicKey = Join-Path $repo 'secrets\jwt-public.pem' }
$key = (Resolve-Path -LiteralPath $PublicKey).Path
$env:JWT_PUBLIC_KEY_LOCATION = ([System.Uri]::new($key)).AbsoluteUri
$env:JWT_ISSUER = 'logitrack-security'
$env:DB_URL = "jdbc:mysql://localhost:3306/$Database"
$env:DB_USERNAME = $DatabaseUser
$secret = Read-Host "Contraseña MySQL de $DatabaseUser" -AsSecureString
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new('', $secret).Password
$env:CONFIG_SERVER_URL = $ConfigServerUrl
$env:COURIER_SERVICE_URL = $CourierUrl
# Evita heredar la opción utilizada una sola vez durante la migración local.
$env:SPRING_FLYWAY_BASELINE_ON_MIGRATE = 'false'
$env:SPRING_JPA_HIBERNATE_DDL_AUTO = 'validate'
Push-Location $repo
try {
    & .\mvnw.cmd spring-boot:run
    if ($LASTEXITCODE -ne 0) { throw "Shipment terminó con código $LASTEXITCODE" }
} finally {
    Remove-Item Env:DB_PASSWORD -ErrorAction SilentlyContinue
    $secret.Dispose()
    Pop-Location
}
