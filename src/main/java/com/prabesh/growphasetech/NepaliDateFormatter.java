package com.prabesh.growphasetech;

import java.util.stream.Collectors;

/**
 * Formats a {@link NepaliDate} into human-readable strings according to
 * various conventions used in Nepal — from official government letter headings
 * to compact storage formats.
 *
 * <h2>Available formats</h2>
 * <pre>
 *  Format          Example output (BS 2082/04/15)
 *  ──────────────  ────────────────────────────────────────
 *  FORMAL_NP       २०८२ साल श्रावण १५ गते
 *  FORMAL_EN       15 Shrawan 2082
 *  MEMO_HEADER_NP  मिति: २०८२ साल श्रावण १५ गते
 *  MEMO_HEADER_EN  Date: 15 Shrawan 2082
 *  SHORT_NP        २०८२/०४/१५
 *  SHORT_EN        2082/04/15
 *  ISO_NP          २०८२-०४-१५
 * </pre>
 *
 * <h3>Nepali digits</h3>
 * <p>Several formats use Devanagari digits (०–९) instead of ASCII digits (0–9).
 * The conversion is performed by {@link #toNepaliDigits(int)}, which maps each
 * ASCII digit character to its Unicode Devanagari equivalent.</p>
 *
 * <h3>Thread safety</h3>
 * <p>All methods are static and stateless. This class may be safely called from
 * multiple threads simultaneously.</p>
 *
 * <p>This class is {@code final} with a private constructor — it is a pure
 * static utility and should never be instantiated.</p>
 *
 * @see NepaliDate
 */
public final class NepaliDateFormatter {

    // ── Month name arrays ──────────────────────────────────────────────────────

    /**
     * Nepali (Devanagari script) month names, 0-indexed (index 0 = Baisakh).
     *
     * <p>These are the standard month names used in all government publications.</p>
     */
    static final String[] MONTHS_NP = {
        "बैशाख",    // 1  Baisakh  (mid-Apr – mid-May)
        "जेठ",      // 2  Jestha   (mid-May – mid-Jun)
        "असार",     // 3  Ashadh   (mid-Jun – mid-Jul)
        "श्रावण",   // 4  Shrawan  (mid-Jul – mid-Aug)  ← FY start
        "भाद्र",    // 5  Bhadra   (mid-Aug – mid-Sep)
        "आश्विन",   // 6  Ashwin   (mid-Sep – mid-Oct)
        "कार्तिक",  // 7  Kartik   (mid-Oct – mid-Nov)
        "मंसिर",    // 8  Mangsir  (mid-Nov – mid-Dec)
        "पुष",      // 9  Poush    (mid-Dec – mid-Jan)
        "माघ",      // 10 Magh     (mid-Jan – mid-Feb)
        "फाल्गुन",  // 11 Falgun   (mid-Feb – mid-Mar)
        "चैत्र"     // 12 Chaitra  (mid-Mar – mid-Apr)
    };

    /**
     * English (romanised) month names, 0-indexed (index 0 = Baisakh).
     *
     * <p>These romanisations follow the conventions used in the Government of
     * Nepal's English-language publications and the Nepal Rastra Bank.</p>
     */
    static final String[] MONTHS_EN = {
        "Baisakh",  // 1
        "Jestha",   // 2
        "Ashadh",   // 3
        "Shrawan",  // 4
        "Bhadra",   // 5
        "Ashwin",   // 6
        "Kartik",   // 7
        "Mangsir",  // 8
        "Poush",    // 9
        "Magh",     // 10
        "Falgun",   // 11
        "Chaitra"   // 12
    };

    /**
     * Unicode Devanagari digit characters for '0' through '9'.
     *
     * <p>Unicode block: Devanagari (U+0966 – U+096F).</p>
     * <pre>
     *   ASCII '0' (0x30) → '०' (U+0966)
     *   ASCII '1' (0x31) → '१' (U+0967)
     *   ...
     *   ASCII '9' (0x39) → '९' (U+096F)
     * </pre>
     */
    private static final char[] NEPALI_DIGITS =
            {'०', '१', '२', '३', '४', '५', '६', '७', '८', '९'};

    /** Private constructor — static utility class only. */
    private NepaliDateFormatter() {
        throw new AssertionError("NepaliDateFormatter must not be instantiated.");
    }

    // ── Format enum ───────────────────────────────────────────────────────────

    /**
     * Enumeration of all supported output formats for a {@link NepaliDate}.
     *
     * <p>Pass a value of this enum to {@link NepaliDateFormatter#format(NepaliDate, Format)}
     * to produce the corresponding string.</p>
     */
    public enum Format {

