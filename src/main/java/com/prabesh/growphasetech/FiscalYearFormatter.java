package com.prabesh.growphasetech;

import java.util.stream.Collectors;

/**
 * Formats a {@link FiscalYear} into human-readable strings according to
 * various conventions used in Nepal — from official government formats
 * to compact storage representations.
 *
 * <h2>Available formats</h2>
 * <pre>
 *  Format          Example output (FY 2082/83)
 *  ──────────────  ────────────────────────────────────────
 *  STANDARD_NP     २०८२/८३
 *  STANDARD_EN     2082/83
 *  FORMAL_NP       श्रावण २०८२ - असार २०८३
 *  FORMAL_EN       Shrawan 2082 - Ashadh 2083
 *  SHORT_NP        २०८२/८३
 *  SHORT_EN        2082/83
 * </pre>
 *
 * <h3>Nepali digits</h3>
 * <p>Several formats use Devanagari digits (०–९) instead of ASCII digits (0–9).
 * The conversion is performed by {@link NepaliDateFormatter#toNepaliDigits(int)},
 * which maps each ASCII digit character to its Unicode Devanagari equivalent.</p>
 *
 * <h3>Thread safety</h3>
 * <p>All methods are static and stateless. This class may be safely called from
 * multiple threads simultaneously.</p>
 *
 * <p>This class is {@code final} with a private constructor — it is a pure
 * static utility and should never be instantiated.</p>
 *
 * @see FiscalYear
 * @see NepaliDateFormatter
 */
public final class FiscalYearFormatter {

    /** Private constructor — static utility class only. */
    private FiscalYearFormatter() {
        throw new AssertionError("FiscalYearFormatter must not be instantiated.");
    }

    // ── Format enum ───────────────────────────────────────────────────────────

    /**
     * Enumeration of all supported output formats for a {@link FiscalYear}.
     *
     * <p>Pass a value of this enum to {@link FiscalYearFormatter#format(FiscalYear, Format)}
     * to produce the corresponding string.</p>
     */
    public enum Format {

        /**
         * Standard format with Nepali (Devanagari) digits in "YYYY/YY" form.
         *
         * <p>Example: {@code २०८२/०३}</p>
         * <p>This is the compact Nepali representation of a fiscal year suitable for
         * display in UI components and reports.</p>
         */
        STANDARD_NP,

        /**
         * Standard format with ASCII digits in "YYYY/YY" form.
         *
         * <p>Example: {@code 2082/83}</p>
         * <p>This is the compact English representation suitable for logging and
         * data storage. This is also the format produced by {@link FiscalYear#toString()}.</p>
         */
        STANDARD_EN,

        /**
         * Full formal Nepali format with month names and date range.
         *
         * <p>Example: {@code श्रावण २०८२ - असार २०८३}</p>
         * <p>This format includes the starting month (Shrawan) and ending month (Ashadh)
         * with Devanagari year numbers. Useful for official government documents.</p>
         */
        FORMAL_NP,

        /**
         * Full formal English format with month names and date range.
         *
         * <p>Example: {@code Shrawan 2082 - Ashadh 2083}</p>
         * <p>This format includes the starting month (Shrawan) and ending month (Ashadh)
         * with English romanisation. Useful for English-language official documents.</p>
         */
        FORMAL_EN
    }

    // ── Main formatting method ────────────────────────────────────────────────

