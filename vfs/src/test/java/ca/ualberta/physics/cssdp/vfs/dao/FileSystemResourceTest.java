package ca.ualberta.physics.cssdp.vfs.dao;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import ca.ualberta.physics.cssdp.vfs.VfsServer;

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
						"http://localhost:8080/vfs/filesystem.json/"
								+ vfsUser.getId() + "/read?path=/build.gradle")
				.given().header("CICSTART.session", sessionToken).and()
				.multiPart("file", file).and().formParam("path", "/").and()
				.when()
				.post("/vfs/filesystem.json/{owner}/write", vfsUser.getId());

		Assert.assertArrayEquals(
				Files.toByteArray(file),
				given().header("CICSTART.session", sessionToken)
						.get(res.getHeader("location")).asByteArray());

		res = given().header("CICSTART.session", sessionToken).and()
				.queryParam("path", "/")
				.get("/vfs/filesystem.json/{owner}/ls", vfsUser.getId());

		System.out.println(res.asString());

	}
	// @Test
	// public void testPathSegments() {
	//
	// // just used during construction to figure out the regex for path
	// // segments
	// WebResource webResource = client
	// .resource("http://localhost:8082/vfs/get/"
	// + user.getUserIdentity() + "/path/to/some/file.txt");
	//
	// webResource.get(ClientResponse.class);
	//
	// ClientResponse r = webResource.get(ClientResponse.class);
	// Assert.assertEquals(404, r.getStatus());
	//
	// }
	//
	// @Test
	// public void testGetFile() {
	//
	// ClientResponse response = storeFile(user, new File(
	// "src/test/java/here-be-dragons.txt"), "/a-new-path");
	//
	// WebResource webResource = client.resource(response.getLocation());
	// response = webResource.get(ClientResponse.class);
	//
	// File storedFile = response.getEntity(File.class);
	// System.out.println(storedFile.getAbsolutePath());
	// }
	//
	// @Test
	// public void testList() {
	//
	// ClientResponse response = storeFile(user, new File(
	// "src/test/java/here-be-dragons.txt"), "/a-new-path");
	//
	// WebResource webResource = client
	// .resource("http://localhost:8082/vfs/ls/"
	// + user.getUserIdentity() + "/");
	// response = webResource.accept(MediaType.APPLICATION_JSON).get(
	// ClientResponse.class);
	//
	// VfsListing listing = response.getEntity(VfsListing.class);
	//
	// Assert.assertEquals("/", listing.getPath());
	// Assert.assertEquals(1, listing.getFiles().size());
	// Assert.assertEquals("a_root_file.txt", listing.getFiles().get(0));
	//
	// Assert.assertEquals(2, listing.getSubDirs().size());
	// Assert.assertEquals("a-new-path", listing.getSubDirs().get(0));
	// Assert.assertEquals("some", listing.getSubDirs().get(1));
	//
	// }
	//
	// @Test
	// public void testDelete() {
	//
	// ClientResponse response = storeFile(user, new File(
	// "src/test/java/here-be-dragons.txt"), "/another-new-path");
	//
	// WebResource webResource = client.resource(
	// "http://localhost:8082/vfs/rm/" + user.getUserIdentity()
	// + "/another-new-path").queryParam("r", "true");
	// response = webResource.delete(ClientResponse.class);
	//
	// Assert.assertEquals(200, response.getStatus());
	//
	// }
	//
	// private ClientResponse storeFile(VfsUser user, File localFile,
	// String pathInVfs) {
	//
	// WebResource webResource = client
	// .resource("http://localhost:8082/vfs/put/"
	// + user.getUserIdentity());
	//
	// FormDataMultiPart fdmp = new FormDataMultiPart();
	// fdmp.field("path", pathInVfs);
	// fdmp.bodyPart(new FileDataBodyPart("file", localFile,
	// MediaType.APPLICATION_OCTET_STREAM_TYPE));
	//
	// ClientResponse response = webResource.type(
	// MediaType.MULTIPART_FORM_DATA_TYPE).put(ClientResponse.class,
	// fdmp);
	//
	// return response;
	// }
}
