# Get current version from gradle.properties
$version = (Get-Content "gradle.properties" | Select-String "VERSION_NAME").ToString().Split("=")[1].Trim()
$tag = "v$version"

# Create and push tag
git tag $tag
git push origin $tag

Write-Host "Created and pushed tag: $tag" 