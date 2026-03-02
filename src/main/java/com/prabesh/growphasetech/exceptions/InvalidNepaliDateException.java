package com.prabesh.growphasetech.exceptions;

/**
 * Thrown when the combination of BS year, month, and day does not represent
 * a valid date in the Bikram Sambat calendar.
 *
 * <p>Common causes:</p>
 * <ul>
 *   <li>Month is outside the range 1–12</li>
 *   <li>Day exceeds the actual number of days in that BS month
 *       (which varies per year — e.g. Baisakh 2082 has 31 days,
 *       but not all months are equal)</li>
 *   <li>Malformed date string passed to {@code NepaliDate.parse()}</li>
 * </ul>
 *
 * <pre>{@code
 * // Throws InvalidNepaliDateException — Baisakh 2082 has only 31 days:
 * NepaliDate date = new NepaliDate(2082, 1, 35);
 * }</pre>
 */
public class InvalidNepaliDateException extends NepaliDateException {

    /**
     * Constructs the exception with a descriptive message.
     *
     * @param message human-readable explanation of what is invalid
     */
    public InvalidNepaliDateException(String message) {
        super(message);
    }

    /**
     * Constructs the exception from a BS year/month/day triple and the maximum
     * allowed day for that month, producing a clear diagnostic message.
     *
     * @param year   BS year provided
     * @param month  BS month provided (1–12)
     * @param day    day value that was found to be invalid
     * @param maxDay the maximum valid day for the given year and month
     */
    public InvalidNepaliDateException(int year, int month, int day, int maxDay) {
        super(("Invalid BS date %d/%02d/%02d — %d/%02d has only %d day(s). "
               + "Note: day counts vary year-to-year in Bikram Sambat.")
               .formatted(year, month, day, year, month, maxDay));
    }
}
