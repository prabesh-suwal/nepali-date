# Publishing Guide for nepali-date to GitHub Packages

This guide walks you through publishing your Java library to GitHub Packages so others can use it as a dependency.

## Prerequisites ✅

- [x] Code pushed to GitHub repository: `prabesh-suwal/nepali-date`
- [x] `pom.xml` configured with `<distributionManagement>` (already done)
- [x] GitHub Actions workflow created at `.github/workflows/publish.yml`

## Step 1: Verify Your Configuration

The following files have been prepared:

1. **`.github/workflows/publish.yml`** — GitHub Actions workflow for CI/CD
2. **`pom.xml`** — Contains distribution management for GitHub Packages

## Step 2: Push the Workflow to GitHub

Run these commands to commit and push the new workflow:

```bash
cd /home/prabesh/1work/projects/nepali-date

# Add the new workflow file
git add .github/workflows/publish.yml

# Commit the changes
git commit -m "Add GitHub Actions workflow for publishing to GitHub Packages"

# Push to GitHub
git push origin main
```

## Step 3: Create and Push a Release Tag

To trigger the publish workflow, create a Git tag and push it:

```bash
# Create a tag for version 1.0.0
git tag v1.0.0

# Push the tag to GitHub
git push origin v1.0.0
```

## Step 4: Create a GitHub Release

1. Go to your repository: https://github.com/prabesh-suwal/nepali-date
2. Click on **"Releases"** in the right sidebar
3. Click **"Create a new release"**
4. Select the tag `v1.0.0` you just created
5. Title: `v1.0.0 - Initial Release`
6. Description: Add release notes (features, changes, etc.)
7. Click **"Publish release"**

This will trigger the GitHub Actions workflow to:
- Build the project
- Run tests
- Generate sources JAR
- Generate Javadoc JAR
- Publish all artifacts to GitHub Packages

## Step 5: Verify Publication

After the workflow completes:

1. Go to your repository on GitHub
2. Click on the **"Packages"** section (right sidebar)
3. You should see `nepali-date` package published
4. Click on it to see the published versions

## Step 6: Using the Published Package

### For Maven Users

Users need to add this to their `pom.xml`:

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/prabesh-suwal/nepali-date</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.prabesh.growphasetech</groupId>
        <artifactId>nepali-date</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

**Authentication Required:** Users must add GitHub credentials to `~/.m2/settings.xml`:

```xml
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>THEIR_GITHUB_USERNAME</username>
      <password>THEIR_GITHUB_TOKEN</password>
    </server>
  </servers>
</settings>
```

**GitHub Token Requirements:**
- Go to GitHub Settings → Developer settings → Personal access tokens → Tokens (classic)
- Generate token with `read:packages` scope
- Use this token as the password

### For Gradle Users

Add to `build.gradle.kts`:

```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/prabesh-suwal/nepali-date")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation("com.prabesh.growphasetech:nepali-date:1.0.0")
}
```

## Troubleshooting

### Issue: Workflow doesn't run after creating release
**Solution:** Make sure you pushed the workflow file to the `main` branch BEFORE creating the release.

### Issue: "401 Unauthorized" when publishing
**Solution:** 
- Check that the workflow has `packages: write` permission (already configured)
- Verify the repository settings allow GitHub Actions to write packages

### Issue: Users can't download the package
**Solution:** 
- They need a GitHub Personal Access Token with `read:packages` scope
- Token must be configured in their Maven `settings.xml` or Gradle properties

## Alternative: Maven Central (Future)

If you want to publish to Maven Central instead of (or in addition to) GitHub Packages:

1. Create an account at https://central.sonatype.com/
2. Verify domain ownership or GitHub repository
3. Enable GPG signing in `pom.xml` (currently commented out)
4. Add OSSRH repository to `<distributionManagement>`
5. Configure Maven Central credentials

**Benefit:** No authentication required for users to download your package.

## Next Release

To publish a new version (e.g., 1.0.1):

1. Update version in `pom.xml`: `<version>1.0.1</version>`
2. Commit and push changes
3. Create and push new tag: `git tag v1.0.1 && git push origin v1.0.1`
4. Create GitHub Release for `v1.0.1`

---

## Summary

✅ **Workflow created** at `.github/workflows/publish.yml`  
✅ **POM configured** with GitHub Packages distribution  
⏳ **Next step:** Push workflow to GitHub and create a release tag

Once you create a release, the package will be automatically published and available at:
`https://github.com/prabesh-suwal/nepali-date/packages`

