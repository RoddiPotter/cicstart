/* ============================================================
 * CollectionPrinter.java
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

import java.util.Arrays;
import java.util.Collection;

/*
 * We had to put this class in the model project because we use it 
 * for formatting various collections on model objects.  We would 
 * have liked to keep it in core, but we unable.  One solution, if 
 * needed would be to create a 'util' project that has these globally 
 * shared objects ... like a static.
 */
/**
 * Nicely format a collection of items with a comma or your delimiter of choice.
 */
public abstract class CollectionPrinter<T> {

	private final String formattedList;

	public CollectionPrinter(Collection<T> list) {
		this(list, ", ");
	}

	public CollectionPrinter(Collection<T> list, String delimiter) {

		if (delimiter == null) {
			delimiter = ",";
		}
		delimiter = delimiter.trim();

		StringBuffer sb = new StringBuffer();
		for (T t : list) {
			if (t != null) {
				sb.append(format(t));
				sb.append(delimiter);
			}
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		formattedList = sb.toString();
	}

	public CollectionPrinter(T... values) {
		this(Arrays.asList(values));
	}

	public CollectionPrinter(String delimiter, T... values) {
		this(Arrays.asList(values), delimiter);
	}

	protected abstract String format(T t);

	@Override
	public String toString() {
		return formattedList;
	}
}
