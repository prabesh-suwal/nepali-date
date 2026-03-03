package com.prabesh.growphasetech;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link FiscalYearFormatter}.
 *
 * <p>Tests all format options (STANDARD_NP, STANDARD_EN, FORMAL_NP, FORMAL_EN)
 * and convenience methods for various fiscal years.</p>
 */
@DisplayName("FiscalYearFormatter tests")
class FiscalYearFormatterTest {

    // ────────────────────────────────────────────────────────────────────────────
    // STANDARD_NP tests (Nepali digits: २०८२/०३)
    // ────────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("STANDARD_NP: basic fiscal year 2082/83")
    void testStandardNP_BasicFiscalYear() {
        FiscalYear fy = new FiscalYear(2082);
        String result = FiscalYearFormatter.format(fy, FiscalYearFormatter.Format.STANDARD_NP);
        assertEquals("२०८२/८३", result, "FY 2082 should format to २०८२/८३");
    }

    @Test
    @DisplayName("STANDARD_NP: fiscal year 2081/82")
    void testStandardNP_FiscalYear2081() {
        FiscalYear fy = new FiscalYear(2081);
        String result = FiscalYearFormatter.format(fy, FiscalYearFormatter.Format.STANDARD_NP);
        assertEquals("२०८१/८२", result, "FY 2081 should format to २०८१/८२");
    }

    @Test
    @DisplayName("STANDARD_NP: fiscal year 2000/01 (edge case: start of range)")
    void testStandardNP_MinimumYear() {
        FiscalYear fy = new FiscalYear(2000);
        String result = FiscalYearFormatter.format(fy, FiscalYearFormatter.Format.STANDARD_NP);
        assertEquals("२०००/०१", result, "FY 2000 should format to २०००/०१");
    }

    @Test
    @DisplayName("STANDARD_NP: fiscal year 2099/00 (edge case: end of range)")
    void testStandardNP_MaximumYear() {
        FiscalYear fy = new FiscalYear(2099);
        String result = FiscalYearFormatter.format(fy, FiscalYearFormatter.Format.STANDARD_NP);
        assertEquals("२०९९/००", result, "FY 2099 should format to २०९९/००");
    }

    @Test
    @DisplayName("STANDARD_NP: convenience method formatStandardNP()")
    void testFormatStandardNP_ConvenienceMethod() {
        FiscalYear fy = new FiscalYear(2082);
        String result = FiscalYearFormatter.formatStandardNP(fy);
        assertEquals("२०८२/८३", result, "formatStandardNP() should work");
    }

    // ────────────────────────────────────────────────────────────────────────────
    // STANDARD_EN tests (ASCII digits: 2082/83)
    // ────────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("STANDARD_EN: basic fiscal year 2082/83")
    void testStandardEN_BasicFiscalYear() {
        FiscalYear fy = new FiscalYear(2082);
        String result = FiscalYearFormatter.format(fy, FiscalYearFormatter.Format.STANDARD_EN);
        assertEquals("2082/83", result, "FY 2082 should format to 2082/83");
    }

    @Test
    @DisplayName("STANDARD_EN: fiscal year 2081/82")
    void testStandardEN_FiscalYear2081() {
        FiscalYear fy = new FiscalYear(2081);
        String result = FiscalYearFormatter.format(fy, FiscalYearFormatter.Format.STANDARD_EN);
        assertEquals("2081/82", result, "FY 2081 should format to 2081/82");
    }

    @Test
    @DisplayName("STANDARD_EN: fiscal year 2000/01 (edge case: start of range)")
    void testStandardEN_MinimumYear() {
        FiscalYear fy = new FiscalYear(2000);
        String result = FiscalYearFormatter.format(fy, FiscalYearFormatter.Format.STANDARD_EN);
        assertEquals("2000/01", result, "FY 2000 should format to 2000/01");
    }

    @Test
    @DisplayName("STANDARD_EN: fiscal year 2099/00 (edge case: end of range)")
    void testStandardEN_MaximumYear() {
        FiscalYear fy = new FiscalYear(2099);
        String result = FiscalYearFormatter.format(fy, FiscalYearFormatter.Format.STANDARD_EN);
        assertEquals("2099/00", result, "FY 2099 should format to 2099/00");
    }

    @Test
    @DisplayName("STANDARD_EN: convenience method formatStandardEN()")
    void testFormatStandardEN_ConvenienceMethod() {
        FiscalYear fy = new FiscalYear(2082);
        String result = FiscalYearFormatter.formatStandardEN(fy);
        assertEquals("2082/83", result, "formatStandardEN() should work");
    }

