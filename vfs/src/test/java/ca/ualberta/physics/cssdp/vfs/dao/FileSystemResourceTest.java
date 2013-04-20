/* ============================================================
 * FileSystemResourceTest.java
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
package ca.ualberta.physics.cssdp.vfs.dao;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import ca.ualberta.physics.cssdp.configuration.VfsServer;

import com.google.common.io.Files;
import com.jayway.restassured.response.Response;

public class FileSystemResourceTest extends VfsTestsScaffolding {

	@Test
	public void testWriteReadListAndDeleteFile() throws Exception {

		// delete the test file in the vfs first
		File userDir = new File(VfsServer.properties().getString("vfs_root"),
				vfsUser.getId().toString());
		File testFile = new File(userDir, "build.gradle");
		testFile.delete();

		String sessionToken = login(vfsUser.getEmail(), "password");
		File file = new File("build.gradle");
		Response res = expect()
				.statusCode(201)
				.and()
				.header("location",
						"http://localhost:8080" + baseUrl()
								+ "/filesystem.json/" + vfsUser.getId()
								+ "/read?path=/build.gradle")
				.given()
				.header("CICSTART.session", sessionToken)
				.and()
				.multiPart("file", file)
				.and()
				.formParam("path", "/")
				.and()
				.when()
				.post(baseUrl() + "/filesystem.json/{owner}/write",
						vfsUser.getId());

		Assert.assertArrayEquals(
				Files.toByteArray(file),
				given().header("CICSTART.session", sessionToken)
						.get(res.getHeader("location")).asByteArray());

		res = given()
				.header("CICSTART.session", sessionToken)
				.and()
				.queryParam("path", "/")
				.get(baseUrl() + "/filesystem.json/{owner}/ls", vfsUser.getId());

		System.out.println(res.asString());

	}
}
