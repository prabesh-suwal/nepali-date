/**
 * <h2>Nepali Date Library — Bikram Sambat (BS) for Java 21</h2>
 *
 * <p>This library provides complete support for the <strong>Bikram Sambat (BS)</strong>
 * calendar system used officially in Nepal. It covers BS years
 * <strong>2000 – 2099</strong> and offers:</p>
 *
 * <ul>
 *   <li>Immutable {@link com.prabesh.growphasetech.NepaliDate} type (Java record)</li>
 *   <li>Accurate AD ↔ BS conversion via {@link com.prabesh.growphasetech.NepaliDateConverter}</li>
 *   <li>Nepali fiscal year (आर्थिक वर्ष) support via {@link com.prabesh.growphasetech.FiscalYear}</li>
 *   <li>Date-range queries via {@link com.prabesh.growphasetech.NepaliDateRange}</li>
 *   <li>Government letter formatting (Nepali &amp; English) via
 *       {@link com.prabesh.growphasetech.NepaliDateFormatter}</li>
 * </ul>
 *
 * <h3>Quick Start</h3>
 * <pre>{@code
 * // Today in Bikram Sambat
 * NepaliDate today = NepaliDate.today();
 *
 * // Convert AD → BS
 * NepaliDate bsDate = NepaliDate.fromAD(LocalDate.of(2025, 3, 15));
 *
 * // Official memo heading
 * String heading = NepaliDateFormatter.format(bsDate, Format.MEMO_HEADER_NP);
 * // → "मिति: २०८१ साल फाल्गुन ०२ गते"
 *
 * // Current fiscal year
 * FiscalYear fy = FiscalYear.of(today);
 * System.out.println(fy); // → "2081/82"
 *
 * // Search memos in fiscal year range
 * NepaliDateRange range = fy.toDateRange();
 * }</pre>
 *
 * <h3>Design Principles</h3>
 * <ul>
 *   <li><strong>Zero dependencies</strong> — pure Java 21, no third-party runtime libs</li>
 *   <li><strong>Immutable</strong> — all core types are records or final classes</li>
 *   <li><strong>Lookup-table based</strong> — BS has no mathematical formula;
 *       accurate day counts come from a verified reference table</li>
 *   <li><strong>Thread-safe</strong> — stateless converters and immutable data</li>
 * </ul>
 *
 * <h3>Bikram Sambat Background</h3>
 * <p>The Bikram Sambat calendar is approximately 56 years and 8.5 months ahead of
 * the Gregorian (AD) calendar. It is the <em>official</em> calendar of the Government
 * of Nepal. Unlike AD, BS months do not follow a fixed pattern — the number of days
 * in each month varies year to year and must be looked up from authoritative tables.</p>
 *
 * <p>Nepal's fiscal year runs from <strong>Shrawan 1</strong> (≈ mid-July AD) to
 * <strong>Ashad end</strong> (≈ mid-July AD next year), and is expressed as "2082/83".</p>
 *
 * @author  nepali-date contributors
 * @version 1.0.0
 * @see     <a href="https://github.com/nepalidate/nepali-date">GitHub Repository</a>
 */
package com.prabesh.growphasetech;
