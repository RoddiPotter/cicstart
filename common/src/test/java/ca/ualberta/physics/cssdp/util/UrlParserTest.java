/* ============================================================
 * UrlParserTest.java
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
