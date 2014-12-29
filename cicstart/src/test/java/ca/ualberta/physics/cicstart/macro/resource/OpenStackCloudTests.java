package ca.ualberta.physics.cicstart.macro.resource;

import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import ca.ualberta.physics.cicstart.macro.service.Cloud;
import ca.ualberta.physics.cicstart.macro.service.Image;
import ca.ualberta.physics.cicstart.macro.service.OpenStackCloud;
import ca.ualberta.physics.cicstart.macro.service.OpenStackCloud.Flavor;
import ca.ualberta.physics.cicstart.macro.service.OpenStackCloud.Identity;
import ca.ualberta.physics.cssdp.domain.auth.User;
import ca.ualberta.physics.cssdp.domain.macro.Instance;

import com.google.common.base.Strings;

public class OpenStackCloudTests extends MacroTestsScaffolding {

	@Ignore
	@Test
	public void testStartInstance() {

		User user = setupDataManager();

		Cloud cloud = new OpenStackCloud();
		cloud.init("DAIR");
		Identity id = cloud.authenticate(user.getOpenStackUsername(),
				user.getOpenStackPassword());

		Image theImage = null;
		for (Image image : cloud.getImages(id)) {

			if (image.id.equals("a8951146-154f-481b-b65a-3d3337ca685d")) {
				theImage = image;
				break;
			}

		}

		Instance instance = cloud.startInstance(id, theImage, Flavor.m1_tiny,
				"UNIT_TEST");

		Assert.assertFalse(Strings.isNullOrEmpty(instance.href));
		Assert.assertFalse(Strings.isNullOrEmpty(instance.ipAddress));
		Assert.assertFalse(Strings.isNullOrEmpty(instance.id));

		System.out.println(instance.ipAddress + " " + instance.id);

		// cleanup
		cloud.stopInstance(id, instance);

		Assert.fail(instance.id + " release my ip address!");
		
	}
	
	// run if you have a DAIR account.  Update macro.properties with credentials
	@Ignore
	@Test
	public void testListImages() {

		User user = setupDataManager();

		Cloud cloud = new OpenStackCloud();
		cloud.init("DAIR");
		Identity id = cloud.authenticate(user.getOpenStackUsername(),
				user.getOpenStackPassword());

		List<Image> images = cloud.getImages(id);

		Assert.assertTrue(images.size() >= 5);

	}
}
