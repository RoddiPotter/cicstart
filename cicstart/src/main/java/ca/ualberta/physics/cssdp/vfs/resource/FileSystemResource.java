/* ============================================================
 * FileSystemResource.java
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
package ca.ualberta.physics.cssdp.vfs.resource;

import java.io.File;
import java.io.InputStream;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.joda.time.LocalDateTime;

import ca.ualberta.physics.cssdp.auth.service.AuthClient;
import ca.ualberta.physics.cssdp.configuration.InjectorHolder;
import ca.ualberta.physics.cssdp.domain.vfs.VfsListing;
import ca.ualberta.physics.cssdp.domain.vfs.VfsListingEntry;
import ca.ualberta.physics.cssdp.jaxb.Link;
import ca.ualberta.physics.cssdp.service.ServiceResponse;
import ca.ualberta.physics.cssdp.vfs.service.FileSystemService;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * A virtual file system accessible via REST commands. Delete is a special case
 * because we use a POST method instead of a DELETE method only because DELETE
 * does not accept request parameters.
 * 
 * @author rpotter
 * 
 */
public class FileSystemResource {

	@Inject
	private FileSystemService fileSystemService;

	@Inject
	private AuthClient authClient;

	public FileSystemResource() {
		InjectorHolder.inject(this);
	}

