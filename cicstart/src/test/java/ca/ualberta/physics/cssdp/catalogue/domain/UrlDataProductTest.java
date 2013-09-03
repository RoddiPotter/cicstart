/* ============================================================
 * UrlDataProductTest.java
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

package ca.ualberta.physics.cssdp.catalogue.domain;

import org.joda.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Test;

import ca.ualberta.physics.cssdp.domain.catalogue.DataProduct;
import ca.ualberta.physics.cssdp.domain.catalogue.Project;
import ca.ualberta.physics.cssdp.domain.catalogue.UrlDataProduct;
import ca.ualberta.physics.cssdp.model.Mnemonic;

public class UrlDataProductTest {

	@Test
	public void testHasNotChanged() {

		String url = "file:///home/users/rod/somefile.txt";
		DataProduct dataProduct = new DataProduct();
		LocalDateTime start = new LocalDateTime().minusDays(1);
		LocalDateTime end = start.plusDays(1);
		boolean isDeleted = false;
		
		UrlDataProduct first = new UrlDataProduct();
		first.setUrl(url);
		first.setDataProduct(dataProduct);
		first.setStartTimestamp(start);
		first.setEndTimestamp(end);
		first.setDeleted(isDeleted);
		
		UrlDataProduct second = new UrlDataProduct();
		second.setUrl(url);
		second.setDataProduct(dataProduct);
		second.setStartTimestamp(start);
		second.setEndTimestamp(end);
		second.setDeleted(isDeleted);
		
		Assert.assertFalse(first.hasChanged(second));
		
	}

	@Test
	public void testHasNotChanged2() {

		String url = "ftp://sunsite.ualberta.ca/pub/Mirror/apache/commons/daemon/binaries/commons-daemon-1.0.15.jar";
		DataProduct dataProduct = new DataProduct();
		LocalDateTime start = null;
		LocalDateTime end = null;
		boolean isDeleted = false;
		
		UrlDataProduct first = new UrlDataProduct();
		first.setUrl(url);
		first.setDataProduct(dataProduct);
		first.setStartTimestamp(start);
		first.setEndTimestamp(end);
		first.setDeleted(isDeleted);
		
		UrlDataProduct second = new UrlDataProduct();
		second.setUrl(url);
		second.setDataProduct(dataProduct);
		second.setStartTimestamp(start);
		second.setEndTimestamp(end);
		second.setDeleted(isDeleted);
		
		Assert.assertFalse(first.hasChanged(second));
		
	}

	@Test
	public void testHasChanged() {

		/*
		 * First test to set a baseline
		 * Next test each change individually, reseting after each
		 * Next test all changes together
		 */
		
		String url = "file:///home/users/rod/somefile.txt";
		DataProduct dataProduct = new DataProduct();
		dataProduct.setExternalKey(new Mnemonic("FIT-RNK"));
		Project project = new Project();
		project.setExternalKey(new Mnemonic("SUPERDARN"));
		dataProduct.setProject(project);
		
		LocalDateTime start = new LocalDateTime().minusDays(1);
		LocalDateTime end = start.plusDays(1);
		boolean isDeleted = false;
		
		UrlDataProduct first = new UrlDataProduct();
		first.setUrl(url);
		first.setDataProduct(dataProduct);
		first.setStartTimestamp(start);
		first.setEndTimestamp(end);
		first.setDeleted(isDeleted);
		
		UrlDataProduct second = new UrlDataProduct();
		second.setUrl(url);
		second.setDataProduct(dataProduct);
		second.setStartTimestamp(start);
		second.setEndTimestamp(end);
		second.setDeleted(isDeleted);
		
		
		Assert.assertFalse(first.hasChanged(second));
		
		second.setUrl("changed");
		Assert.assertTrue(first.hasChanged(second));
		second.setUrl(url);
		Assert.assertFalse(first.hasChanged(second));

		DataProduct changedDataProduct = new DataProduct();
		changedDataProduct.setExternalKey(new Mnemonic("FIT-SAS"));
		changedDataProduct.setProject(project);
		Assert.assertFalse(first.hasChanged(second));

		second.setDataProduct(changedDataProduct);
		Assert.assertTrue(first.hasChanged(second));
		second.setDataProduct(dataProduct);
		Assert.assertFalse(first.hasChanged(second));
		
		second.setStartTimestamp(new LocalDateTime());
		Assert.assertTrue(first.hasChanged(second));
		second.setStartTimestamp(start);
		Assert.assertFalse(first.hasChanged(second));
		
		second.setEndTimestamp(new LocalDateTime().plusMinutes(1));
		Assert.assertTrue(first.hasChanged(second));
		second.setEndTimestamp(end);
		Assert.assertFalse(first.hasChanged(second));
		
		// everything has changed
		second.setUrl("changed");
		second.setDataProduct(changedDataProduct);
		second.setStartTimestamp(new LocalDateTime());
		second.setEndTimestamp(new LocalDateTime());
		Assert.assertTrue(first.hasChanged(second));
		
		
	}

}
