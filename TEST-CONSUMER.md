# Test Consumer Project for nepali-date

This is a minimal Maven project to test consuming your published library.

## How to Use This

After publishing your library, create a test project with this structure:

### 1. Create test project directory
```bash
mkdir ~/test-nepali-date
cd ~/test-nepali-date
```

### 2. Create `pom.xml`
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.test</groupId>
    <artifactId>test-nepali-date</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

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
</project>
```

### 3. Create test Java file
```bash
mkdir -p src/main/java/com/test
```

Create `src/main/java/com/test/TestNepaliDate.java`:
```java
package com.test;

import com.prabesh.growphasetech.NepaliDate;
import java.time.LocalDate;

public class TestNepaliDate {
    public static void main(String[] args) {
        // Test basic functionality
        NepaliDate today = NepaliDate.today();
        System.out.println("Today in BS: " + today);

        // Test conversion
        NepaliDate bsDate = NepaliDate.fromAD(LocalDate.of(2025, 7, 17));
        System.out.println("2025-07-17 in BS: " + bsDate);

        LocalDate adDate = bsDate.toAD();
        System.out.println("Back to AD: " + adDate);

        System.out.println("✅ Library works correctly!");
    }
}
```

### 4. Configure Authentication

Create/edit `~/.m2/settings.xml`:
```xml
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>YOUR_GITHUB_TOKEN</password>
    </server>
  </servers>
</settings>
```

**Generate token at:** https://github.com/settings/tokens
- Scope needed: `read:packages`

### 5. Test the Library

```bash
# Build and run
mvn clean compile
mvn exec:java -Dexec.mainClass="com.test.TestNepaliDate"
```

Expected output:
```
Today in BS: 2082/11/19
2025-07-17 in BS: 2082/04/01
Back to AD: 2025-07-17
✅ Library works correctly!
```

---

## Troubleshooting

### "Could not find artifact"
- Check that the package is published at: https://github.com/prabesh-suwal/nepali-date/packages
- Verify your GitHub credentials in `~/.m2/settings.xml`
- Ensure your token has `read:packages` scope

### "401 Unauthorized"
- Double-check your GitHub username and token
- Regenerate your GitHub token if needed
- Verify the `<server><id>` matches between `pom.xml` and `settings.xml`

### "Package not found"
- Wait a few minutes after publishing
- Verify the workflow completed successfully in GitHub Actions
- Check the package visibility is set correctly (public/private)

---

This test project helps verify your library works correctly after publishing!

