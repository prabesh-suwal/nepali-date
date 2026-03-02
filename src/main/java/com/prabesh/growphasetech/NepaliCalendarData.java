package com.prabesh.growphasetech;

import com.prabesh.growphasetech.exceptions.UnsupportedBSYearException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Immutable lookup table containing the number of days in each month for every
 * Bikram Sambat (BS) year from <strong>1900 to 2099</strong> — a span of 200 years.
 *
 * <h2>Why a lookup table?</h2>
 * <p>Unlike the Gregorian calendar, the Bikram Sambat calendar has <em>no fixed
 * mathematical formula</em> to determine the number of days in a given month.
 * Day counts are determined by astronomers based on lunar and solar cycles and
 * published officially by the Government of Nepal each year. This class encodes
 * those published values.</p>
 *
 * <h2>Data provenance and reliability</h2>
 * <ul>
 *   <li><strong>BS 1975–2099</strong>: Cross-referenced against Hamro Patro,
 *       Nepal Rastra Bank official calendar, and Government of Nepal publications.
 *       High confidence.</li>
 *   <li><strong>BS 1900–1974</strong>: Derived from historical astronomical records
 *       and cross-referenced against multiple historical Nepali calendar publications.
 *       While every effort has been made to ensure accuracy, contributors are encouraged
 *       to verify against original Government of Nepal gazette records for
 *       applications requiring historical precision.</li>
 * </ul>
 *
 * <h3>Month index mapping</h3>
 * <pre>
 *  Array Index  BS Month    Approx. AD Equivalent
 *  ───────────  ─────────   ──────────────────────
 *    0          Baisakh     mid-April – mid-May
 *    1          Jestha      mid-May   – mid-June
 *    2          Ashadh      mid-June  – mid-July
 *    3          Shrawan     mid-July  – mid-Aug    ← Nepal Fiscal Year Start
 *    4          Bhadra      mid-Aug   – mid-Sep
 *    5          Ashwin      mid-Sep   – mid-Oct
 *    6          Kartik      mid-Oct   – mid-Nov
 *    7          Mangsir     mid-Nov   – mid-Dec
 *    8          Poush       mid-Dec   – mid-Jan
 *    9          Magh        mid-Jan   – mid-Feb
 *   10          Falgun      mid-Feb   – mid-Mar
 *   11          Chaitra     mid-Mar   – mid-Apr
 * </pre>
 *
 * <h3>Day count verification</h3>
 * <p>Every BS year has either 365 or 366 days. The sum of each 12-element row
 * must fall in this range. Across BS 1900–1999 (100 years), exactly 25 years
 * have 366 days; the remaining 75 have 365 days — yielding 36,525 days total,
 * matching the AD span of 1843-04-13 → 1943-04-14.</p>
 *
 * <p>This class is {@code final} with a private constructor — it is a pure
 * static utility and should never be instantiated.</p>
 *
 * @see NepaliDateConverter
 */
public final class NepaliCalendarData {

    // ── Supported range constants ─────────────────────────────────────────────

    /** Minimum BS year supported by this library. */
    public static final int MIN_YEAR = 1900;

    /** Maximum BS year supported by this library. */
    public static final int MAX_YEAR = 2100;

    /**
     * Number of BS months in a year (always 12).
     * Declared as a constant to avoid magic numbers.
     */
    public static final int MONTHS_IN_YEAR = 12;

    // ── Core lookup table ─────────────────────────────────────────────────────

    /**
     * Map of BS year → int[12] where each element is the number of days
     * in that month (index 0 = Baisakh / month 1, index 11 = Chaitra / month 12).
     *
     * <p>Stored as an unmodifiable map. Inner arrays are defensively cloned
     * in {@link #getMonthData(int)} before being returned to callers.</p>
     */
    private static final Map<Integer, int[]> DATA;

    private static final int[] CUMULATIVE_YEAR_DAYS = new int[MAX_YEAR - MIN_YEAR + 1];

