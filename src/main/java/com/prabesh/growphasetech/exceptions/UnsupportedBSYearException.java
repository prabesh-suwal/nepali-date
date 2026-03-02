package com.prabesh.growphasetech.exceptions;

/**
 * Thrown when a Bikram Sambat year is outside the library's supported range (2000–2099).
 *
 * <p>Because BS date conversion relies on a lookup table (there is no mathematical
 * formula), support is bounded by the data included in {@code NepaliCalendarData}.
 * Extending support to future years requires adding new rows to that table.</p>
 *
 * <pre>{@code
 * // Throws UnsupportedBSYearException:
 * NepaliDate date = new NepaliDate(2100, 1, 1);
 * }</pre>
 */
public class UnsupportedBSYearException extends NepaliDateException {

    /** The year that triggered this exception. */
    private final int year;

    /**
     * Constructs the exception for the given out-of-range BS year.
     *
     * @param year the BS year that is not supported
     */
    public UnsupportedBSYearException(int year) {
        super("BS year %d is outside the supported range (2000–2099). "
              .formatted(year)
              + "To add support, extend NepaliCalendarData with verified data for that year.");
        this.year = year;
    }

    /**
     * Returns the BS year that caused this exception.
     *
     * @return the unsupported BS year
     */
    public int getYear() {
        return year;
    }
}
