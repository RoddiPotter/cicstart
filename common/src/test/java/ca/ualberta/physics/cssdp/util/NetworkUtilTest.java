package ca.ualberta.physics.cssdp.util;

import org.junit.Assert;
import org.junit.Test;

public class NetworkUtilTest {

	@Test
	public void testCurrentlyRunningOn() {
		
		Assert.assertTrue(NetworkUtil.currentlyRunningOn("localhost"));
		Assert.assertTrue(NetworkUtil.currentlyRunningOn("127.0.0.1"));
		Assert.assertTrue(NetworkUtil.currentlyRunningOn("192.168.1.67"));
		Assert.assertTrue(NetworkUtil.currentlyRunningOn("rod-work.local"));
		
		Assert.assertFalse(NetworkUtil.currentlyRunningOn("50.99.195.108"));
		Assert.assertFalse(NetworkUtil.currentlyRunningOn("10.0.28.4"));
	}
	
}
