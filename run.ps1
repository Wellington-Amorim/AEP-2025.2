<#
.SYNOPSIS
  Run script for Windows PowerShell: build (if needed) and run shaded JAR.
.PARAMETER DbUrl
  JDBC URL to the database.
.PARAMETER DbUser
  Database username.
.PARAMETER DbPassword
  Database password.
#>
param(
  [string]$DbUrl = "jdbc:mysql://localhost:3306/doacoes?createDatabaseIfNotExist=true&serverTimezone=UTC",
  [string]$DbUser = "root",
  [string]$DbPassword = ""
)

$jar = Join-Path -Path $PSScriptRoot -ChildPath "target\AEP-2025.2-1.0-SNAPSHOT-shaded.jar"
if (-not (Test-Path $jar)) {
    Write-Host "JAR not found: $jar`nBuilding project (this may take a while)..."
    mvn -q -DskipTests package
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Maven build failed. Aborting."
        exit 1
    }
}

Write-Host "Starting application..."

# Monta argumentos de forma robusta (evita problemas de aspas)
$javaArgs = @(
  "-Dfile.encoding=UTF-8",
  "-Ddb.url=$DbUrl",
  "-Ddb.username=$DbUser",
  "-Ddb.password=$DbPassword",
  "-jar",
  $jar
)
Write-Host ("java " + ($javaArgs -join ' '))
& java @javaArgs
if ($LASTEXITCODE -ne 0) {
    Write-Error "Application exited with code $LASTEXITCODE"
}
