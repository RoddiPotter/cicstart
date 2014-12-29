package ca.ualberta.physics.cssdp.util;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class NetworkUtilTest {

	@Ignore
	@Test
	public void testCurrentlyRunningOn() {
		
		Assert.assertTrue(NetworkUtil.currentlyRunningOn("localhost"));
		Assert.assertTrue(NetworkUtil.currentlyRunningOn("127.0.0.1"));
		Assert.assertTrue(NetworkUtil.currentlyRunningOn("192.168.1.67"));
		Assert.assertTrue(NetworkUtil.currentlyRunningOn("rpotter-ThinkPad-T61.local"));
		
		Assert.assertFalse(NetworkUtil.currentlyRunningOn("50.99.195.108"));
		Assert.assertFalse(NetworkUtil.currentlyRunningOn("10.0.28.4"));
	}
	
	@Test
	public void testGetLocalHostIp() {
		
		Assert.assertEquals("127.0.0.1", NetworkUtil.getLocalHostIp());
		
	}
}
