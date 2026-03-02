# 🗓️ Nepali Date — Bikram Sambat for Java 21

[![CI](https://github.com/prabesh-suwal/nepali-date/actions/workflows/publish.yml/badge.svg)](https://github.com/prabesh-suwal/nepali-date/actions)
[![GitHub Packages](https://img.shields.io/badge/GitHub%20Packages-v1.0.0-blue)](https://github.com/prabesh-suwal/nepali-date/packages)
[![Java 21](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/projects/jdk/21/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

A **zero-dependency**, immutable Java 21 library for the **Bikram Sambat (BS)** calendar —
the official calendar of Nepal. Coverage: **BS 2000 – 2099** (≈ AD 1943 – 2043).

---

## ✨ Features

| Feature | Description |
|---|---|
| **AD ↔ BS conversion** | Accurate lookup-table conversion for any date in BS 2000–2099 |
| **Fiscal year** | Nepal FY logic (Shrawan 1 → Ashad end), e.g. `"2082/83"` |
| **Date range search** | Filter lists or generate AD ranges for DB `BETWEEN` queries |
| **Official formatting** | Government letter headings in Nepali script and English |
| **Nepali digits** | Convert between ASCII (0–9) and Devanagari (०–९) digits |
| **Java 21 records** | Immutable, thread-safe, value-based types |
| **Zero dependencies** | Pure Java — no third-party runtime libraries |

---

## 📦 Installation

### Maven (via GitHub Packages)

Add the repository and dependency to your `pom.xml`:

```xml
<!-- 1. Declare the GitHub Packages repository -->
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/prabesh-suwal/nepali-date</url>
    </repository>
</repositories>

<!-- 2. Add the dependency -->
<dependencies>
    <dependency>
        <groupId>com.prabesh.growphasetech</groupId>
        <artifactId>nepali-date</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

> **Note:** GitHub Packages requires authentication even for public packages.
> Add this to your `~/.m2/settings.xml`:
> ```xml
> <servers>
>   <server>
>     <id>github</id>
>     <username>YOUR_GITHUB_USERNAME</username>
>     <password>YOUR_GITHUB_TOKEN</password>  <!-- needs read:packages scope -->
>   </server>
> </servers>
> ```

### Gradle (Kotlin DSL)

```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/prabesh-suwal/nepali-date")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key")  as String? ?: System.getenv("TOKEN")
        }
    }
}

dependencies {
    implementation("com.prabesh.growphasetech:nepali-date:1.0.0")
}
```

---

## 🚀 Quick Start

```java
import com.prabesh.growphasetech.*;
import com.prabesh.growphasetech.NepaliDateFormatter.Format;
import java.time.LocalDate;

// ── Today in Bikram Sambat ───────────────────────────────────────────────────
NepaliDate today = NepaliDate.today();
System.out.println(today);  // 2082/04/15

// ── Convert AD → BS ──────────────────────────────────────────────────────────
NepaliDate bsDate = NepaliDate.fromAD(LocalDate.of(2025, 7, 17));
System.out.println(bsDate); // 2082/04/01

// ── Convert BS → AD ──────────────────────────────────────────────────────────
LocalDate adDate = new NepaliDate(2082, 4, 1).toAD();
System.out.println(adDate); // 2025-07-17

// ── Parse a date string ──────────────────────────────────────────────────────
NepaliDate parsed = NepaliDate.parse("2082/04/15");
NepaliDate parsed2 = NepaliDate.parse("2082-04-15"); // dash also works
```

---

## 📝 Official Letter Formatting

```java
NepaliDate date = new NepaliDate(2082, 4, 15);

// Government standard (most common in official letters)
NepaliDateFormatter.format(date, Format.MEMO_HEADER_NP);
// → "मिति: २०८२ साल श्रावण १५ गते"

NepaliDateFormatter.format(date, Format.FORMAL_NP);
// → "२०८२ साल श्रावण १५ गते"

NepaliDateFormatter.format(date, Format.FORMAL_EN);
// → "15 Shrawan 2082"

NepaliDateFormatter.format(date, Format.MEMO_HEADER_EN);
// → "Date: 15 Shrawan 2082"

NepaliDateFormatter.format(date, Format.SHORT_NP);
// → "२०८२/०४/१५"

NepaliDateFormatter.format(date, Format.SHORT_EN);
// → "2082/04/15"

// Digit conversion utilities
NepaliDateFormatter.toNepaliDigits(2082);    // → "२०८२"
NepaliDateFormatter.toAsciiDigits("२०८२");   // → "2082"
```

---

## 📅 Fiscal Year (आर्थिक वर्ष)

Nepal's fiscal year runs **Shrawan 1 → Ashadh end** (month 4 → month 3 next year).

```java
// Get the fiscal year of a BS date
NepaliDate date = new NepaliDate(2082, 5, 10); // Bhadra 10
FiscalYear fy = FiscalYear.of(date);
System.out.println(fy);             // "2082/83"
System.out.println(fy.startDate()); // 2082/04/01  (Shrawan 1)
System.out.println(fy.endDate());   // 2083/03/31  (last day of Ashadh)

// Parse from string
FiscalYear fy2 = FiscalYear.parse("2082/83");

// Check if a date is in the fiscal year
fy.contains(new NepaliDate(2082, 8, 1)); // true — Mangsir is in FY 2082/83

// Month 3 (Ashadh) of 2082 belongs to the PREVIOUS fiscal year
FiscalYear.of(new NepaliDate(2082, 3, 15)).toString(); // "2081/82"
```

---

## 🔍 Date Range Search

```java
// ── Define a range ───────────────────────────────────────────────────────────
NepaliDateRange q1 = new NepaliDateRange(
    new NepaliDate(2082, 4, 1),
    new NepaliDate(2082, 6, 30)
);

// Check if a date is in the range
q1.contains(new NepaliDate(2082, 5, 15)); // true
q1.contains(LocalDate.of(2025, 8, 10));   // also works with AD date

// ── Filter a list ─────────────────────────────────────────────────────────────
List<Memo> q1Memos = q1.filter(allMemos, Memo::getBsDate);

// ── Get AD range for JPA/SQL query ────────────────────────────────────────────
LocalDate startAD = q1.startAD();
LocalDate endAD   = q1.endAD();
// Use in JPQL: WHERE m.dateAd BETWEEN :startAD AND :endAD

// ── Fiscal year range ─────────────────────────────────────────────────────────
NepaliDateRange fyRange = FiscalYear.parse("2082/83").toDateRange();
List<Memo> fyMemos = fyRange.filterByAD(allMemos, Memo::getDateAd);

// ── Whole month range ──────────────────────────────────────────────────────────
NepaliDateRange shrawan2082 = NepaliDateRange.ofMonth(2082, 4);
```

---

## 🏛️ Recommended Database Design

```sql
CREATE TABLE memos (
    id           BIGSERIAL PRIMARY KEY,
    memo_number  VARCHAR(30),

    -- Store in AD for native DB date indexing and reliable ordering
    date_ad      DATE        NOT NULL,

    -- Store BS components for Nepali-script display and fast FY filtering
    bs_year      SMALLINT    NOT NULL,
    bs_month     SMALLINT    NOT NULL,
    bs_day       SMALLINT    NOT NULL,

    -- Computed and stored for zero-cost fiscal year filtering
    fiscal_year  VARCHAR(7)  NOT NULL,    -- e.g. "2082/83"

    subject      VARCHAR(500),
    content      TEXT,
    created_at   TIMESTAMPTZ DEFAULT NOW()
);

-- Index AD date for range queries (BETWEEN)
CREATE INDEX idx_memo_date_ad   ON memos (date_ad);
-- Index BS components for month/year display pages
CREATE INDEX idx_memo_bs_date   ON memos (bs_year, bs_month, bs_day);
-- Index fiscal year for one-column fiscal year filtering
CREATE INDEX idx_memo_fy        ON memos (fiscal_year);
```

### Spring Data JPA Repository

```java
@Repository
public interface MemoRepository extends JpaRepository<Memo, Long> {

    // Fast fiscal year query using the stored fiscal_year string
    List<Memo> findByFiscalYearOrderByDateAdAsc(String fiscalYear);

    // Date range using the AD date column (best performance)
    @Query("SELECT m FROM Memo m WHERE m.dateAd BETWEEN :start AND :end ORDER BY m.dateAd")
    List<Memo> findByDateRange(@Param("start") LocalDate start,
                               @Param("end")   LocalDate end);

    // Month filter using stored BS components
    List<Memo> findByBsYearAndBsMonthOrderByBsDayAsc(int bsYear, int bsMonth);
}

// Service layer example
@Service
public class MemoService {

    public List<Memo> searchByFiscalYear(String fiscalYearStr) {
        FiscalYear fy = FiscalYear.parse(fiscalYearStr); // e.g. "2082/83"
        NepaliDateRange range = fy.toDateRange();
        return memoRepository.findByDateRange(range.startAD(), range.endAD());
    }

    public List<Memo> searchByBsRange(String bsStart, String bsEnd) {
        NepaliDateRange range = new NepaliDateRange(
            NepaliDate.parse(bsStart),  // e.g. "2082/04/01"
            NepaliDate.parse(bsEnd)     // e.g. "2082/06/30"
        );
        return memoRepository.findByDateRange(range.startAD(), range.endAD());
    }
}
```

---

## 📐 API Reference

### `NepaliDate` (record)

| Method | Description |
|---|---|
| `NepaliDate.today()` | Current date in BS |
| `NepaliDate.fromAD(LocalDate)` | Convert AD → BS |
| `NepaliDate.parse(String)` | Parse `"YYYY/MM/DD"` or `"YYYY-MM-DD"` |
| `toAD()` | Convert BS → AD `LocalDate` |
| `getFiscalYear()` | Get containing `FiscalYear` |
| `lengthOfMonth()` | Days in this date's BS month |
| `isBefore/isAfter/isEqual/isBetween(...)` | Comparisons |

### `NepaliDateFormatter`

| Format constant | Output example |
|---|---|
| `FORMAL_NP` | `२०८२ साल श्रावण १५ गते` |
| `FORMAL_EN` | `15 Shrawan 2082` |
| `MEMO_HEADER_NP` | `मिति: २०८२ साल श्रावण १५ गते` |
| `MEMO_HEADER_EN` | `Date: 15 Shrawan 2082` |
| `SHORT_NP` | `२०८२/०४/१५` |
| `SHORT_EN` | `2082/04/15` |
| `ISO_NP` | `२०८२-०४-१५` |

### `FiscalYear` (record)

| Method | Description |
|---|---|
| `FiscalYear.of(NepaliDate)` | Get FY for a date |
| `FiscalYear.parse("2082/83")` | Parse FY string |
| `startDate()` | Shrawan 1 of start year |
| `endDate()` | Last day of Ashadh of end year |
| `toDateRange()` | Full `NepaliDateRange` |
| `contains(NepaliDate)` | Date membership check |
| `toString()` | `"2082/83"` |

### `NepaliDateRange` (record)

| Method | Description |
|---|---|
| `contains(NepaliDate)` | Check BS date membership (inclusive) |
| `contains(LocalDate)` | Check AD date membership |
| `startAD()` / `endAD()` | AD equivalents for DB queries |
| `filter(list, extractor)` | Filter by BS date |
| `filterByAD(list, extractor)` | Filter by AD date |
| `totalDays()` | Number of days in range |
| `NepaliDateRange.ofMonth(y, m)` | Full-month range |
| `NepaliDateRange.ofYear(y)` | Full-year range |

---

## 🗺️ BS Month Quick Reference

| No. | English | नेपाली | Approx. AD |
|---|---|---|---|
| 1 | Baisakh | बैशाख | mid-Apr – mid-May |
| 2 | Jestha | जेठ | mid-May – mid-Jun |
| 3 | Ashadh | असार | mid-Jun – mid-Jul |
| **4** | **Shrawan** | **श्रावण** | **mid-Jul – mid-Aug** ← FY Start |
| 5 | Bhadra | भाद्र | mid-Aug – mid-Sep |
| 6 | Ashwin | आश्विन | mid-Sep – mid-Oct |
| 7 | Kartik | कार्तिक | mid-Oct – mid-Nov |
| 8 | Mangsir | मंसिर | mid-Nov – mid-Dec |
| 9 | Poush | पुष | mid-Dec – mid-Jan |
| 10 | Magh | माघ | mid-Jan – mid-Feb |
| 11 | Falgun | फाल्गुन | mid-Feb – mid-Mar |
| 12 | Chaitra | चैत्र | mid-Mar – mid-Apr |

---

## 📜 Publishing a New Release

```bash
# 1. Update version in pom.xml (e.g., 1.1.0)
# 2. Commit and push
git add pom.xml && git commit -m "chore: bump version to 1.1.0"
git push origin main

# 3. Create and push a release tag — this triggers the GitHub Actions publish job
git tag v1.1.0
git push origin v1.1.0

# 4. Create a GitHub Release from the tag in the GitHub UI
#    → Actions workflow will automatically publish to GitHub Packages
```

---

## 🤝 Contributing

Contributions are welcome! If you find incorrect date data or want to extend
coverage beyond 2099, please open an issue or pull request on GitHub.

When adding new year data to `NepaliCalendarData`, please cross-reference at least
two authoritative sources (e.g., hamropatro.com and the Nepal Rastra Bank calendar)
and add a comment citing your sources.

---

## 📄 License

MIT License — see [LICENSE](LICENSE) for details.
