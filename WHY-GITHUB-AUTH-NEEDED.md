# Why GitHub Packages Requires Authentication (Even for Public Packages)

## The Short Answer

**GitHub Packages is a registry service, not a public download site.** Even though your package is publicly visible, GitHub treats package downloads as API requests that need authentication for rate limiting and tracking purposes.

---

## The Detailed Explanation

### 1. **Rate Limiting & Abuse Prevention**

GitHub uses authentication tokens to track and limit downloads per user/account:

- **Unauthenticated requests:** 60 requests/hour (very low)
- **Authenticated requests:** 5,000 requests/hour (much higher)

Without authentication, users would quickly hit rate limits when downloading dependencies.

```
❌ Without auth: You download 10 packages → immediately hit 60/hour limit
✅ With auth: You download 100 packages → still have plenty of requests left
```

### 2. **Request Attribution & Tracking**

GitHub needs to know WHO is downloading packages to:
- Track usage statistics
- Detect malicious activity
- Enforce API quotas per user
- Prevent DDoS attacks

Even public packages are tracked through authenticated requests.

### 3. **How Maven Resolves Dependencies**

When Maven builds your project, it makes HTTP requests to download JARs:

```
1. User runs: mvn install
2. Maven reads pom.xml dependencies
3. Maven looks up repositories (including GitHub Packages)
4. Maven makes HTTP GET request to: 
   https://maven.pkg.github.com/prabesh-suwal/nepali-date/...
5. GitHub API requires Authorization header
6. Without auth token → 401 Unauthorized
```

### 4. **Comparison with Other Package Registries**

| Registry | Public Packages | Auth Required? | Why? |
|----------|-----------------|----------------|------|
| **Maven Central** | ✅ Yes | ❌ No | Handles millions of requests, designed for public access |
| **GitHub Packages** | ✅ Yes | ✅ Yes | Private registry tied to GitHub accounts |
| **npm (public)** | ✅ Yes | ❌ No | Designed for public consumption |
| **PyPI (public)** | ✅ Yes | ❌ No | Open public registry |

---

## The Technical Flow

### Without Authentication (What Happens):

```
User's Maven                GitHub Packages
    │                            │
    ├─ GET /nepali-date.jar ────>│
    │                            │
    │<─── 401 Unauthorized ──────┤
    │     (no auth header)        │
    │                            │
    └─ Build FAILS ❌
```

### With Authentication (What Happens):

```
User's Maven                GitHub Packages
    │                            │
    ├─ GET /nepali-date.jar ────>│
    │ Authorization: token XYZ  │
    │                            │
    │<─── 200 OK + JAR file ────┤
    │                            │
    └─ Build SUCCEEDS ✅
```

---

## Why Not Just Use Maven Central?

Good question! If you want to avoid authentication requirements, you can publish to **Maven Central** instead, which is completely public and requires no authentication.

### Comparison:

| Aspect | GitHub Packages | Maven Central |
|--------|-----------------|---------------|
| **Public Access** | ✅ Visible, but auth required | ✅ Visible, no auth needed |
| **Setup Time** | ⚡ Fast (10 min) | 🐢 Slow (1-2 days) |
| **Setup Complexity** | Simple | Complex (JIRA ticket, domain verification, GPG signing) |
| **Discoverability** | Limited to GitHub | Widely indexed, searchable |
| **Authentication** | ✅ Required for downloads | ❌ Not required |

**Your current choice:** GitHub Packages (fast & easy)  
**Best choice for public adoption:** Maven Central (no auth, more discoverable)

---

## GitHub's Official Explanation

From GitHub Docs:

> "GitHub Packages is a software package hosting service that allows you to host your software packages privately or publicly. GitHub Packages requires you to authenticate to GitHub when you install or publish packages."

The authentication requirement is by design to:
1. Maintain API stability through rate limiting
2. Track package usage
3. Prevent abuse
4. Maintain security

---

## For Your Users

When users want to use your nepali-date library from GitHub Packages, they MUST:

1. **Create a GitHub Personal Access Token**
   - Go to: https://github.com/settings/tokens
   - Scope needed: `read:packages`
   - Copy the token

2. **Add to their `~/.m2/settings.xml`**
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

3. **Add repository to their `pom.xml`**
   ```xml
   <repositories>
     <repository>
       <id>github</id>
       <url>https://maven.pkg.github.com/prabesh-suwal/nepali-date</url>
     </repository>
   </repositories>
   ```

---

## Alternative Approach: Switch to Maven Central

If you want your library to be easily accessible without authentication, you should publish to **Maven Central** instead. This requires:

1. Creating account at https://central.sonatype.com/
2. Verifying domain ownership or GitHub repository
3. Enabling GPG signing
4. Updating `pom.xml` with OSSRH repository
5. First release takes 1-2 days for verification

**Pros:**
- ✅ No authentication needed
- ✅ Widely discoverable
- ✅ Industry standard

**Cons:**
- 🐢 Slower initial setup
- 🔐 Requires GPG key setup
- 📋 More validation rules

---

## Summary

| Question | Answer |
|----------|--------|
| **Why auth for public package?** | Rate limiting, abuse prevention, request tracking |
| **Is it GitHub's fault?** | No, it's intentional design for API stability |
| **Can I avoid this?** | Yes, publish to Maven Central instead |
| **Is my package secure?** | Yes, authentication protects your infrastructure |
| **Do I tell users?** | Yes, provide setup instructions (already in your README!) |

---

## What to Tell Your Users

Add this to your README or CONTRIBUTING.md:

```markdown
## Using nepali-date from GitHub Packages

Since this library is published to GitHub Packages (not Maven Central), 
you need to authenticate with GitHub to download it.

### Setup Instructions

1. **Create a GitHub Personal Access Token**
   - Visit: https://github.com/settings/tokens
   - Select scopes: `read:packages`
   - Generate and copy the token

2. **Configure Maven**
   Add to `~/.m2/settings.xml`:
   ```xml
   <settings>
     <servers>
       <server>
         <id>github</id>
         <username>YOUR_GITHUB_USERNAME</username>
         <password>YOUR_PERSONAL_ACCESS_TOKEN</password>
       </server>
     </servers>
   </settings>
   ```

3. **Add Repository**
   Add to your `pom.xml`:
   ```xml
   <repositories>
     <repository>
       <id>github</id>
       <url>https://maven.pkg.github.com/prabesh-suwal/nepali-date</url>
     </repository>
   </repositories>
   ```

### Why Authentication?

GitHub Packages requires authentication for rate limiting and abuse prevention.
If you prefer a package registry without authentication, consider Maven Central 
(though setup is more complex).
```

---

## Bottom Line

**GitHub Packages being public ≠ GitHub Packages being openly accessible**

It's public in the sense that:
- ✅ Anyone can see it exists
- ✅ Anyone can find the package
- ✅ The code is viewable

But it's not open in the sense that:
- ❌ Anyone cannot download it without authentication
- ❌ Download requires proving you have a GitHub account
- ❌ This is intentional for stability & security

This is the same model as many other private/enterprise registries (Artifactory, Nexus, etc.).

