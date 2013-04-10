package ca.ualberta.physics.cssdp.util;

import org.junit.Assert;
import org.junit.Test;

public class UrlParserTest {

	@Test
	public void testGetPath() {
		
		Assert.assertEquals("/dir/on/host", UrlParser.getPath("ftp://ssl.justice.berkely.edu/dir/on/host"));
		Assert.assertEquals("/dir/on/host/somefile.txt", UrlParser.getPath("ftp://ssl.justice.berkely.edu/dir/on/host/somefile.txt"));
		Assert.assertEquals("/local/file.zip", UrlParser.getPath("file:///local/file.zip"));
		Assert.assertEquals("/local/file.zip", UrlParser.getPath("/local/file.zip"));
		
	}
	
	@Test
	public void testGetHostname() {
		
		Assert.assertNull(UrlParser.getHostname("file:///home/rpotter/file.txt"));
		Assert.assertEquals("ssl.justice.berkely.edu", UrlParser.getHostname("ftp://ssl.justice.berkely.edu/dir/on/host/somefile.txt"));
		Assert.assertEquals("localhost", UrlParser.getHostname("ftp://localhost/ftp/somefile.txt"));
		Assert.assertEquals("ssl.justice.berkely.edu", UrlParser.getHostname("ftp://ssl.justice.berkely.edu:21/dir/on/host/somefile.txt"));
		
	}
	
	@Test 
	public void testGetLeaf() {
		
		Assert.assertEquals("host", UrlParser.getLeaf("ftp://ssl.justice.berkely.edu/dir/on/host"));
		Assert.assertEquals("somefile.txt", UrlParser.getLeaf("ftp://ssl.justice.berkely.edu/dir/on/host/somefile.txt"));
		Assert.assertEquals("file.zip", UrlParser.getLeaf("file:///local/file.zip"));
		Assert.assertEquals("file.zip", UrlParser.getLeaf("/local/file.zip"));
		Assert.assertEquals("", UrlParser.getLeaf("/local/dir/"));

	}
	
}
