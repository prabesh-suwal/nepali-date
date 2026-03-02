package com.prabesh.growphasetech;

import com.prabesh.growphasetech.exceptions.InvalidNepaliDateException;

/**
 * Represents a Nepal government fiscal year (आर्थिक वर्ष).
 *
 * <h2>Nepal fiscal year structure</h2>
 * <p>Nepal's fiscal year runs from <strong>Shrawan 1</strong> (BS month 4,
 * approximately mid-July AD) to the last day of <strong>Ashadh</strong>
 * (BS month 3, approximately mid-July AD the following year).</p>
 *
 * <p>A fiscal year is written as {@code "YYYY/YY"} — e.g., {@code "2082/83"}
 * meaning it starts in BS 2082 and ends in BS 2083. Only the last two digits
 * of the end year are written.</p>
 *
 * <h3>Month assignment logic</h3>
 * <pre>
 *  BS Month  Name      Fiscal Year Assignment
 *  ────────  ────────  ─────────────────────────────────────────────
 *    1       Baisakh   FY starting the PREVIOUS calendar year
 *    2       Jestha    FY starting the PREVIOUS calendar year
 *    3       Ashadh    FY starting the PREVIOUS calendar year
 *    4       Shrawan   FY starting THIS calendar year       ← FY start
 *    5       Bhadra    FY starting THIS calendar year
 *    ...     ...       FY starting THIS calendar year
 *   12       Chaitra   FY starting THIS calendar year
 * </pre>
 *
 * <h3>Example</h3>
 * <pre>{@code
 * NepaliDate date1 = new NepaliDate(2082, 5, 10);  // Bhadra 10, 2082
 * FiscalYear fy1 = FiscalYear.of(date1);
 * System.out.println(fy1); // "2082/83" — Bhadra is in the FY that started Shrawan 2082
 *
 * NepaliDate date2 = new NepaliDate(2082, 2, 15);  // Jestha 15, 2082
 * FiscalYear fy2 = FiscalYear.of(date2);
 * System.out.println(fy2); // "2081/82" — Jestha 2082 belongs to FY that started Shrawan 2081
 *
 * // Date range for a fiscal year
 * NepaliDateRange range = FiscalYear.parse("2082/83").toDateRange();
 * System.out.println(range.start()); // 2082/04/01
 * System.out.println(range.end());   // 2083/03/32 (or 31 depending on year data)
 * }</pre>
 *
 * <p>This is a Java 21 {@code record} — it is immutable and thread-safe.</p>
 *
 * @param startYear the BS calendar year in which the fiscal year begins (i.e., Shrawan of this year)
 *
 * @see NepaliDate
 * @see NepaliDateRange
 */
public record FiscalYear(int startYear) {

    /**
     * BS month number that marks the start of the fiscal year.
     * Shrawan = month 4.
     */
    public static final int FISCAL_YEAR_START_MONTH = 4; // Shrawan

    // ── Compact constructor ───────────────────────────────────────────────────

    /**
     * Validates that the start year is within the supported BS range.
     *
     * <p>Note: a fiscal year spans two BS calendar years (startYear and startYear+1),
     * so we also ensure startYear+1 is within range so that {@link #endDate()} works.</p>
     *
     * @throws IllegalArgumentException if startYear or startYear+1 is outside supported range
     */
    public FiscalYear {
        if (!NepaliCalendarData.isYearSupported(startYear)) {
            throw new IllegalArgumentException(
                "Fiscal year start " + startYear + " is outside the supported range "
                + "(" + NepaliCalendarData.MIN_YEAR + "–" + NepaliCalendarData.MAX_YEAR + ").");
        }
        if (!NepaliCalendarData.isYearSupported(startYear + 1)) {
            throw new IllegalArgumentException(
                "Fiscal year end " + (startYear + 1) + " is outside the supported range "
                + "(max: " + NepaliCalendarData.MAX_YEAR + ").");
        }
    }

    // ── Static factory methods ────────────────────────────────────────────────

    /**
     * Determines the fiscal year that contains the given BS date.
     *
     * <p>If the date's month is ≥ {@value #FISCAL_YEAR_START_MONTH} (Shrawan),
     * the fiscal year started in the same calendar year. Otherwise it started in
     * the previous calendar year.</p>
     *
     * @param date the BS date whose fiscal year is to be determined; must not be {@code null}
     * @return the {@code FiscalYear} containing {@code date}
     * @throws NullPointerException if {@code date} is {@code null}
     */
    public static FiscalYear of(NepaliDate date) {
        if (date == null) throw new NullPointerException("date must not be null");

        // Shrawan (month 4) through Chaitra (month 12) → FY started in same year
        // Baisakh (month 1) through Ashadh (month 3)   → FY started in previous year
        int fyStartYear = (date.month() >= FISCAL_YEAR_START_MONTH)
                ? date.year()
                : date.year() - 1;

        return new FiscalYear(fyStartYear);
    }

