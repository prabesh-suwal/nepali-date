package com.prabesh.growphasetech;

import com.prabesh.growphasetech.exceptions.InvalidNepaliDateException;
import com.prabesh.growphasetech.exceptions.NepaliDateException;
import com.prabesh.growphasetech.exceptions.UnsupportedBSYearException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.prabesh.growphasetech.NepaliDateFormatter.Format.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Comprehensive test suite for the Nepali Date library.
 *
 * <p>Tests are organised into nested classes, one per major component.
 * Key conversion anchors are verified against multiple published reference calendars
 * to guard against data-entry errors in the lookup table.</p>
 */
@DisplayName("Nepali Date Library")
class NepaliDateTest {

    // ══════════════════════════════════════════════════════════════════════════
    // NepaliDate record
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("NepaliDate construction and validation")
    class NepaliDateConstructionTest {

        @Test
        void debugAnchor() {
            System.out.println(NepaliDate.fromAD(LocalDate.of(1943,4,14)));
            System.out.println(
                    ChronoUnit.DAYS.between(
                            LocalDate.of(1943,4,14),
                            LocalDate.of(2025,3,15)
                    )
            );
        }

        @Test
        void todayRoundTrip() {
            LocalDate today = LocalDate.now();
            assertEquals(today, NepaliDate.fromAD(today).toAD());
        }


        @Test
        @DisplayName("Valid date constructs without exception")
        void validDateConstructs() {
            assertDoesNotThrow(() -> new NepaliDate(2082, 4, 15));
        }

        @Test
        @DisplayName("Year below minimum throws UnsupportedBSYearException")
        void yearBelowMinThrows() {
            assertThrows(UnsupportedBSYearException.class, () -> new NepaliDate(1899, 1, 1));
        }

        @Test
        @DisplayName("Year above maximum throws UnsupportedBSYearException")
        void yearAboveMaxThrows() {
            assertThrows(UnsupportedBSYearException.class, () -> new NepaliDate(2100, 1, 1));
        }

        @Test
        @DisplayName("Month 0 throws InvalidNepaliDateException")
        void monthZeroThrows() {
            assertThrows(InvalidNepaliDateException.class, () -> new NepaliDate(2082, 0, 1));
        }

        @Test
        @DisplayName("Month 13 throws InvalidNepaliDateException")
        void month13Throws() {
            assertThrows(InvalidNepaliDateException.class, () -> new NepaliDate(2082, 13, 1));
        }

        @Test
        @DisplayName("Day exceeding month length throws InvalidNepaliDateException")
        void dayExceedingMonthThrows() {
            // Baisakh 2082 has 31 days; day 32 should fail
            assertThrows(InvalidNepaliDateException.class, () -> new NepaliDate(2082, 1, 32));
        }

        @Test
        @DisplayName("Day 0 throws InvalidNepaliDateException")
        void dayZeroThrows() {
            assertThrows(InvalidNepaliDateException.class, () -> new NepaliDate(2082, 1, 0));
        }

        @Test
        @DisplayName("Boundary year 2099 is valid")
        void year2099IsValid() {
            assertDoesNotThrow(() -> new NepaliDate(2099, 1, 1));
        }

        @Test
        @DisplayName("Boundary year 2000 is valid")
        void year2000IsValid() {
            assertDoesNotThrow(() -> new NepaliDate(2000, 1, 1));
        }

        @Test
        @DisplayName("NepaliDate.parse accepts YYYY/MM/DD format")
        void parseSlashFormat() {
            NepaliDate date = NepaliDate.parse("2082/04/15");
            assertEquals(new NepaliDate(2082, 4, 15), date);
        }

        @Test
        @DisplayName("NepaliDate.parse accepts YYYY-MM-DD format")
        void parseDashFormat() {
            NepaliDate date = NepaliDate.parse("2082-04-15");
            assertEquals(new NepaliDate(2082, 4, 15), date);
        }

