package com.prabesh.growphasetech;

import com.prabesh.growphasetech.exceptions.InvalidNepaliDateException;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * An immutable, closed date range in the Bikram Sambat (BS) calendar — from
 * {@link #start()} to {@link #end()}, <strong>inclusive</strong> on both ends.
 *
 * <h2>Use cases</h2>
 * <ul>
 *   <li>Represent a fiscal year period (see {@link FiscalYear#toDateRange()}).</li>
 *   <li>Represent a quarterly or monthly reporting window.</li>
 *   <li>Filter lists of domain objects whose dates fall within the range.</li>
 *   <li>Convert to an AD date range for use in JPA/SQL queries.</li>
 * </ul>
 *
 * <h3>Database integration pattern</h3>
 * <p>Store dates as {@code DATE} columns in AD (for reliable indexing and sorting)
 * plus BS year/month/day columns for display and Nepali-script filtering.
 * Use {@link #startAD()} and {@link #endAD()} to build the BETWEEN clause:</p>
 *
 * <pre>{@code
 * NepaliDateRange range = FiscalYear.parse("2082/83").toDateRange();
 *
 * // Spring Data JPA
 * List<Memo> memos = memoRepository.findByDateAdBetween(range.startAD(), range.endAD());
 *
 * // JPQL with @Query
 * // "SELECT m FROM Memo m WHERE m.dateAd BETWEEN :start AND :end"
 * }</pre>
 *
 * <h3>In-memory filtering</h3>
 * <pre>{@code
 * NepaliDateRange q1 = new NepaliDateRange(
 *     new NepaliDate(2082, 4, 1),
 *     new NepaliDate(2082, 6, 30)
 * );
 *
 * List<Memo> q1Memos = q1.filter(allMemos, memo -> memo.getBsDate());
 * }</pre>
 *
 * <p>This is a Java 21 {@code record} — it is immutable and thread-safe.</p>
 *
 * @param start the first date of the range (inclusive); must not be {@code null}
 * @param end   the last date of the range (inclusive); must not be {@code null}
 *
 * @see NepaliDate
 * @see FiscalYear
 */
public record NepaliDateRange(NepaliDate start, NepaliDate end) {

    // ── Compact constructor ───────────────────────────────────────────────────

    /**
     * Validates that start ≤ end (a degenerate range of a single day is allowed).
     *
     * @throws NullPointerException       if {@code start} or {@code end} is {@code null}
     * @throws InvalidNepaliDateException if {@code start} is after {@code end}
     */
    public NepaliDateRange {
        if (start == null) throw new NullPointerException("start must not be null");
        if (end   == null) throw new NullPointerException("end must not be null");

        if (start.isAfter(end)) {
            throw new InvalidNepaliDateException(
                "Range start (%s) must not be after range end (%s).".formatted(start, end));
        }
    }

    // ── Static factory methods ────────────────────────────────────────────────

    /**
     * Creates a range covering an entire BS month.
     *
     * <p>The end date is automatically set to the last valid day of the month
     * (looked up from {@link NepaliCalendarData} since it varies by year).</p>
     *
     * @param bsYear  the BS year
     * @param bsMonth the BS month (1–12)
     * @return range from day 1 to the last day of the given year-month
     */
    public static NepaliDateRange ofMonth(int bsYear, int bsMonth) {
        int lastDay = NepaliCalendarData.getDaysInMonth(bsYear, bsMonth);
        return new NepaliDateRange(
            new NepaliDate(bsYear, bsMonth, 1),
            new NepaliDate(bsYear, bsMonth, lastDay)
        );
    }

    /**
     * Creates a range covering an entire BS year (Baisakh 1 to Chaitra end).
     *
     * @param bsYear the BS year
     * @return range from Baisakh 1 to the last day of Chaitra in the given year
     */
    public static NepaliDateRange ofYear(int bsYear) {
        int lastDayChaitra = NepaliCalendarData.getDaysInMonth(bsYear, 12);
        return new NepaliDateRange(
            new NepaliDate(bsYear, 1, 1),
            new NepaliDate(bsYear, 12, lastDayChaitra)
        );
    }

    // ── Containment checks ────────────────────────────────────────────────────

    /**
     * Returns {@code true} if the given BS date falls within this range (inclusive).
     *
     * @param date the date to test; must not be {@code null}
     * @return {@code true} iff {@code start ≤ date ≤ end}
     * @throws NullPointerException if {@code date} is {@code null}
     */
    public boolean contains(NepaliDate date) {
        if (date == null) throw new NullPointerException("date must not be null");
        return date.isBetween(start, end);
    }

    /**
     * Returns {@code true} if the given Gregorian (AD) date falls within this
     * range after conversion to BS.
     *
     * <p>This is convenient when you receive AD dates from external systems but
     * want to check them against a BS range.</p>
     *
     * @param adDate the Gregorian date to test; must not be {@code null}
     * @return {@code true} iff the converted BS date is within this range
     * @throws NullPointerException if {@code adDate} is {@code null}
     */
    public boolean contains(LocalDate adDate) {
        if (adDate == null) throw new NullPointerException("adDate must not be null");
        return contains(NepaliDate.fromAD(adDate));
    }

    /**
     * Returns {@code true} if this range completely contains the other range
     * (i.e., {@code other.start ≥ this.start} AND {@code other.end ≤ this.end}).
     *
     * @param other the range to test; must not be {@code null}
     * @return {@code true} iff the other range is fully enclosed by this range
     */
    public boolean containsRange(NepaliDateRange other) {
        if (other == null) throw new NullPointerException("other must not be null");
        return !other.start().isBefore(this.start) && !other.end().isAfter(this.end);
    }

    /**
     * Returns {@code true} if this range and the other range overlap (share at
     * least one day).
     *
     * @param other the range to test; must not be {@code null}
     * @return {@code true} iff the ranges overlap
     */
    public boolean overlaps(NepaliDateRange other) {
        if (other == null) throw new NullPointerException("other must not be null");
        // Two ranges overlap unless one ends before the other starts
        return !this.end.isBefore(other.start()) && !other.end().isBefore(this.start);
    }

    // ── AD conversion (for database queries) ─────────────────────────────────

    /**
     * Returns the Gregorian (AD) equivalent of the range's start date.
     *
     * <p>Use this as the lower bound of a {@code BETWEEN} clause in SQL or JPQL
     * to take advantage of native date indexing in the database:</p>
     *
     * <pre>{@code
     * // JPQL:
     * // SELECT m FROM Memo m WHERE m.dateAd BETWEEN :start AND :end
     * query.setParameter("start", range.startAD());
     * query.setParameter("end",   range.endAD());
     * }</pre>
     *
     * @return the AD equivalent of {@link #start()}
     */
    public LocalDate startAD() {
        return start.toAD();
    }

    /**
     * Returns the Gregorian (AD) equivalent of the range's end date.
     *
     * @return the AD equivalent of {@link #end()}
     */
    public LocalDate endAD() {
        return end.toAD();
    }

    // ── Filtering helpers ─────────────────────────────────────────────────────

    /**
     * Filters a list of objects, keeping only those whose extracted BS date
     * falls within this range.
     *
     * <p>The {@code dateExtractor} function is applied to each element to obtain
     * its {@link NepaliDate}. Elements for which the date is within the range
     * are included in the returned list (which preserves original order).</p>
     *
     * <pre>{@code
     * NepaliDateRange q1 = new NepaliDateRange(
     *     new NepaliDate(2082, 4, 1),
     *     new NepaliDate(2082, 6, 30)
     * );
     * List<Memo> q1Memos = q1.filter(allMemos, Memo::getBsDate);
     * }</pre>
     *
     * @param <T>           the type of elements in the list
     * @param items         the list to filter; must not be {@code null}
     * @param dateExtractor function to extract a {@link NepaliDate} from each element;
     *                      must not return {@code null}
     * @return a new list containing only the matching elements (in original order)
     */
    public <T> List<T> filter(List<T> items, Function<T, NepaliDate> dateExtractor) {
        if (items         == null) throw new NullPointerException("items must not be null");
        if (dateExtractor == null) throw new NullPointerException("dateExtractor must not be null");

        return items.stream()
                .filter(item -> contains(dateExtractor.apply(item)))
                .collect(Collectors.toList());
    }

    /**
     * Filters a list of objects using their Gregorian (AD) date.
     *
     * <p>Useful when objects store dates in AD format and you want to filter
     * them against a BS range without converting each item individually:</p>
     *
     * <pre>{@code
     * List<Memo> memos = range.filterByAD(allMemos, memo -> memo.getDateAd());
     * }</pre>
     *
     * @param <T>           the type of elements in the list
     * @param items         the list to filter; must not be {@code null}
     * @param dateExtractor function to extract a {@link LocalDate} (AD) from each element
     * @return a new list containing only elements whose AD date converts to a BS date
     *         within this range (in original order)
     */
    public <T> List<T> filterByAD(List<T> items, Function<T, LocalDate> dateExtractor) {
        if (items         == null) throw new NullPointerException("items must not be null");
        if (dateExtractor == null) throw new NullPointerException("dateExtractor must not be null");

        return items.stream()
                .filter(item -> contains(dateExtractor.apply(item)))
                .collect(Collectors.toList());
    }

    // ── Utility ───────────────────────────────────────────────────────────────

    /**
     * Returns the total number of days in this range (inclusive on both ends).
     *
     * <p>A range of a single day returns 1.</p>
     *
     * @return number of days from start to end (inclusive)
     */
    public long totalDays() {
        // Convert both ends to AD and use epoch-day arithmetic for simplicity
        return endAD().toEpochDay() - startAD().toEpochDay() + 1;
    }

    // ── Object overrides ──────────────────────────────────────────────────────

    /**
     * Returns a human-readable representation of this range.
     *
     * <p>Format: {@code "start → end"}, e.g. {@code "2082/04/01 → 2083/03/31"}.</p>
     *
     * @return string representation of the date range
     */
    @Override
    public String toString() {
        return "%s → %s".formatted(start, end);
    }
}
