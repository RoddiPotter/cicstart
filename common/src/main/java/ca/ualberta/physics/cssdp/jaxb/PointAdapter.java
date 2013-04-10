package ca.ualberta.physics.cssdp.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import ca.ualberta.physics.cssdp.model.Point;

/**
 * Marshals Points back and forth between String and Point
 * representations.
 */
public class PointAdapter extends XmlAdapter<String, Point> {

	@Override
	public String marshal(Point p) throws Exception {
		return p.toASCIIString();
	}

	@Override
	public Point unmarshal(String value) throws Exception {
		return new Point(value);
	}

}