    /**
     * Parses a fiscal year from its standard string representation.
     *
     * <p>Accepted formats:</p>
     * <ul>
     *   <li>{@code "2082/83"} — the canonical form (slash separator)</li>
     *   <li>{@code "2082-83"} — dash separator is also accepted</li>
     *   <li>{@code "2082"} — only the start year (end year is inferred)</li>
     * </ul>
     *
     * <p>Note: only the start year is stored; the end year in {@code "2082/83"}
     * is ignored (it is always {@code startYear + 1}).</p>
     *
     * @param fiscalYearStr the fiscal year string to parse; must not be {@code null}
     * @return the parsed {@code FiscalYear}
     * @throws InvalidNepaliDateException if the string cannot be parsed
     * @throws IllegalArgumentException   if the resulting start year is outside the supported range
     */
    public static FiscalYear parse(String fiscalYearStr) {
        if (fiscalYearStr == null) throw new NullPointerException("fiscalYearStr must not be null");

        String trimmed = fiscalYearStr.trim();
        try {
            // Accept "2082/83", "2082-83", or just "2082"
            String startPart = trimmed.split("[/\\-]")[0].trim();
            return new FiscalYear(Integer.parseInt(startPart));
        } catch (NumberFormatException e) {
            throw new InvalidNepaliDateException(
                "Cannot parse fiscal year string: '" + fiscalYearStr + "'. "
                + "Expected format: '2082/83' or '2082-83' or '2082'.");
        }
    }

    // ── Computed properties ───────────────────────────────────────────────────

    /**
     * Returns the BS calendar year in which the fiscal year ends (always {@code startYear + 1}).
     *
     * @return the end calendar year
     */
    public int endYear() {
        return startYear + 1;
    }

    /**
     * Returns the first day of this fiscal year — Shrawan 1 of {@code startYear}.
     *
     * @return {@link NepaliDate} representing Shrawan 1, startYear
     */
    public NepaliDate startDate() {
        // Fiscal year always starts on Shrawan 1 (month 4, day 1)
        return new NepaliDate(startYear, FISCAL_YEAR_START_MONTH, 1);
    }

    /**
     * Returns the last day of this fiscal year — the final day of Ashadh (month 3)
     * of {@code endYear()}.
     *
     * <p>The last day of Ashadh varies year-to-year and is looked up from
     * {@link NepaliCalendarData}. It is typically 31 or 32.</p>
     *
     * @return {@link NepaliDate} representing the last day of Ashadh in endYear
     */
    public NepaliDate endDate() {
        // Ashadh = month 3; the last day varies by year
        int lastDayOfAshadh = NepaliCalendarData.getDaysInMonth(endYear(), 3);
        return new NepaliDate(endYear(), 3, lastDayOfAshadh);
    }

    /**
     * Returns a {@link NepaliDateRange} spanning the entire fiscal year
     * (from {@link #startDate()} to {@link #endDate()} inclusive).
     *
     * <p>Useful for building date-range queries:</p>
     * <pre>{@code
     * NepaliDateRange range = FiscalYear.parse("2082/83").toDateRange();
     * // Use range.startAD() and range.endAD() in a JPA or SQL query
     * }</pre>
     *
     * @return date range representing the full fiscal year
     */
    public NepaliDateRange toDateRange() {
        return new NepaliDateRange(startDate(), endDate());
    }

    /**
     * Returns {@code true} if the given BS date falls within this fiscal year.
     *
     * @param date the date to test; must not be {@code null}
     * @return {@code true} iff {@code date} is in [{@link #startDate()}, {@link #endDate()}]
     */
    public boolean contains(NepaliDate date) {
        return toDateRange().contains(date);
    }

    // ── Comparison helpers ────────────────────────────────────────────────────

    /**
     * Returns {@code true} if this fiscal year starts before the given fiscal year.
     *
     * @param other the fiscal year to compare to; must not be {@code null}
     * @return {@code true} iff this.startYear &lt; other.startYear
     */
    public boolean isBefore(FiscalYear other) {
        return this.startYear < other.startYear;
    }

    /**
     * Returns {@code true} if this fiscal year starts after the given fiscal year.
     *
     * @param other the fiscal year to compare to; must not be {@code null}
     * @return {@code true} iff this.startYear &gt; other.startYear
     */
    public boolean isAfter(FiscalYear other) {
        return this.startYear > other.startYear;
    }

    // ── Object overrides ──────────────────────────────────────────────────────

    /**
     * Returns the canonical string representation of this fiscal year.
     *
     * <p>Format: {@code "YYYY/YY"} where {@code YY} is the last two digits of
     * {@code startYear + 1}.</p>
     *
     * <p>Examples:</p>
     * <ul>
     *   <li>startYear 2082 → {@code "2082/83"}</li>
     *   <li>startYear 2099 → {@code "2099/00"} (edge case; consider avoiding FY 2099 start)</li>
     * </ul>
     *
     * @return fiscal year string in "YYYY/YY" format
     */
    @Override
    public String toString() {
        // Extract last 2 digits of endYear using modulo 100
        return "%d/%02d".formatted(startYear, endYear() % 100);
    }
}
