#Requires -Version 6.0

param(
   [Parameter()]
    [string]
    $TeamCityToken = ""
)

$ErrorActionPreference = "Stop"
Get-GitHubPullRequest -Owner WSStudios -RepositoryName tdp1 -Number 5736 -Token $(ConvertTo-SecureString -AsPlainText -Force -String "$TeamCityToken")