	@POST
	@Path("/{owner}/write")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@ApiOperation(value = "Write a file to this filesystem at the given path.  "
			+ "If the file already exists, a '(n)' is suffixed to the filename. "
			+ "Non-existing paths will automatically be created.")
	@ApiErrors(value = {
			@ApiError(code = 400, reason = "No owner specified"),
			@ApiError(code = 400, reason = "No path specified"),
			@ApiError(code = 400, reason = "No file data specified"),
			@ApiError(code = 404, reason = "No session found"),
			@ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response write(
			@ApiParam(value = "The owner of this file system", required = true) @PathParam("owner") Long owner,
			@ApiParam(value = "The path to write the data to", required = true) @FormDataParam("path") String path,
			@ApiParam(value = "The file data to write", required = true) @FormDataParam("file") InputStream inputStream,
			@FormDataParam("file") FormDataContentDisposition disposition,
			@ApiParam(value = "The authenticated session token", required = true) @HeaderParam("CICSTART.session") String sessionToken,
			@Context UriInfo uriInfo) {

		if (Strings.isNullOrEmpty(path)) {
			return Response.status(400).build();
		}
		if (owner == null) {
			return Response.status(400).build();
		}

		validateSessionAndOwner(owner, sessionToken);

		path = path.endsWith("/") ? path : path + "/";

		ServiceResponse<Void> sr = fileSystemService.write(owner, path,
				disposition.getFileName(), inputStream);

		if (sr.isRequestOk()) {

			return Response
					.status(201)
					.location(
							UriBuilder
									.fromUri(uriInfo.getBaseUri())
									.path(getClass())
									.path(owner.toString())
									.path("read")
									.queryParam("path",
											path + disposition.getFileName())
									.build()).build();

		} else {
			return Response.status(500).entity(sr.getMessagesAsStrings())
					.build();
		}

	}

	private boolean validateSessionAndOwner(Long owner, String sessionToken) {
		if (authClient.whois(sessionToken).getId().equals(owner)) {
			return true;
		}
		throw new WebApplicationException(404);
	}

	@GET
	@Path("/{owner}/read")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@ApiOperation(value = "Read a file from this filesystem")
	@ApiErrors(value = {
			@ApiError(code = 400, reason = "No owner specified"),
			@ApiError(code = 400, reason = "No path specified"),
			@ApiError(code = 404, reason = "File not found"),
			@ApiError(code = 404, reason = "No session found"),
			@ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response read(
			@ApiParam(value = "The owner of this filesystem", required = true) @PathParam("owner") Long owner,
			@ApiParam(value = "The path of the file to read", required = true) @QueryParam("path") String path,
			@ApiParam(value = "The authenticated session token", required = true) @HeaderParam("CICSTART.session") String sessionToken) {

		if (Strings.isNullOrEmpty(path)) {
			return Response.status(400).build();
		}
		if (owner == null) {
			return Response.status(400).build();
		}
		validateSessionAndOwner(owner, sessionToken);

		File file = fileSystemService.read(owner, path).getPayload();

		if (file != null) {

			ResponseBuilder response = Response.ok((Object) file);
			response.type(MediaType.APPLICATION_OCTET_STREAM);
			response.header("Content-Disposition", "attachment; "
					+ "filename=\"" + file.getName() + "\"");
			return response.build();

		} else {
			return Response.status(404).build();
		}
	}

	@GET
	@Path("/{owner}/ls")
	@ApiOperation(value = "List the contents of a directory in this filesystem", responseClass = "ca.ualberta.physics.cssdp.domain.vfs.VfsListing")
	@ApiErrors(value = {
			@ApiError(code = 400, reason = "No owner specified"),
			@ApiError(code = 400, reason = "No path specified"),
			@ApiError(code = 404, reason = "Path not found"),
			@ApiError(code = 404, reason = "No session found"),
			@ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response ls(
			@ApiParam(value = "The owner of this filesystem", required = true) @PathParam("owner") Long owner,
			@ApiParam(value = "The path of the file to read", required = true) @QueryParam("path") String path,
			@ApiParam(value = "The authenticated session token", required = true) @HeaderParam("CICSTART.session") String sessionToken,
			@Context UriInfo uriInfo) {

		if (Strings.isNullOrEmpty(path)) {
			return Response.status(400).build();
		}
		if (owner == null) {
			return Response.status(400).build();
		}
		validateSessionAndOwner(owner, sessionToken);

		path = path.endsWith("/") ? path : path + "/";

		ServiceResponse<File[]> sr = fileSystemService.ls(owner, path);
		if (sr.isRequestOk()) {

			File[] files = sr.getPayload();
			if (files != null) {

				VfsListing listing = new VfsListing();

				listing.setPath(new Link(path, UriBuilder
						.fromUri(uriInfo.getBaseUri()).path(getClass())
						.path(owner.toString()).path("ls")
						.queryParam("path", path).build()));

				for (File file : files) {

					VfsListingEntry entry = new VfsListingEntry();
					entry.setDir(file.isDirectory());

					entry.setPath(new Link(file.getName(), UriBuilder
							.fromUri(uriInfo.getBaseUri()).path(getClass())
							.path(owner.toString()).path("read")
							.queryParam("path", path + file.getName()).build()));
					entry.setSize(file.length());
					entry.setLastModified(new LocalDateTime(new Date(file
							.lastModified())));
					listing.getEntires().add(entry);
				}

				return Response.ok(listing).build();
			} else {
				return Response.status(204).build();
			}
		} else {
			return Response.status(500).entity(sr.getMessagesAsStrings())
					.build();
		}
	}

	/**
	 * Not "RESTful", but DELETE doesn't take parameters and we need the params
	 * to locate the file or directory to delete
	 * 
	 * @param owner
	 * @param path
	 * @return
	 */
	@POST
	@Path("/{owner}/rm")
	@ApiOperation(value = "Remove a file or files in a directory in this filesystem")
	@ApiErrors(value = {
			@ApiError(code = 400, reason = "No owner specified"),
			@ApiError(code = 400, reason = "No path specified"),
			@ApiError(code = 404, reason = "File or path not found"),
			@ApiError(code = 404, reason = "No session found"),
			@ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response rm(
			@ApiParam(value = "The owner of this filesystem", required = true) @PathParam("owner") Long owner,
			@ApiParam(value = "The path of the file to read", required = true) @QueryParam("path") String path,
			@ApiParam(value = "The authenticated session token", required = true) @HeaderParam("CICSTART.session") String sessionToken) {

		if (Strings.isNullOrEmpty(path)) {
			return Response.status(400).build();
		}
		if (owner == null) {
			return Response.status(400).build();
		}

		validateSessionAndOwner(owner, sessionToken);

		ServiceResponse<Void> sr = fileSystemService.rm(owner, path);

		if (sr.isRequestOk()) {
			return Response.ok().build();
		} else {
			return Response.status(500).entity(sr.getMessagesAsStrings())
					.build();
		}
	}

}
