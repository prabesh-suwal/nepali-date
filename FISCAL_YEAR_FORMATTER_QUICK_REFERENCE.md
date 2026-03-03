# FiscalYearFormatter Quick Reference

## Basic Usage

```java
import com.prabesh.growphasetech.*;

// Get current fiscal year
FiscalYear fy = FiscalYear.of(NepaliDate.today());

// Format in Nepali with Devanagari digits
String nepali = FiscalYearFormatter.formatStandardNP(fy);
// Example: "२०८२/८३"

// Format in English with ASCII digits
String english = FiscalYearFormatter.formatStandardEN(fy);
// Example: "2082/83"

// Format with full month names (Nepali)
String nepaliFormat = FiscalYearFormatter.formatFormalNP(fy);
// Example: "श्रावण २०८२ - असार २०८३"

// Format with full month names (English)
String englishFormat = FiscalYearFormatter.formatFormalEN(fy);
// Example: "Shrawan 2082 - Ashadh 2083"
```

## All Format Options

```java
// Using the enum directly
FiscalYearFormatter.format(fy, FiscalYearFormatter.Format.STANDARD_NP)
FiscalYearFormatter.format(fy, FiscalYearFormatter.Format.STANDARD_EN)
FiscalYearFormatter.format(fy, FiscalYearFormatter.Format.FORMAL_NP)
FiscalYearFormatter.format(fy, FiscalYearFormatter.Format.FORMAL_EN)
```

## Common Scenarios

### Scenario 1: Display fiscal year in UI (Nepali interface)
```java
FiscalYear fy = FiscalYear.parse("2082/83");
String display = FiscalYearFormatter.formatStandardNP(fy);
// Use in: Labels, headers, reports → "२०८२/८३"
```

### Scenario 2: Log/Store fiscal year (system format)
```java
FiscalYear fy = FiscalYear.of(someNepaliDate);
String stored = FiscalYearFormatter.formatStandardEN(fy);
// Use in: Logs, databases → "2082/83"
```

### Scenario 3: Official government document (Nepali)
```java
FiscalYear fy = FiscalYear.parse("2082/83");
String officialText = FiscalYearFormatter.formatFormalNP(fy);
// Use in: Government letters, official documents
// → "श्रावण २०८२ - असार २०८३"
```

### Scenario 4: English government correspondence
```java
FiscalYear fy = FiscalYear.parse("2082/83");
String englishOfficial = FiscalYearFormatter.formatFormalEN(fy);
// Use in: English official documents
// → "Shrawan 2082 - Ashadh 2083"
```

## Format Output Reference

| Input | STANDARD_NP | STANDARD_EN | FORMAL_NP | FORMAL_EN |
|-------|-------------|-----------|-----------|-----------|
| FY(2000) | २०००/०१ | 2000/01 | श्रावण २००० - असार २००१ | Shrawan 2000 - Ashadh 2001 |
| FY(2082) | २०८२/८३ | 2082/83 | श्रावण २०८२ - असार २०८३ | Shrawan 2082 - Ashadh 2083 |
| FY(2099) | २०९९/०० | 2099/00 | श्रावण २०९९ - असार २१०० | Shrawan 2099 - Ashadh 2100 |

## Error Handling

```java
// Null fiscal year throws NullPointerException
FiscalYearFormatter.formatStandardNP(null);  // ❌ throws NPE

// Null format throws NullPointerException
FiscalYearFormatter.format(fy, null);  // ❌ throws NPE

// Valid inputs work perfectly
FiscalYearFormatter.formatStandardNP(new FiscalYear(2082));  // ✅
```

## Integration with Other Classes

```java
// From NepaliDate to formatted fiscal year
NepaliDate date = new NepaliDate(2082, 5, 10);
FiscalYear fy = date.getFiscalYear();  // or FiscalYear.of(date)
String formatted = FiscalYearFormatter.formatStandardNP(fy);

// From date range to formatted fiscal year
NepaliDateRange range = fy.toDateRange();
String formatted = FiscalYearFormatter.formatFormalNP(fy);
```

## Thread Safety

✅ All methods are **thread-safe** and **stateless**
- No shared mutable state
- Can be safely called from multiple threads simultaneously
- No synchronization needed

## Performance

✅ **Zero allocation overhead** for most use cases
✅ **Direct string formatting** - no intermediate object creation
✅ **Reuses** `NepaliDateFormatter` utilities

## Nepali Language Details

### Supported Month Names
- Shrawan (श्रावण) - Fiscal year start
- Ashadh (असार) - Fiscal year end

### Devanagari Digits
- 0 → ०, 1 → १, 2 → २, 3 → ३, 4 → ४
- 5 → ५, 6 → ६, 7 → ७, 8 → ८, 9 → ९

## Related Documentation

- `FiscalYear` - Fiscal year calculations and operations
- `NepaliDateFormatter` - Individual date formatting
- `NepaliDate` - Core Bikram Sambat date type
- Package documentation - Full API overview