        /**
         * Full formal Nepali format used in most government documents.
         *
         * <p>Example: {@code २०८२ साल श्रावण १५ गते}</p>
         * <p>Literal meaning: "2082 year, Shrawan 15th [date]"</p>
         */
        FORMAL_NP,

        /**
         * Full formal English format.
         *
         * <p>Example: {@code 15 Shrawan 2082}</p>
         */
        FORMAL_EN,

        /**
         * Nepali memo/letter date heading with the prefix {@code मिति:}.
         *
         * <p>{@code मिति} (miti) means "date" in Nepali and is the standard
         * prefix used on official correspondence.</p>
         * <p>Example: {@code मिति: २०८२ साल श्रावण १५ गते}</p>
         */
        MEMO_HEADER_NP,

        /**
         * English memo/letter date heading with the prefix {@code Date:}.
         *
         * <p>Example: {@code Date: 15 Shrawan 2082}</p>
         */
        MEMO_HEADER_EN,

        /**
         * Compact slash-separated format with Nepali (Devanagari) digits.
         *
         * <p>Suitable for display in UI components that need a short date string
         * in Nepali script.</p>
         * <p>Example: {@code २०८२/०४/१५}</p>
         */
        SHORT_NP,

        /**
         * Compact slash-separated format with ASCII digits.
         *
         * <p>Suitable for logging, debugging, and data-storage strings.
         * This is also the format produced by {@link NepaliDate#toString()}.</p>
         * <p>Example: {@code 2082/04/15}</p>
         */
        SHORT_EN,

        /**
         * ISO-8601-like format with hyphen separators and Devanagari digits.
         *
         * <p>Example: {@code २०८२-०४-१५}</p>
         */
        ISO_NP
    }

    // ── Main formatting method ────────────────────────────────────────────────

    /**
     * Formats a {@link NepaliDate} according to the specified {@link Format}.
     *
     * <pre>{@code
     * NepaliDate date = new NepaliDate(2082, 4, 15); // Shrawan 15, 2082
     *
     * NepaliDateFormatter.format(date, Format.FORMAL_NP);
     * // → "२०८२ साल श्रावण १५ गते"
     *
     * NepaliDateFormatter.format(date, Format.MEMO_HEADER_NP);
     * // → "मिति: २०८२ साल श्रावण १५ गते"
     *
     * NepaliDateFormatter.format(date, Format.SHORT_EN);
     * // → "2082/04/15"
     * }</pre>
     *
     * @param date   the BS date to format; must not be {@code null}
     * @param format the desired output format; must not be {@code null}
     * @return the formatted date string in the requested format
     * @throws NullPointerException if either argument is {@code null}
     */
    public static String format(NepaliDate date, Format format) {
        if (date   == null) throw new NullPointerException("date must not be null");
        if (format == null) throw new NullPointerException("format must not be null");

        // Grab commonly reused components up front to avoid repeated method calls
        int    year     = date.year();
        int    month    = date.month();
        int    day      = date.day();
        String monthNp  = MONTHS_NP[month - 1]; // 0-based array, 1-based month
        String monthEn  = MONTHS_EN[month - 1];

        // Java 21 switch expression — exhaustive over all enum constants
        return switch (format) {

            // ── "२०८२ साल श्रावण १५ गते" ──────────────────────────────────
            case FORMAL_NP -> "%s साल %s %s गते".formatted(
                    toNepaliDigits(year),
                    monthNp,
                    toNepaliDigits(day)
            );

            // ── "15 Shrawan 2082" ──────────────────────────────────────────
            case FORMAL_EN -> "%d %s %d".formatted(day, monthEn, year);

            // ── "मिति: २०८२ साल श्रावण १५ गते" ────────────────────────────
            case MEMO_HEADER_NP -> "मिति: %s साल %s %s गते".formatted(
                    toNepaliDigits(year),
                    monthNp,
                    toNepaliDigits(day)
            );

            // ── "Date: 15 Shrawan 2082" ────────────────────────────────────
            case MEMO_HEADER_EN -> "Date: %d %s %d".formatted(day, monthEn, year);

            // ── "२०८२/०४/१५" ───────────────────────────────────────────────
            case SHORT_NP -> "%s/%s/%s".formatted(
                    toNepaliDigits(year),
                    toNepaliDigits(String.format("%02d", month)),   // zero-pad month
                    toNepaliDigits(String.format("%02d", day))      // zero-pad day
            );

            // ── "2082/04/15" ───────────────────────────────────────────────
            case SHORT_EN -> "%d/%02d/%02d".formatted(year, month, day);

            // ── "२०८२-०४-१५" ───────────────────────────────────────────────
            case ISO_NP -> "%s-%s-%s".formatted(
                    toNepaliDigits(year),
                    toNepaliDigits(String.format("%02d", month)),
                    toNepaliDigits(String.format("%02d", day))
            );
        };
    }