    /**
     * Formats a {@link FiscalYear} according to the specified {@link Format}.
     *
     * <pre>{@code
     * FiscalYear fy = FiscalYear.parse("2082/83");
     *
     * FiscalYearFormatter.format(fy, Format.STANDARD_NP);
     * // → "२०८२/०३"
     *
     * FiscalYearFormatter.format(fy, Format.FORMAL_NP);
     * // → "श्रावण २०८२ - असार २०८३"
     *
     * FiscalYearFormatter.format(fy, Format.STANDARD_EN);
     * // → "2082/83"
     * }</pre>
     *
     * @param fiscalYear the fiscal year to format; must not be {@code null}
     * @param format     the desired output format; must not be {@code null}
     * @return the formatted fiscal year string in the requested format
     * @throws NullPointerException if either argument is {@code null}
     */
    public static String format(FiscalYear fiscalYear, Format format) {
        if (fiscalYear == null) throw new NullPointerException("fiscalYear must not be null");
        if (format == null) throw new NullPointerException("format must not be null");

        int startYear = fiscalYear.startYear();
        int endYear = fiscalYear.endYear();

        // Java 21 switch expression — exhaustive over all enum constants
        return switch (format) {

            // ── "२०८२/८३" ─────────────────────────────────────────────────
            case STANDARD_NP -> {
                String startYearNp = NepaliDateFormatter.toNepaliDigits(startYear);
                String endYearTwoDigits = String.format("%02d", endYear % 100);
                String endYearNp = NepaliDateFormatter.toNepaliDigits(endYearTwoDigits);
                yield "%s/%s".formatted(startYearNp, endYearNp);
            }

            // ── "2082/83" ──────────────────────────────────────────────────
            case STANDARD_EN -> "%d/%02d".formatted(startYear, endYear % 100);

            // ── "श्रावण २०८२ - असार २०८३" ──────────────────────────────
            case FORMAL_NP -> {
                String startMonthNp = NepaliDateFormatter.monthNameNepali(4); // Shrawan
                String startYearNp = NepaliDateFormatter.toNepaliDigits(startYear);
                String endMonthNp = NepaliDateFormatter.monthNameNepali(3); // Ashadh
                String endYearNp = NepaliDateFormatter.toNepaliDigits(endYear);
                yield "%s %s - %s %s".formatted(startMonthNp, startYearNp, endMonthNp, endYearNp);
            }

            // ── "Shrawan 2082 - Ashadh 2083" ───────────────────────────────
            case FORMAL_EN -> {
                String startMonthEn = NepaliDateFormatter.monthNameEnglish(4); // Shrawan
                String endMonthEn = NepaliDateFormatter.monthNameEnglish(3); // Ashadh
                yield "%s %d - %s %d".formatted(startMonthEn, startYear, endMonthEn, endYear);
            }
        };
    }

    /**
     * Formats a {@link FiscalYear} in standard English format (ASCII digits).
     *
     * <p>Convenience method equivalent to
     * {@code format(fiscalYear, Format.STANDARD_EN)}.</p>
     *
     * <p>Example: {@code "2082/83"}</p>
     *
     * @param fiscalYear the fiscal year to format; must not be {@code null}
     * @return fiscal year string in "YYYY/YY" format with ASCII digits
     * @throws NullPointerException if {@code fiscalYear} is {@code null}
     */
    public static String formatStandardEN(FiscalYear fiscalYear) {
        return format(fiscalYear, Format.STANDARD_EN);
    }

    /**
     * Formats a {@link FiscalYear} in standard Nepali format (Devanagari digits).
     *
     * <p>Convenience method equivalent to
     * {@code format(fiscalYear, Format.STANDARD_NP)}.</p>
     *
     * <p>Example: {@code "२०८२/०३"}</p>
     *
     * @param fiscalYear the fiscal year to format; must not be {@code null}
     * @return fiscal year string in "YYYY/YY" format with Devanagari digits
     * @throws NullPointerException if {@code fiscalYear} is {@code null}
     */
    public static String formatStandardNP(FiscalYear fiscalYear) {
        return format(fiscalYear, Format.STANDARD_NP);
    }

    /**
     * Formats a {@link FiscalYear} in formal English format with month names.
     *
     * <p>Convenience method equivalent to
     * {@code format(fiscalYear, Format.FORMAL_EN)}.</p>
     *
     * <p>Example: {@code "Shrawan 2082 - Ashadh 2083"}</p>
     *
     * @param fiscalYear the fiscal year to format; must not be {@code null}
     * @return fiscal year string with English month names and ASCII digits
     * @throws NullPointerException if {@code fiscalYear} is {@code null}
     */
    public static String formatFormalEN(FiscalYear fiscalYear) {
        return format(fiscalYear, Format.FORMAL_EN);
    }

    /**
     * Formats a {@link FiscalYear} in formal Nepali format with month names.
     *
     * <p>Convenience method equivalent to
     * {@code format(fiscalYear, Format.FORMAL_NP)}.</p>
     *
     * <p>Example: {@code "श्रावण २०८२ - असार २०८३"}</p>
     *
     * @param fiscalYear the fiscal year to format; must not be {@code null}
     * @return fiscal year string with Nepali month names and Devanagari digits
     * @throws NullPointerException if {@code fiscalYear} is {@code null}
     */
    public static String formatFormalNP(FiscalYear fiscalYear) {
        return format(fiscalYear, Format.FORMAL_NP);
    }
}

