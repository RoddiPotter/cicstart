package ca.ualberta.physics.cicstart.macro.service;

import com.google.common.base.Objects;

/**
 * Represents an image in the cloud
 * 
 */
public class Image {

	public String id;
	public String name;
	public String href;

	
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("id", id).add("name", name).add("href", href).toString();
	}
}
