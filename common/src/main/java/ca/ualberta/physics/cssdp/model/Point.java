/* ============================================================
 * Point.java
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

import com.wordnik.swagger.annotations.ApiClass;

@ApiClass(value = "Defines a point in two dimensional space.  Use format x,y in XML and JSON objects")
public class Point implements Serializable {

	private static final long serialVersionUID = 1L;

	private double x;

	private double y;

	public Point() {

	}

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Point(Point point) {
		this.x = point.x;
		this.y = point.y;
	}

	/**
	 * Given the pattern x,y create a Point object
	 * 
	 * @param point
	 */
	public Point(String point) {

		point = point.replaceAll(" ", "");
		if (!point
				.matches("^-?\\d{1,3}\\.?\\d{0,4}\\,\\s?-?\\d{1,3}\\.?\\d{0,4}$")) {
			throw new IllegalArgumentException(point
					+ " is not in the form x,y");
		}

		point = point.replaceAll("\\s", "");
		String[] parts = point.split(",");
		String xString = parts[0];
		String yString = parts[1];

		x = Double.parseDouble(xString);
		y = Double.parseDouble(yString);
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	private String _pk() {
		return toString();
	}

	@Override
	public String toString() {
		return x + "," + y;
	}

	public String toASCIIString() {
		return toString();
	}

	@Override
	public boolean equals(Object other) {
		return _pk().equals(((Point) other)._pk());
	}

	@Override
	public int hashCode() {
		return _pk().hashCode();
	}
}
