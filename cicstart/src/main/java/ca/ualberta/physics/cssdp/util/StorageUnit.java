/* ============================================================
 * StorageUnit.java
 * ============================================================
 * Copyright 2013 University of Alberta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============================================================ 
 */
package ca.ualberta.physics.cssdp.util;

/**
 * This was taken directly from
 * https://groups.google.com/group/comp.lang.java.help
 * /browse_thread/thread/0db818517ca9de79/b0a55aa19f911204?pli=1 that thread on
 * google groups.
 */
public enum StorageUnit {
	
	BYTE("B", 1L), KILOBYTE("KB", 1L << 10), MEGABYTE("MB", 1L << 20), GIGABYTE(
			"GB", 1L << 30), TERABYTE("TB", 1L << 40), PETABYTE("PB", 1L << 50), EXABYTE(
			"EB", 1L << 60);
	
	public static final StorageUnit BASE = BYTE;
	private final String symbol;
	private final long divider; // divider of BASE unit

	StorageUnit(String name, long divider) {
		this.symbol = name;
		this.divider = divider;
	}

	public static StorageUnit of(final long number) {
		final long n = number > 0 ? -number : number;
		if (n > -(1L << 10)) {
			return BYTE;
		} else if (n > -(1L << 20)) {
			return KILOBYTE;
		} else if (n > -(1L << 30)) {
			return MEGABYTE;
		} else if (n > -(1L << 40)) {
			return GIGABYTE;
		} else if (n > -(1L << 50)) {
			return TERABYTE;
		} else if (n > -(1L << 60)) {
			return PETABYTE;
		} else { // n >= Long.MIN_VALUE
			return EXABYTE;
		}
	}

	public String format(long number) {
		return nf.format((double) number / divider) + " " + symbol;
	}

	private static java.text.NumberFormat nf = java.text.NumberFormat
			.getInstance();
	static {
		nf.setGroupingUsed(false);
		nf.setMinimumFractionDigits(0);
		nf.setMaximumFractionDigits(1);
	}
}
