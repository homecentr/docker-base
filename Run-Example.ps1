param (
    [Parameter(Mandatory=$true)][String]$Image,
    [Switch]$AsRoot
)

$ErrorActionPreference = "Stop"

function Docker([Parameter(Mandatory=$true)][String[]]$Args, [Parameter(Mandatory=$true)][String]$WorkDir)
{
  Start-Process -FilePath "docker" -WorkingDirectory "$WorkDir" -ArgumentList $Args -NoNewWindow -Wait
}

Write-Host "Building base image..."
Docker -Args @("build", ".", "-t", "base-$($Image):local") -WorkDir "./$Image"

Write-Host "Building example image..."
Docker -Args @("build", ".", "-t", "example-$($Image):local") -WorkDir "./$Image/example"

If ($AsRoot -eq $true) 
{
  Write-Host "Starting example container (as root)..."
  Docker -Args @("run", "-it", "-e", "PUID=0", "-e", "PGID=0", "example-$($Image):local") -WorkDir "./$Image/example"
}
else 
{
  Write-Host "Starting example container..."
  Docker -Args @("run", "-it", "example-$($Image):local") -WorkDir "./$Image/example"
}