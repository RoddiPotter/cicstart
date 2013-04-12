/* ============================================================
 * Model.java
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
package ca.ualberta.physics.cssdp.model;

import java.io.Serializable;

/**
 * A Model simply enforces that all descendants implement a way of generating a
 * primary key for the object they represent
 */
public abstract class Model implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The primary that can differentiate this object from another of the same
	 * type. This is used in the hashcode and equals methods.
	 * 
	 * @return a String representation of primary key
	 */
	public abstract String _pk();

	@Override
	public int hashCode() {
		return _pk().hashCode();
	}

	@Override
	public boolean equals(Object that) {

		if (that == null) {
			return false;
		}

		if (this == that) {
			return true;
		}

		Model that2 = null;
		try {
			that2 = (Model) that;
			return _pk().equals(that2._pk());
		} catch (ClassCastException cce) {
			return false;
		}

	}

}
