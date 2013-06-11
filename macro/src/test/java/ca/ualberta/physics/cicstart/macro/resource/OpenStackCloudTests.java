package ca.ualberta.physics.cicstart.macro.resource;

import java.util.List;

import org.junit.Assert;
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

	@Test
	public void testStartInstance() {

		User user = setupDataManager();
		String sessionToken = login(user.getEmail(), "password");

		Cloud cloud = new OpenStackCloud();
		Identity id = cloud.authenticate(user.getOpenStackUsername(),
				user.getOpenStackPassword());

		Image image = new Image();
		image.id = "a8951146-154f-481b-b65a-3d3337ca685d";
		Instance instance = cloud.startInstance(id, image, Flavor.m1_tiny,
				sessionToken + "testJobId");

		Assert.assertFalse(Strings.isNullOrEmpty(instance.href));
		Assert.assertFalse(Strings.isNullOrEmpty(instance.ipAddress));
		Assert.assertFalse(Strings.isNullOrEmpty(instance.password));

		System.out.println(instance.ipAddress + " " + instance.password);

		// cleanup
		cloud.stopInstance(id, instance);

	}

	@Test
	public void testListImages() {

		User user = setupDataManager();

		Cloud cloud = new OpenStackCloud();
		Identity id = cloud.authenticate(user.getOpenStackUsername(),
				user.getOpenStackPassword());

		List<Image> images = cloud.getImages(id);

		Assert.assertTrue(images.size() >= 5);
		

	}
}