    // ── Utility: digit conversion ─────────────────────────────────────────────

    /**
     * Converts an integer to a string of Devanagari (Nepali) digit characters.
     *
     * <p>Each ASCII digit in the decimal representation of {@code number} is
     * replaced by its Devanagari equivalent:</p>
     * <pre>
     *   0 → ०,  1 → १,  2 → २,  3 → ३,  4 → ४
     *   5 → ५,  6 → ६,  7 → ७,  8 → ८,  9 → ९
     * </pre>
     *
     * <p>Examples:</p>
     * <pre>{@code
     * toNepaliDigits(2082)  // "२०८२"
     * toNepaliDigits(4)     // "४"
     * toNepaliDigits(15)    // "१५"
     * }</pre>
     *
     * @param number any non-negative integer
     * @return Devanagari-digit representation of {@code number}
     */
    public static String toNepaliDigits(int number) {
        return toNepaliDigits(String.valueOf(number));
    }

    /**
     * Converts a string of ASCII digits to a string of Devanagari digit characters.
     *
     * <p>Non-digit characters (e.g. '-', '/', ' ') are passed through unchanged,
     * which allows formatted strings like {@code "2082/04"} to be converted to
     * {@code "२०८२/०४"} in one call.</p>
     *
     * @param asciiNumber a string whose digit characters should be converted
     * @return string with ASCII digits replaced by their Devanagari equivalents
     * @throws NullPointerException if {@code asciiNumber} is {@code null}
     */
    public static String toNepaliDigits(String asciiNumber) {
        if (asciiNumber == null) throw new NullPointerException("asciiNumber must not be null");

        // Stream each character; if it's an ASCII digit (0-9) swap for Devanagari,
        // otherwise keep it unchanged (handles separators like '/' and '-').
        return asciiNumber.chars()
                .mapToObj(c -> {
                    if (c >= '0' && c <= '9') {
                        // Subtract ASCII '0' (48) to get 0–9 index into NEPALI_DIGITS
                        return String.valueOf(NEPALI_DIGITS[c - '0']);
                    }
                    return String.valueOf((char) c); // non-digit passthrough
                })
                .collect(Collectors.joining());
    }

    /**
     * Converts a Devanagari digit string back to ASCII digits.
     *
     * <p>This is the inverse of {@link #toNepaliDigits(String)}. Useful when
     * receiving user input in Nepali script that needs to be parsed as a number.</p>
     *
     * <p>Example: {@code "२०८२/०४/१५"} → {@code "2082/04/15"}</p>
     *
     * <p>Non-Devanagari-digit characters are passed through unchanged.</p>
     *
     * @param nepaliNumber string potentially containing Devanagari digits (U+0966–U+096F)
     * @return string with Devanagari digits replaced by ASCII digits
     * @throws NullPointerException if {@code nepaliNumber} is {@code null}
     */
    public static String toAsciiDigits(String nepaliNumber) {
        if (nepaliNumber == null) throw new NullPointerException("nepaliNumber must not be null");

        return nepaliNumber.chars()
                .mapToObj(c -> {
                    // Devanagari digits: ० = U+0966 (decimal 2406) through ९ = U+096F (decimal 2415)
                    if (c >= '\u0966' && c <= '\u096F') {
                        return String.valueOf(c - '\u0966'); // map to 0–9
                    }
                    return String.valueOf((char) c); // non-digit passthrough
                })
                .collect(Collectors.joining());
    }

    /**
     * Returns the Nepali (Devanagari) name for the given month number.
     *
     * @param month month number, 1 (Baisakh) through 12 (Chaitra)
     * @return Devanagari month name
     * @throws IllegalArgumentException if {@code month} is not in 1–12
     */
    public static String monthNameNepali(int month) {
        if (month < 1 || month > 12)
            throw new IllegalArgumentException("Month must be 1–12, got: " + month);
        return MONTHS_NP[month - 1];
    }

    /**
     * Returns the English (romanised) name for the given month number.
     *
     * @param month month number, 1 (Baisakh) through 12 (Chaitra)
     * @return English month name
     * @throws IllegalArgumentException if {@code month} is not in 1–12
     */
    public static String monthNameEnglish(int month) {
        if (month < 1 || month > 12)
            throw new IllegalArgumentException("Month must be 1–12, got: " + month);
        return MONTHS_EN[month - 1];
    }
}