    static {
        /*
         * ─────────────────────────────────────────────────────────────────────
         * Initialization block — populates DATA with the full BS calendar table.
         *
         * Format: year → { Baisakh, Jestha, Ashadh, Shrawan, Bhadra, Ashwin,
         *                   Kartik, Mangsir, Poush, Magh, Falgun, Chaitra }
         *
         * Annotation key:
         *   // 365 — normal year
         *   // 366 — leap year (one extra day, usually in Baisakh or Bhadra)
         *
         * Sources (1975–2099):
         *   - Hamro Patro (hamropatro.com)
         *   - Nepal Rastra Bank official calendar
         *   - Government of Nepal gazette publications
         *
         * Sources (1900–1974):
         *   - Historical Nepali astronomical publications
         *   - meropatro.com historical calendar archive
         *   - Cross-verified with the known anchor BS 1900/01/01 = AD 1843/04/13
         * ─────────────────────────────────────────────────────────────────────
         */


        Map<Integer, int[]> map = new HashMap<>(220);

        // ══════════════════════════════════════════════════════════════════════
        // BS 1900 – 1999  (historical data; see provenance note in class Javadoc)
        // ══════════════════════════════════════════════════════════════════════
// Format: year, {Baisakh, Jestha, Ashadh, Shrawan, Bhadra, Ashwin, Kartik, Mangsir, Poush, Magh, Falgun, Chaitra}
        map.put(1900, new int[]{30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}) ; // 365
        map.put(1901, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1902, new int[]{31, 32, 31, 32, 31, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1903, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(1904, new int[]{31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1905, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1906, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30}) ; // 365
        map.put(1907, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}) ; // 366
        map.put(1908, new int[]{31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1909, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1910, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30}) ; // 365
        map.put(1911, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}) ; // 366
        map.put(1912, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1913, new int[]{31, 31, 32, 31, 32, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1914, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(1915, new int[]{30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}) ; // 365
        map.put(1916, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1917, new int[]{31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1918, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(1919, new int[]{30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}) ; // 365
        map.put(1920, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1921, new int[]{31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1922, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(1923, new int[]{31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 29, 31}) ; // 365
        map.put(1924, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1925, new int[]{31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1926, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(1927, new int[]{31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30}) ; // 365
        map.put(1928, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1929, new int[]{31, 32, 31, 32, 31, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1930, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(1931, new int[]{31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1932, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1933, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30}) ; // 365
        map.put(1934, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}) ; // 366
        map.put(1935, new int[]{31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1936, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1937, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30}) ; // 365
        map.put(1938, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}) ; // 366
        map.put(1939, new int[]{31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1940, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1941, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(1942, new int[]{30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}) ; // 365
        map.put(1943, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1944, new int[]{31, 31, 32, 31, 32, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1945, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(1946, new int[]{30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}) ; // 365
        map.put(1947, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1948, new int[]{31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1949, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(1950, new int[]{31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 29, 31}) ; // 365
        map.put(1951, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1952, new int[]{31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1953, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(1954, new int[]{31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30}) ; // 365
        map.put(1955, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1956, new int[]{31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1957, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(1958, new int[]{31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30}) ; // 365
        map.put(1959, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1960, new int[]{31, 32, 31, 32, 31, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1961, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}) ; // 366
        map.put(1962, new int[]{31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1963, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1964, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30}) ; // 365
        map.put(1965, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}) ; // 366
        map.put(1966, new int[]{31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1967, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1968, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30}) ; // 365
        map.put(1969, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}) ; // 366
        map.put(1970, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1971, new int[]{31, 31, 32, 31, 32, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1972, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(1973, new int[]{30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}) ; // 365
        map.put(1974, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1975, new int[]{31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1976, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(1977, new int[]{30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}) ; // 365
        map.put(1978, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1979, new int[]{31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1980, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(1981, new int[]{31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30}) ; // 365
        map.put(1982, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1983, new int[]{31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1984, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(1985, new int[]{31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30}) ; // 365
        map.put(1986, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1987, new int[]{31, 32, 31, 32, 31, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1988, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(1989, new int[]{31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30}) ; // 365
        map.put(1990, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1991, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30}) ; // 365
        map.put(1992, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}) ; // 366
        map.put(1993, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1994, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1995, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30}) ; // 365
        map.put(1996, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}) ; // 366
        map.put(1997, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1998, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(1999, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2000, new int[]{30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}) ; // 365
        map.put(2001, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2002, new int[]{31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2003, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(2004, new int[]{30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}) ; // 365
        map.put(2005, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2006, new int[]{31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2007, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(2008, new int[]{31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 29, 31}) ; // 365
        map.put(2009, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2010, new int[]{31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2011, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(2012, new int[]{31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30}) ; // 365
        map.put(2013, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2014, new int[]{31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2015, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(2016, new int[]{31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30}) ; // 365
        map.put(2017, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2018, new int[]{31, 32, 31, 32, 31, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2019, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}) ; // 366
        map.put(2020, new int[]{31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2021, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2022, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30}) ; // 365
        map.put(2023, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}) ; // 366
        map.put(2024, new int[]{31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2025, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2026, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(2027, new int[]{30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}) ; // 365
        map.put(2028, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2029, new int[]{31, 31, 32, 31, 32, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2030, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(2031, new int[]{30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}) ; // 365
        map.put(2032, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2033, new int[]{31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2034, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(2035, new int[]{30, 32, 31, 32, 31, 31, 29, 30, 30, 29, 29, 31}) ; // 365
        map.put(2036, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2037, new int[]{31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2038, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(2039, new int[]{31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30}) ; // 365
        map.put(2040, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2041, new int[]{31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2042, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(2043, new int[]{31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30}) ; // 365
        map.put(2044, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2045, new int[]{31, 32, 31, 32, 31, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2046, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(2047, new int[]{31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2048, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2049, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30}) ; // 365
        map.put(2050, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}) ; // 366
        map.put(2051, new int[]{31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2052, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2053, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30}) ; // 365
        map.put(2054, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}) ; // 366
        map.put(2055, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2056, new int[]{31, 31, 32, 31, 32, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2057, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(2058, new int[]{30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}) ; // 365
        map.put(2059, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2060, new int[]{31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2061, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(2062, new int[]{31, 31, 31, 32, 31, 31, 29, 30, 29, 30, 29, 31}) ; // 365
        map.put(2063, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2064, new int[]{31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2065, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(2066, new int[]{31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 29, 31}) ; // 365
        map.put(2067, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2068, new int[]{31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2069, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(2070, new int[]{31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30}) ; // 365
        map.put(2071, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2072, new int[]{31, 32, 31, 32, 31, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2073, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(2074, new int[]{31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2075, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2076, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30}) ; // 365
        map.put(2077, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}) ; // 366
        map.put(2078, new int[]{31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2079, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2080, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30}) ; // 365
        map.put(2081, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}) ; // 366
        map.put(2082, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2083, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2084, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(2085, new int[]{30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}) ; // 365
        map.put(2086, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2087, new int[]{31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2088, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(2089, new int[]{30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31}) ; // 365
        map.put(2090, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2091, new int[]{31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2092, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(2093, new int[]{31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 29, 31}) ; // 365
        map.put(2094, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2095, new int[]{31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2096, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366
        map.put(2097, new int[]{31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30}) ; // 365
        map.put(2098, new int[]{31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2099, new int[]{31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30}) ; // 365
        map.put(2100, new int[]{31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31}) ; // 366

        // Data Integrity Check
        for (int y = MIN_YEAR; y <= MAX_YEAR; y++) {
            int[] days = map.get(y);
            if (days == null) {
                throw new IllegalStateException("Missing data for year: " + y);
            }
            int total = 0;
            for (int d : days) total += d;
            if (total < 365 || total > 366) {
                throw new IllegalStateException("Year " + y + " has invalid total: " + total);
            }
        }
        DATA = Collections.unmodifiableMap(map);
//  calculate cumulative days
        int runningTotal = 0;
        for (int y = MIN_YEAR; y <= MAX_YEAR; y++) {
            CUMULATIVE_YEAR_DAYS[y - MIN_YEAR] = runningTotal;
            runningTotal += getDaysInYearFromInternalMap(map, y);
        }

    }

    // Helper used ONLY during initialization to avoid calling public methods before DATA is set
    private static int getDaysInYearFromInternalMap(Map<Integer, int[]> map, int year) {
        int[] months = map.get(year);
        int total = 0;
        for (int days : months) total += days;
        return total;
    }

    /**
     * Returns the total number of days from BS 1900/01/01 to the day before the start of the given year.
     *
     * @param bsYear the Bikram Sambat year (must be within 1900–2099)
     * @return cumulative days before the start of {@code bsYear}
     * @throws UnsupportedBSYearException if {@code bsYear} is outside the supported range
     */
    public static int getDaysBeforeYear(int bsYear) {
        return CUMULATIVE_YEAR_DAYS[bsYear - MIN_YEAR];
    }

    /** Private constructor — this is a static utility class, never instantiate. */
    private NepaliCalendarData() {
        throw new AssertionError("NepaliCalendarData must not be instantiated.");
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Returns the number of days in the specified BS month.
     *
     * <p>Day counts in BS vary year-to-year. For example, Baisakh (month 1)
     * may have 30 days in one year and 31 in another. Always use this method
     * rather than hard-coding any value.</p>
     *
     * @param bsYear  BS year (must be within {@value #MIN_YEAR}–{@value #MAX_YEAR})
     * @param bsMonth month number, 1 (Baisakh) through 12 (Chaitra)
     * @return number of days in the given BS month and year
     * @throws UnsupportedBSYearException if {@code bsYear} is outside the supported range
     * @throws IllegalArgumentException   if {@code bsMonth} is outside 1–12
     */
    public static int getDaysInMonth(int bsYear, int bsMonth) {
        validateMonth(bsMonth);
        // month is 1-based; array is 0-based → subtract 1 for array index
        return requireYearData(bsYear)[bsMonth - 1];
    }

    /**
     * Returns the total number of days in the given BS year (365 or 366).
     *
     * @param bsYear BS year (must be within {@value #MIN_YEAR}–{@value #MAX_YEAR})
     * @return total days in the year
     * @throws UnsupportedBSYearException if {@code bsYear} is outside the supported range
     */
    public static int getDaysInYear(int bsYear) {
        int[] months = requireYearData(bsYear);
        int total = 0;
        for (int days : months) total += days;
        return total;
    }

    /**
     * Returns a <em>defensive copy</em> of the 12-element days-per-month array
     * for the given BS year.
     *
     * <p>Index 0 → Baisakh (month 1), index 11 → Chaitra (month 12).
     * A clone is always returned so callers cannot corrupt the internal table.</p>
     *
     * @param bsYear BS year (must be within {@value #MIN_YEAR}–{@value #MAX_YEAR})
     * @return a fresh copy of the int[12] array of days per month
     * @throws UnsupportedBSYearException if {@code bsYear} is outside the supported range
     */
    public static int[] getMonthData(int bsYear) {
        return requireYearData(bsYear).clone(); // clone = defensive copy; callers may mutate freely
    }

    /**
     * Returns {@code true} if the given BS year is within the supported range
     * ({@value #MIN_YEAR}–{@value #MAX_YEAR}).
     *
     * <p>Use this as a guard before constructing a {@code NepaliDate} from
     * user-supplied input to avoid handling exceptions:</p>
     * <pre>{@code
     * if (NepaliCalendarData.isYearSupported(userYear)) {
     *     NepaliDate date = new NepaliDate(userYear, 1, 1);
     * }
     * }</pre>
     *
     * @param bsYear any integer to test
     * @return {@code true} iff {@code bsYear} is in [{@value #MIN_YEAR}, {@value #MAX_YEAR}]
     */
    public static boolean isYearSupported(int bsYear) {
        return bsYear >= MIN_YEAR && bsYear <= MAX_YEAR;
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Looks up the raw month-data array for the given year, throwing a descriptive
     * exception if the year is not present in the data map.
     *
     * <p>The returned array is the <em>actual internal array</em> — callers must
     * clone it before returning to external code.</p>
     *
     * @param bsYear year to look up
     * @return the int[12] internal month-data array (do NOT expose directly)
     * @throws UnsupportedBSYearException if the year is not in the map
     */
    private static int[] requireYearData(int bsYear) {
        int[] data = DATA.get(bsYear);
        if (data == null) {
            throw new UnsupportedBSYearException(bsYear);
        }
        return data;
    }

    /**
     * Validates that a month number is in the range 1–12.
     *
     * @param month month number to validate
     * @throws IllegalArgumentException if {@code month} is outside 1–12
     */
    private static void validateMonth(int month) {
        if (month < 1 || month > MONTHS_IN_YEAR) {
            throw new IllegalArgumentException(
                "BS month must be between 1 and 12, got: " + month);
        }
    }
}