        @Test
        @DisplayName("NepaliDate.parse throws on invalid format")
        void parseInvalidFormat() {
            assertThrows(NepaliDateException.class, () -> NepaliDate.parse("2082.04.15"));
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // AD ↔ BS conversion — verified against published calendars
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("AD ↔ BS conversion")
    class ConversionTest {

        /**
         * Reference conversions cross-checked against hamropatro.com and
         * the Nepal Rastra Bank official calendar.
         *
         * CSV format: AD date | expected BS year | expected BS month | expected BS day
         */
        @ParameterizedTest(name = "AD {0} → BS {1}/{2}/{3}")
        @CsvSource({
            "1943-04-14, 2000, 1,  1",   // Anchor point
            "2024-04-13, 2081, 1,  1",   // Baisakh 1, 2081 (Nepali New Year)
            "2024-04-14, 2081, 1,  2",
            "2025-04-13, 2082, 1,  1",   // Baisakh 1, 2082 (Nepali New Year)
            "2025-03-15, 2081, 12, 2",   // Mid-Falgun
            "2000-01-01, 2056, 9, 17",   // AD millennium → BS
        })
        void adToBsConversion(String adStr, int bsYear, int bsMonth, int bsDay) {
            LocalDate adDate = LocalDate.parse(adStr);
            NepaliDate actual = NepaliDate.fromAD(adDate);
            NepaliDate expected = new NepaliDate(bsYear, bsMonth, bsDay);
            assertEquals(expected, actual,
                () -> "AD " + adStr + " should convert to BS " + expected);
        }

        @ParameterizedTest(name = "BS {0}/{1}/{2} → AD {3}")
        @CsvSource({
            "2000,  1,  1, 1943-04-14",  // Anchor point (reverse)
            "2081,  1,  1, 2024-04-13",  // Baisakh 1, 2081
            "2082,  1,  1, 2025-04-13",  // Baisakh 1, 2082
            "2082,  11,  2, 2026-02-14",  // Falgun 11, 2082
            "2081, 11,  2, 2025-02-14",
        })
        void bsToAdConversion(int bsYear, int bsMonth, int bsDay, String expectedAdStr) {
            NepaliDate bsDate = new NepaliDate(bsYear, bsMonth, bsDay);
            LocalDate actual   = bsDate.toAD();
            LocalDate expected = LocalDate.parse(expectedAdStr);
            assertEquals(expected, actual,
                () -> "BS " + bsDate + " should convert to AD " + expectedAdStr);
        }

        @Test
        @DisplayName("Round-trip: AD → BS → AD should return the original date")
        void roundTripAdToBsToAd() {
            // Test a variety of AD dates to ensure the round-trip is lossless
            List<LocalDate> testDates = List.of(
                LocalDate.of(1943, 4, 14),
                LocalDate.of(2000, 1, 1),
                LocalDate.of(2025, 7, 17),
                LocalDate.of(2043, 4, 13)
            );
            for (LocalDate original : testDates) {
                LocalDate roundTripped = NepaliDate.fromAD(original).toAD();
                assertEquals(original, roundTripped,
                    () -> "Round-trip failed for AD date: " + original);
            }
        }

        @Test
        @DisplayName("Round-trip: BS → AD → BS should return the original date")
        void roundTripBsToAdToBs() {
            List<NepaliDate> testDates = List.of(
                new NepaliDate(2000, 1, 1),
                new NepaliDate(2082, 4, 15),
                new NepaliDate(2099, 1, 1)
            );
            for (NepaliDate original : testDates) {
                NepaliDate roundTripped = NepaliDate.fromAD(original.toAD());
                assertEquals(original, roundTripped,
                    () -> "Round-trip failed for BS date: " + original);
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // NepaliDate comparison
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("NepaliDate comparison")
    class ComparisonTest {

        final NepaliDate earlier = new NepaliDate(2082, 1, 1);
        final NepaliDate later   = new NepaliDate(2082, 6, 15);
        final NepaliDate same    = new NepaliDate(2082, 1, 1);

        @Test void isBeforeWorks()  { assertTrue(earlier.isBefore(later)); }
        @Test void isAfterWorks()   { assertTrue(later.isAfter(earlier)); }
        @Test void isEqualWorks()   { assertTrue(earlier.isEqual(same)); }

        @Test
        void isBetweenInclusive() {
            NepaliDate mid = new NepaliDate(2082, 3, 15);
            assertTrue(mid.isBetween(earlier, later));
            // Boundaries are inclusive
            assertTrue(earlier.isBetween(earlier, later));
            assertTrue(later.isBetween(earlier, later));
        }

        @Test
        void comparatorOrdering() {
            assertTrue(earlier.compareTo(later) < 0);
            assertTrue(later.compareTo(earlier) > 0);
            assertEquals(0, earlier.compareTo(same));
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // NepaliDateFormatter
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("NepaliDateFormatter")
    class FormatterTest {

        final NepaliDate date = new NepaliDate(2082, 4, 15); // Shrawan 15, 2082

        @Test
        @DisplayName("FORMAL_NP produces Devanagari formal date")
        void formalNp() {
            String result = NepaliDateFormatter.format(date, FORMAL_NP);
            assertEquals("२०८२ साल श्रावण १५ गते", result);
        }

        @Test
        @DisplayName("FORMAL_EN produces English formal date")
        void formalEn() {
            assertEquals("15 Shrawan 2082", NepaliDateFormatter.format(date, FORMAL_EN));
        }

        @Test
        @DisplayName("MEMO_HEADER_NP includes मिति: prefix")
        void memoHeaderNp() {
            String result = NepaliDateFormatter.format(date, MEMO_HEADER_NP);
            assertTrue(result.startsWith("मिति:"), "Should start with 'मिति:'");
            assertTrue(result.contains("श्रावण"), "Should contain month name");
        }

        @Test
        @DisplayName("MEMO_HEADER_EN includes Date: prefix")
        void memoHeaderEn() {
            String result = NepaliDateFormatter.format(date, MEMO_HEADER_EN);
            assertTrue(result.startsWith("Date:"), "Should start with 'Date:'");
        }

        @Test
        @DisplayName("SHORT_EN produces zero-padded slash-separated string")
        void shortEn() {
            assertEquals("2082/04/15", NepaliDateFormatter.format(date, SHORT_EN));
        }

        @Test
        @DisplayName("SHORT_NP uses Devanagari digits")
        void shortNp() {
            // Digits 2,0,8,2 → २,०,८,२ etc.
            assertEquals("२०८२/०४/१५", NepaliDateFormatter.format(date, SHORT_NP));
        }

        @Test
        @DisplayName("toNepaliDigits converts each ASCII digit correctly")
        void toNepaliDigits() {
            assertEquals("०", NepaliDateFormatter.toNepaliDigits(0));
            assertEquals("९", NepaliDateFormatter.toNepaliDigits(9));
            assertEquals("२०८२", NepaliDateFormatter.toNepaliDigits(2082));
            assertEquals("१२३४५६७८९०", NepaliDateFormatter.toNepaliDigits(1234567890));
        }

        @Test
        @DisplayName("toAsciiDigits is the inverse of toNepaliDigits")
        void toAsciiDigitsRoundTrip() {
            String original = "2082/04/15";
            String nepali   = NepaliDateFormatter.toNepaliDigits(original);
            String backToAscii = NepaliDateFormatter.toAsciiDigits(nepali);
            assertEquals(original, backToAscii);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // FiscalYear
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("FiscalYear")
    class FiscalYearTest {

        @Test
        @DisplayName("Date in Shrawan (month 4) belongs to FY starting in same year")
        void shrawanBelongsToCurrentFY() {
            NepaliDate shrawan = new NepaliDate(2082, 4, 1); // FY 2082/83 start
            assertEquals("2082/83", FiscalYear.of(shrawan).toString());
        }

        @Test
        @DisplayName("Date in Ashadh (month 3) belongs to FY starting in previous year")
        void ashadhBelongsToPreviousFY() {
            NepaliDate ashadh = new NepaliDate(2082, 3, 31); // last day of FY 2081/82
            assertEquals("2081/82", FiscalYear.of(ashadh).toString());
        }

        @Test
        @DisplayName("FiscalYear.parse parses '2082/83' correctly")
        void parseSlash() {
            FiscalYear fy = FiscalYear.parse("2082/83");
            assertEquals(2082, fy.startYear());
            assertEquals(2083, fy.endYear());
        }

        @Test
        @DisplayName("FiscalYear.parse parses '2082-83' correctly")
        void parseDash() {
            FiscalYear fy = FiscalYear.parse("2082-83");
            assertEquals(2082, fy.startYear());
        }

        @Test
        @DisplayName("startDate is Shrawan 1 of start year")
        void startDate() {
            FiscalYear fy = FiscalYear.parse("2082/83");
            assertEquals(new NepaliDate(2082, 4, 1), fy.startDate());
        }

        @Test
        @DisplayName("endDate is last day of Ashadh in end year")
        void endDate() {
            FiscalYear fy = FiscalYear.parse("2082/83");
            NepaliDate end = fy.endDate();
            assertEquals(2083, end.year());
            assertEquals(3, end.month());
            // Day should equal the actual days in Ashadh 2083
            assertEquals(NepaliCalendarData.getDaysInMonth(2083, 3), end.day());
        }

        @Test
        @DisplayName("toDateRange start and end are correct")
        void toDateRange() {
            FiscalYear fy = FiscalYear.parse("2082/83");
            NepaliDateRange range = fy.toDateRange();
            assertEquals(fy.startDate(), range.start());
            assertEquals(fy.endDate(),   range.end());
        }

        @Test
        @DisplayName("contains() returns true for date within fiscal year")
        void containsTrue() {
            FiscalYear fy = FiscalYear.parse("2082/83");
            assertTrue(fy.contains(new NepaliDate(2082, 8, 10))); // Mangsir 10, 2082
        }

        @Test
        @DisplayName("contains() returns false for date outside fiscal year")
        void containsFalse() {
            FiscalYear fy = FiscalYear.parse("2082/83");
            assertFalse(fy.contains(new NepaliDate(2082, 3, 15))); // Ashadh 15, 2082 → FY 2081/82
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // NepaliDateRange
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("NepaliDateRange")
    class DateRangeTest {

        final NepaliDate from = new NepaliDate(2082, 4, 1);
        final NepaliDate to   = new NepaliDate(2082, 6, 30);
        NepaliDateRange range;

        @BeforeEach
        void setUp() {
            range = new NepaliDateRange(from, to);
        }

        @Test
        @DisplayName("Start-after-end throws InvalidNepaliDateException")
        void invalidRangeThrows() {
            assertThrows(InvalidNepaliDateException.class,
                () -> new NepaliDateRange(to, from));
        }

        @Test
        @DisplayName("Single-day range (start == end) is valid")
        void singleDayRange() {
            assertDoesNotThrow(() -> new NepaliDateRange(from, from));
        }

        @Test
        @DisplayName("contains() is inclusive on both boundaries")
        void containsInclusive() {
            assertTrue(range.contains(from), "Start boundary should be included");
            assertTrue(range.contains(to),   "End boundary should be included");
        }

        @Test
        @DisplayName("contains() returns false for date before range")
        void containsBeforeRange() {
            assertFalse(range.contains(new NepaliDate(2082, 3, 31)));
        }

        @Test
        @DisplayName("contains() returns false for date after range")
        void containsAfterRange() {
            assertFalse(range.contains(new NepaliDate(2082, 7, 1)));
        }

        @Test
        @DisplayName("startAD() and endAD() return valid AD dates")
        void adConversion() {
            LocalDate startAD = range.startAD();
            LocalDate endAD   = range.endAD();
            assertNotNull(startAD);
            assertNotNull(endAD);
            assertTrue(startAD.isBefore(endAD),
                "Start AD should be before end AD");
        }

        @Test
        @DisplayName("filter() returns only items within range")
        void filterItems() {
            // Simulate memos with BS dates
            record Memo(String title, NepaliDate date) {}

            List<Memo> memos = List.of(
                new Memo("Memo A", new NepaliDate(2082, 3, 15)), // before range
                new Memo("Memo B", new NepaliDate(2082, 4, 1)),  // in range (start)
                new Memo("Memo C", new NepaliDate(2082, 5, 20)), // in range
                new Memo("Memo D", new NepaliDate(2082, 7, 1))   // after range
            );

            List<Memo> filtered = range.filter(memos, Memo::date);
            assertEquals(2, filtered.size());
            assertEquals("Memo B", filtered.get(0).title());
            assertEquals("Memo C", filtered.get(1).title());
        }

        @Test
        @DisplayName("totalDays() returns correct count (inclusive)")
        void totalDays() {
            // Single day range
            NepaliDateRange oneDay = new NepaliDateRange(from, from);
            assertEquals(1, oneDay.totalDays());
        }

        @Test
        @DisplayName("ofMonth() creates range covering the full month")
        void ofMonth() {
            NepaliDateRange monthRange = NepaliDateRange.ofMonth(2082, 4);
            assertEquals(1, monthRange.start().day());
            assertEquals(NepaliCalendarData.getDaysInMonth(2082, 4), monthRange.end().day());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // NepaliCalendarData
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("NepaliCalendarData")
    class CalendarDataTest {

        @Test
        @DisplayName("All years 2000–2099 should be present in the data")
        void allYearsPresent() {
            for (int y = 2000; y <= 2099; y++) {
                final int year = y;
                assertDoesNotThrow(
                    () -> NepaliCalendarData.getDaysInYear(year),
                    () -> "Year " + year + " is missing from calendar data"
                );
            }
        }

        @Test
        @DisplayName("Every year should have 365 or 366 days")
        void yearDayCountIsReasonable() {
            for (int y = 2000; y <= 2099; y++) {
                int days = NepaliCalendarData.getDaysInYear(y);
                assertTrue(days == 365 || days == 366,
                    "Year " + y + " has unexpected day count: " + days);
            }
        }

        @Test
        @DisplayName("getMonthData returns a defensive copy (mutation does not affect library)")
        void defensiveCopy() {
            int[] data = NepaliCalendarData.getMonthData(2082);
            int originalFirstValue = data[0];
            data[0] = 999; // mutate the returned array
            int[] data2 = NepaliCalendarData.getMonthData(2082);
            assertEquals(originalFirstValue, data2[0],
                "Mutation of returned array should not affect internal data");
        }

        @Test
        @DisplayName("isYearSupported returns true for boundary years")
        void isYearSupportedBoundaries() {
            assertTrue(NepaliCalendarData.isYearSupported(2000));
            assertTrue(NepaliCalendarData.isYearSupported(2099));
            assertTrue(NepaliCalendarData.isYearSupported(1999));
            assertFalse(NepaliCalendarData.isYearSupported(2100));
            assertFalse(NepaliCalendarData.isYearSupported(1899));

        }
    }
}

    // ══════════════════════════════════════════════════════════════════════════
    // Historical BS 1900–1999 (extended range)
    // ══════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Extended range: BS 1900–1999 (historical data)")
    class HistoricalRangeTest {

        @Test
        @DisplayName("New anchor: BS 1900/01/01 = AD 1843-04-13")
        void anchorBS1900() {
            NepaliDate bs1900 = new NepaliDate(1900, 1, 1);
            assertEquals(LocalDate.of(1843, 4, 13), bs1900.toAD());
        }

        @Test
        @DisplayName("Reverse anchor: AD 1843-04-13 = BS 1900/01/01")
        void reverseAnchorBS1900() {
            assertEquals(new NepaliDate(1900, 1, 1),
                NepaliDate.fromAD(LocalDate.of(1843, 4, 13)));
        }

        @Test
        @DisplayName("Old anchor still resolves correctly: BS 2000/01/01 = AD 1943-04-14")
        void oldAnchorStillWorks() {
            assertEquals(new NepaliDate(2000, 1, 1),
                NepaliDate.fromAD(LocalDate.of(1943, 4, 14)));
        }

        @Test
        @DisplayName("BS 1900 is the minimum supported year")
        void minYearIs1900() {
            assertEquals(1900, NepaliCalendarData.MIN_YEAR);
            assertDoesNotThrow(() -> new NepaliDate(1900, 1, 1));
        }

        @Test
        @DisplayName("BS 1899 is below the supported range and throws")
        void year1899Throws() {
            assertThrows(UnsupportedBSYearException.class,
                () -> new NepaliDate(1899, 1, 1));
        }

        @Test
        @DisplayName("All 200 years (1900–2099) present with 365 or 366 days")
        void allTwoHundredYearsPresent() {
            for (int y = 1900; y <= 2099; y++) {
                final int year = y;
                int days = assertDoesNotThrow(
                    () -> NepaliCalendarData.getDaysInYear(year),
                    () -> "Missing data for BS year " + year);
                assertTrue(days == 365 || days == 366,
                    "BS " + year + " has unexpected day count: " + days);
            }
        }

        @Test
        @DisplayName("Historical spot check: BS 1975/01/01 is in April 1918 AD")
        void bs1975NewYear() {
            LocalDate ad = new NepaliDate(1975, 1, 1).toAD();
            assertEquals(1918, ad.getYear());
            assertEquals(4,    ad.getMonthValue());
        }

        @Test
        @DisplayName("Round-trip for historical dates is lossless")
        void historicalRoundTrip() {
            List<LocalDate> dates = List.of(
                LocalDate.of(1843, 4, 13),  // BS 1900/01/01
                LocalDate.of(1870, 6, 15),  // mid BS 1920s
                LocalDate.of(1900, 1, 1),   // AD 1900 ≈ BS 1956/09
                LocalDate.of(1943, 4, 14)   // BS 2000/01/01
            );
            for (LocalDate original : dates) {
                assertEquals(original, NepaliDate.fromAD(original).toAD(),
                    () -> "Round-trip failed for: " + original);
            }
        }

        @Test
        @DisplayName("FiscalYear works correctly for 1900s dates")
        void fiscalYear1900s() {
            // Shrawan 1975 → FY 1975/76
            assertEquals("1975/76",
                FiscalYear.of(new NepaliDate(1975, 4, 1)).toString());
            // Ashadh 1975 → FY 1974/75
            assertEquals("1974/75",
                FiscalYear.of(new NepaliDate(1975, 3, 30)).toString());
        }

        @Test
        @DisplayName("Date range filter works for 1900s BS dates")
        void dateRangeFilter1900s() {
            NepaliDateRange range = new NepaliDateRange(
                new NepaliDate(1960, 1, 1),
                new NepaliDate(1960, 3, 31));
            assertTrue(range.contains(new NepaliDate(1960, 2, 15)));
            assertFalse(range.contains(new NepaliDate(1960, 4, 1)));
        }
    }

