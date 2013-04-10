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