    @Test
    @DisplayName("STANDARD_EN: matches FiscalYear.toString()")
    void testStandardEN_MatchesDefaultToString() {
        FiscalYear fy = new FiscalYear(2082);
        String formattedEN = FiscalYearFormatter.format(fy, FiscalYearFormatter.Format.STANDARD_EN);
        String toStringResult = fy.toString();
        assertEquals(toStringResult, formattedEN,
                "STANDARD_EN format should match FiscalYear.toString()");
    }

    // ────────────────────────────────────────────────────────────────────────────
    // FORMAL_NP tests (Nepali format: श्रावण २०८२ - असार २०८३)
    // ────────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("FORMAL_NP: basic fiscal year 2082/83")
    void testFormalNP_BasicFiscalYear() {
        FiscalYear fy = new FiscalYear(2082);
        String result = FiscalYearFormatter.format(fy, FiscalYearFormatter.Format.FORMAL_NP);
        assertEquals("श्रावण २०८२ - असार २०८३", result,
                "FY 2082 should format to श्रावण २०८२ - असार २०८३");
    }

    @Test
    @DisplayName("FORMAL_NP: fiscal year 2081/82")
    void testFormalNP_FiscalYear2081() {
        FiscalYear fy = new FiscalYear(2081);
        String result = FiscalYearFormatter.format(fy, FiscalYearFormatter.Format.FORMAL_NP);
        assertEquals("श्रावण २०८१ - असार २०८२", result,
                "FY 2081 should format to श्रावण २०८१ - असार २०८२");
    }

    @Test
    @DisplayName("FORMAL_NP: fiscal year 2000/01 (edge case: start of range)")
    void testFormalNP_MinimumYear() {
        FiscalYear fy = new FiscalYear(2000);
        String result = FiscalYearFormatter.format(fy, FiscalYearFormatter.Format.FORMAL_NP);
        assertEquals("श्रावण २००० - असार २००१", result,
                "FY 2000 should format to श्रावण २००० - असार २००१");
    }

    @Test
    @DisplayName("FORMAL_NP: fiscal year 2099/00 (edge case: end of range)")
    void testFormalNP_MaximumYear() {
        FiscalYear fy = new FiscalYear(2099);
        String result = FiscalYearFormatter.format(fy, FiscalYearFormatter.Format.FORMAL_NP);
        assertEquals("श्रावण २०९९ - असार २१००", result,
                "FY 2099 should format to श्रावण २०९९ - असार २१००");
    }

    @Test
    @DisplayName("FORMAL_NP: convenience method formatFormalNP()")
    void testFormatFormalNP_ConvenienceMethod() {
        FiscalYear fy = new FiscalYear(2082);
        String result = FiscalYearFormatter.formatFormalNP(fy);
        assertEquals("श्रावण २०८२ - असार २०८३", result, "formatFormalNP() should work");
    }

    // ────────────────────────────────────────────────────────────────────────────
    // FORMAL_EN tests (English format: Shrawan 2082 - Ashadh 2083)
    // ────────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("FORMAL_EN: basic fiscal year 2082/83")
    void testFormalEN_BasicFiscalYear() {
        FiscalYear fy = new FiscalYear(2082);
        String result = FiscalYearFormatter.format(fy, FiscalYearFormatter.Format.FORMAL_EN);
        assertEquals("Shrawan 2082 - Ashadh 2083", result,
                "FY 2082 should format to Shrawan 2082 - Ashadh 2083");
    }

    @Test
    @DisplayName("FORMAL_EN: fiscal year 2081/82")
    void testFormalEN_FiscalYear2081() {
        FiscalYear fy = new FiscalYear(2081);
        String result = FiscalYearFormatter.format(fy, FiscalYearFormatter.Format.FORMAL_EN);
        assertEquals("Shrawan 2081 - Ashadh 2082", result,
                "FY 2081 should format to Shrawan 2081 - Ashadh 2082");
    }

    @Test
    @DisplayName("FORMAL_EN: fiscal year 2000/01 (edge case: start of range)")
    void testFormalEN_MinimumYear() {
        FiscalYear fy = new FiscalYear(2000);
        String result = FiscalYearFormatter.format(fy, FiscalYearFormatter.Format.FORMAL_EN);
        assertEquals("Shrawan 2000 - Ashadh 2001", result,
                "FY 2000 should format to Shrawan 2000 - Ashadh 2001");
    }

