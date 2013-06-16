package ca.ualberta.physics.cicstart.macro.service;

import java.util.List;

import ca.ualberta.physics.cicstart.macro.service.OpenStackCloud.Flavor;
import ca.ualberta.physics.cicstart.macro.service.OpenStackCloud.Identity;
import ca.ualberta.physics.cssdp.domain.macro.Instance;

public interface Cloud {

	/**
	 * Initialize the cloud instance
	 * 
	 * @param cloudName
	 */
	public void init(String cloudName);

	public Identity authenticate(String username, String password);

	/**
	 * Start machine instances given the machine count, the size, and the image
	 * to base the instances off of
	 * 
	 * @param identity
	 *            authenticated cloud user
	 * @param image
	 *            image to use to start the instance
	 * @param flavor
	 *            flavor of the instance (tiny, small, etc).
	 * @param name
	 *            name of the instance to refer to later
	 * 
	 * @return an instance object with password and ip address
	 */
	public Instance startInstance(Identity identity, Image image,
			Flavor flavor, String name);

	/**
	 * Creates a block storage volume
	 * 
	 * @return
	 */
	public Object createVolume();

	/**
	 * Attaches the given volume to an instance and returns the device the
	 * volume is attached at
	 * 
	 * @return
	 */
	public String attachVolume();

	/**
	 * Stops the given instance
	 */
	public void stopInstance(Identity identity, Instance instance);

	/**
	 * Detaches the volume from whatever instance it's attached to
	 */
	public void detachVolume();

	/**
	 * Deletes the volume
	 */
	public void deleteVolume();

	/**
	 * Creates a snapshot of a running instance for later inspection (debugging)
	 */
	public void createSnapshot();

	/**
	 * Gets a list of images
	 * 
	 * @param sessionToken
	 */
	public List<Image> getImages(Identity identity);

	/**
	 * Adds the given key to this cloud account if it's not already there.
	 * 
	 * @param clientIdentity
	 * @param keyname
	 * @param publicKey
	 */
	public void putKey(Identity clientIdentity, String keyname, String publicKey);

}
