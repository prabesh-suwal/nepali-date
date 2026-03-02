package com.prabesh.growphasetech;

import com.prabesh.growphasetech.exceptions.NepaliDateException;
import com.prabesh.growphasetech.exceptions.UnsupportedBSYearException;

import java.time.LocalDate;

/**
 * Converts dates between the Gregorian (AD) calendar and the Bikram Sambat (BS) calendar.
 *
 * <h2>Supported range</h2>
 * <p>BS 1900 (approx. AD 1843) through BS 2099 (approx. AD 2043).</p>
 *
 * <h2>Algorithm overview</h2>
 * <p>BS has no mathematical formula, so conversion works by counting days from a
 * known anchor point.</p>
 *
 * <h3>AD to BS</h3>
 * <ol>
 *   <li>Compute days elapsed between anchor (BS 1900/01/01 = AD 1843/04/13) and target.</li>
 *   <li>Walk BS years forward subtracting each year's total until days fit within a year.</li>
 *   <li>Walk months of that year similarly to find month and day.</li>
 * </ol>
 *
 * <h3>BS to AD</h3>
 * <ol>
 *   <li>Sum all days from BS 1900/01/01 to the day before the target.</li>
 *   <li>Add that offset to anchor AD date (1843/04/13).</li>
 * </ol>
 *
 * <h2>Anchor point</h2>
 * <pre>
 *   BS 1900/01/01  =  AD 1843/04/13
 * </pre>
 * <p>Derived from: BS 2000/01/01 = AD 1943/04/14, minus 36,525 days
 * (= sum of days in BS 1900–1999).</p>
 *
 * <p>This class is {@code final} with a private constructor — all methods are static.</p>
 *
 * @see NepaliCalendarData
 * @see NepaliDate
 */
public final class NepaliDateConverter {

    // ── Anchor constants ──────────────────────────────────────────────────────

    /**
     * The Gregorian anchor date corresponding to BS 1900/01/01 (Baisakh 1, 1900).
     *
     * <p>Stored as an epoch day (days since 1970-01-01) for efficient arithmetic
     * via {@link LocalDate#toEpochDay()}.</p>
     *
     * <h4>Anchor derivation</h4>
     * <ul>
     *   <li>Known reference anchor: BS 2000/01/01 = AD 1943/04/14</li>
     *   <li>Sum of days in BS 1900–1999 = 25 × 366 + 75 × 365 = 36,525</li>
     *   <li>AD 1943/04/14 minus 36,525 days = <strong>AD 1843/04/13</strong></li>
     * </ul>
     */
    private static final long AD_EPOCH_DAY = LocalDate.of(1843, 4, 13).toEpochDay();

    /**
     * The BS year corresponding to the anchor date — first year in the lookup table.
     * Day-counting starts from Baisakh 1 of this year.
     */
    private static final int BS_START_YEAR = NepaliCalendarData.MIN_YEAR; // 1900

    /** Private constructor — static utility class only. */
    private NepaliDateConverter() {
        throw new AssertionError("NepaliDateConverter must not be instantiated.");
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Converts a Gregorian (AD) date to Bikram Sambat (BS).
     *
     * @param adDate the Gregorian date to convert; must not be {@code null}
     * @return the equivalent Bikram Sambat date
     * @throws NullPointerException       if {@code adDate} is {@code null}
     * @throws NepaliDateException        if {@code adDate} is before AD 1843/04/13
     *                                    (i.e. BS 1900/01/01)
     * @throws UnsupportedBSYearException if the resulting BS year exceeds 2099
     */
    public static NepaliDate toBS(LocalDate adDate) {
        if (adDate == null) throw new NullPointerException("adDate must not be null");

        // Number of days from the anchor to the target date
        long totalDays = adDate.toEpochDay() - AD_EPOCH_DAY;

        if (totalDays < 0) {
            throw new NepaliDateException(
                "Date " + adDate + " is before the earliest supported BS date "
                + "(BS 1900/01/01 = AD 1843/04/13).");
        }

        int bsYear  = BS_START_YEAR;
        int bsMonth = 1;
        int bsDay   = 1;

        // ── Walk BS years, consuming each year's day count ────────────────────
        for (int y = BS_START_YEAR; y <= NepaliCalendarData.MAX_YEAR; y++) {
            int daysInYear = NepaliCalendarData.getDaysInYear(y);

            if (totalDays < daysInYear) {
                // Target falls within year y
                bsYear = y;

                // ── Walk months within the year ────────────────────────────────
                int[] monthData = NepaliCalendarData.getMonthData(y);
                for (int m = 0; m < 12; m++) {
                    if (totalDays < monthData[m]) {
                        bsMonth = m + 1;               // 0-based index → 1-based month
                        bsDay   = (int) totalDays + 1; // 0 remaining → day 1
                        break;
                    }
                    totalDays -= monthData[m];
                }
                break;
            }
            totalDays -= daysInYear;
        }

        return new NepaliDate(bsYear, bsMonth, bsDay);
    }

    /**
     * Converts a Bikram Sambat (BS) date to the Gregorian (AD) calendar.
     *
     * @param bsDate the Bikram Sambat date to convert; must not be {@code null}
     * @return the equivalent {@link LocalDate} in the Gregorian calendar
     * @throws NullPointerException       if {@code bsDate} is {@code null}
     * @throws UnsupportedBSYearException if {@code bsDate.year()} is outside 1900–2099
     */
//    public static LocalDate toAD(NepaliDate bsDate) {
//        if (bsDate == null) throw new NullPointerException("bsDate must not be null");
//
//        long totalDays = 0;
//
//        // Sum all complete years from the start year up to (but not including) the target year
//        for (int y = BS_START_YEAR; y < bsDate.year(); y++) {
//            totalDays += NepaliCalendarData.getDaysInYear(y);
//        }
//
//        // Sum complete months in the target year before the target month
//        int[] monthData = NepaliCalendarData.getMonthData(bsDate.year());
//        for (int m = 0; m < bsDate.month() - 1; m++) {
//            totalDays += monthData[m];
//        }
//
//        // Add the day offset: day 1 contributes 0 extra days
//        totalDays += bsDate.day() - 1;
//
//        return LocalDate.ofEpochDay(AD_EPOCH_DAY + totalDays);
//    }
    public static LocalDate toAD(NepaliDate bsDate) {
        long totalDays = NepaliCalendarData.getDaysBeforeYear(bsDate.year());

        int[] monthData = NepaliCalendarData.getMonthData(bsDate.year());
        for (int m = 0; m < bsDate.month() - 1; m++) {
            totalDays += monthData[m];
        }
        totalDays += bsDate.day() - 1;

        return LocalDate.ofEpochDay(AD_EPOCH_DAY + totalDays);
    }
}