    @Test
    @DisplayName("FORMAL_EN: fiscal year 2099/00 (edge case: end of range)")
    void testFormalEN_MaximumYear() {
        FiscalYear fy = new FiscalYear(2099);
        String result = FiscalYearFormatter.format(fy, FiscalYearFormatter.Format.FORMAL_EN);
        assertEquals("Shrawan 2099 - Ashadh 2100", result,
                "FY 2099 should format to Shrawan 2099 - Ashadh 2100");
    }

    @Test
    @DisplayName("FORMAL_EN: convenience method formatFormalEN()")
    void testFormatFormalEN_ConvenienceMethod() {
        FiscalYear fy = new FiscalYear(2082);
        String result = FiscalYearFormatter.formatFormalEN(fy);
        assertEquals("Shrawan 2082 - Ashadh 2083", result, "formatFormalEN() should work");
    }

    // ────────────────────────────────────────────────────────────────────────────
    // Null checks
    // ────────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("format(): throws NullPointerException if fiscalYear is null")
    void testFormat_NullFiscalYear() {
        assertThrows(NullPointerException.class,
                () -> FiscalYearFormatter.format(null, FiscalYearFormatter.Format.STANDARD_EN),
                "format() should throw NullPointerException for null fiscalYear");
    }

    @Test
    @DisplayName("format(): throws NullPointerException if format is null")
    void testFormat_NullFormat() {
        FiscalYear fy = new FiscalYear(2082);
        assertThrows(NullPointerException.class,
                () -> FiscalYearFormatter.format(fy, null),
                "format() should throw NullPointerException for null format");
    }

    @Test
    @DisplayName("formatStandardNP(): throws NullPointerException if fiscalYear is null")
    void testFormatStandardNP_Null() {
        assertThrows(NullPointerException.class,
                () -> FiscalYearFormatter.formatStandardNP(null));
    }

    @Test
    @DisplayName("formatStandardEN(): throws NullPointerException if fiscalYear is null")
    void testFormatStandardEN_Null() {
        assertThrows(NullPointerException.class,
                () -> FiscalYearFormatter.formatStandardEN(null));
    }

    @Test
    @DisplayName("formatFormalNP(): throws NullPointerException if fiscalYear is null")
    void testFormatFormalNP_Null() {
        assertThrows(NullPointerException.class,
                () -> FiscalYearFormatter.formatFormalNP(null));
    }

    @Test
    @DisplayName("formatFormalEN(): throws NullPointerException if fiscalYear is null")
    void testFormatFormalEN_Null() {
        assertThrows(NullPointerException.class,
                () -> FiscalYearFormatter.formatFormalEN(null));
    }

    // ────────────────────────────────────────────────────────────────────────────
    // Integration tests with FiscalYear.of()
    // ────────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Integration: format FiscalYear from NepaliDate (Shrawan)")
    void testIntegration_FiscalYearFromNepaliDate_Shrawan() {
        NepaliDate date = new NepaliDate(2082, 5, 10); // Bhadra 10, 2082 → FY 2082/83
        FiscalYear fy = FiscalYear.of(date);
        String result = FiscalYearFormatter.formatStandardNP(fy);
        assertEquals("२०८२/८३", result);
    }

    @Test
    @DisplayName("Integration: format FiscalYear from NepaliDate (Baisakh)")
    void testIntegration_FiscalYearFromNepaliDate_Baisakh() {
        NepaliDate date = new NepaliDate(2082, 1, 15); // Baisakh 15, 2082 → FY 2081/82
        FiscalYear fy = FiscalYear.of(date);
        String result = FiscalYearFormatter.formatStandardNP(fy);
        assertEquals("२०८१/८२", result);
    }

    @Test
    @DisplayName("Integration: format FiscalYear parsed from string")
    void testIntegration_FiscalYearFromParse() {
        FiscalYear fy = FiscalYear.parse("2082/83");
        String nepaliFormat = FiscalYearFormatter.formatStandardNP(fy);
        String formalFormat = FiscalYearFormatter.formatFormalNP(fy);
        assertEquals("२०८२/८३", nepaliFormat);
        assertEquals("श्रावण २०८२ - असार २०८३", formalFormat);
    }

    // ────────────────────────────────────────────────────────────────────────────
    // Exhaustiveness test (ensure all format enum values are handled)
    // ────────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("All Format enum values are handled (no exceptions)")
    void testAllFormatValuesHandled() {
        FiscalYear fy = new FiscalYear(2082);
        for (FiscalYearFormatter.Format format : FiscalYearFormatter.Format.values()) {
            assertDoesNotThrow(() -> FiscalYearFormatter.format(fy, format),
                    "format() should handle " + format);
        }
    }
}

