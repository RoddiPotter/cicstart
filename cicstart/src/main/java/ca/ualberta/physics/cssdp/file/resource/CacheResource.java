/* ============================================================
 * CacheResource.java
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
package ca.ualberta.physics.cssdp.file.resource;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import ca.ualberta.physics.cssdp.configuration.InjectorHolder;
import ca.ualberta.physics.cssdp.domain.file.CachedFile;
import ca.ualberta.physics.cssdp.domain.file.Host;
import ca.ualberta.physics.cssdp.file.remote.RemoteServers;
import ca.ualberta.physics.cssdp.file.remote.command.Download;
import ca.ualberta.physics.cssdp.file.service.CacheService;
import ca.ualberta.physics.cssdp.file.service.HostService;
import ca.ualberta.physics.cssdp.service.ServiceResponse;
import ca.ualberta.physics.cssdp.util.UrlParser;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * download is more like a host operation and will always cache the data; and
 * check the cache first before doing the download. The caching operation of the
 * download needs to split the input stream... one to the client and one to the
 * cache By default, all downloads are cached... All files are keyed in the
 * cache by MD5 of the file, as well as by the URL (if available) and any number
 * of arbitrary external keys.
 * 
 * <pre>
 * Cache:
 * =============================
 * PUT /file @multipart @formData(url), @FormData(key)
 * PUT /file/{MD5}/map?url=&key=
 * GET /file/find?url=&key=
 * GET /file/{MD5}
 * DELETE /file/{MD5}
 * </pre>
 * 
 * @author rpotter
 * 
 */
public class CacheResource {

	@Inject
	private CacheService cacheService;

	@Inject
	private RemoteServers remoteServers;

	@Inject
	private HostService hostService;

	public CacheResource() {
		InjectorHolder.inject(this);
	}

	@PUT
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@ApiOperation(value = "Upload a file directly into the cache.  "
			+ "Upload a file using multipart form data directly into the cache.  "
			+ "The MD5 of the file will be returned on successfull PUT operations.")
	@ApiErrors(value = {
			@ApiError(code = 400, reason = "No file supplied"),
			@ApiError(code = 404, reason = "No key or url supplied"),
			@ApiError(code = 409, reason = "Duplicate key for a different MD5"),
			@ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response addToCache(
			@ApiParam(value = "An arbitrary key to associate this file with (e.g., the url)", required = true) @FormDataParam("key") String key,
			@ApiParam(value = "The file data to cache", required = true) @FormDataParam("file") InputStream inputStream,
			@FormDataParam("file") FormDataContentDisposition disposition,
			@Context UriInfo uriInfo) {

		if (inputStream == null) {
			return Response.status(400).build();
		}

		if (Strings.isNullOrEmpty(key)) {
			return Response.status(404).build();
		}

		ServiceResponse<String> sr = cacheService.put(
				disposition.getFileName(), key, inputStream);

		if (sr.isRequestOk()) {
			return Response
					.status(201)
					.location(
							UriBuilder.fromUri(uriInfo.getBaseUri())
									.path(getClass())
									.queryParam("md5", sr.getPayload()).build())
					.build();
		} else {

			String errorMessages = sr.getMessagesAsStrings().toLowerCase();
			if (errorMessages.contains("external_key_uq")
					&& errorMessages.contains("violat")) {

				return Response.status(409).entity(errorMessages).build();

			} else {

				return Response.status(500).entity(errorMessages).build();
			}

		}

	}

	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@ApiOperation(value = "Get a file if it is cached already, otherwise download the file using the url given and cache it.  "
			+ " The url will be used a key to the cached file.  "
			+ " If the file is not cached, an attemp will be made to retrieve the file contents from the url given. This is an asynchronous "
			+ " request.  Expect response code 202 and use the location given to check the status. If the file is cached, then the file will"
			+ " be returned.  Giving an MD5 assumes the file is already cached and a 404 will be returned if it is not.")
	@ApiErrors(value = {
			@ApiError(code = 400, reason = "Neither MD5 or url is supplied"),
			@ApiError(code = 404, reason = "No file cached with MD5 given"),
			@ApiError(code = 404, reason = "No file cached with key given"),
			@ApiError(code = 404, reason = "No file catalogued with url given"),
			@ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response getFromCache(
			@ApiParam(value = "The MD5 of this cached file", required = false) @QueryParam("md5") String md5,
			@ApiParam(value = "The MD5 of this cached file", required = false) @QueryParam("key") String key,
			@ApiParam(value = "The URL of the uncached file", required = false) @QueryParam("url") String url,
			@Context UriInfo uriInfo) {

		if (Strings.isNullOrEmpty(md5) && Strings.isNullOrEmpty(url)
				&& Strings.isNullOrEmpty(key)) {

			return Response.status(400).build();

		} else if (!Strings.isNullOrEmpty(md5)) {

			ServiceResponse<CachedFile> sr = cacheService.get(md5);
			if (sr.isRequestOk()) {

				CachedFile cachedFile = sr.getPayload();
				if (cachedFile != null) {

					ResponseBuilder response = Response.ok((Object) cachedFile
							.getFile());
					response.type(MediaType.APPLICATION_OCTET_STREAM);
					response.header("Content-Disposition", "attachment; "
							+ "filename=\"" + cachedFile.getFilename() + "\"");
					return response.build();

				} else {
					return Response.status(404).build();
				}

			} else {

				return Response.status(500).entity(sr.getMessagesAsStrings())
						.build();

			}
		} else {

			ServiceResponse<CachedFile> sr = null;
			if (!Strings.isNullOrEmpty(key)) {
				sr = cacheService.find(key);
			} else {
				sr = cacheService.find(url);
			}

			if (sr.isRequestOk()) {

				CachedFile cachedFile = sr.getPayload();

				if (cachedFile != null) {

					ResponseBuilder response = Response.ok((Object) cachedFile
							.getFile());
					response.type(MediaType.APPLICATION_OCTET_STREAM);
					response.header("Content-Disposition", "attachment; "
							+ "filename=\"" + cachedFile.getFilename() + "\"");
					return response.build();

				} else if (!Strings.isNullOrEmpty(url)) {

					String hostname = UrlParser.getHostname(url);
					ServiceResponse<Host> getHostSr = hostService
							.getHostEntry(hostname);
					if (getHostSr.getPayload() != null) {
						Host host = getHostSr.getPayload();
						if (!remoteServers.contains(host)) {
							remoteServers.add(host);
						}
					} else {
						return Response
								.status(400)
								.entity("Please add "
										+ hostname
										+ " to "
										+ UriBuilder
												.fromResource(HostResourceJSON.class)
										+ " and try again.").build();
					}

					// no file, let's download it from the remote host
					Download downloadCommand = new Download(cacheService,
							hostname, url);
					remoteServers.requestOperation(downloadCommand);

					return Response
							.status(202)
							.location(
									UriBuilder.fromUri(uriInfo.getBaseUri())
											.path(getClass())
											.queryParam("url", url).build())
							.entity("Accepted request to download file from "
									+ url
									+ " and cache it.  "
									+ "Try (and repeat) the url given in the location header "
									+ "until the file data becomes available.")
							.build();
				} else {
					return Response.status(404).entity(key + " is not cached")
							.build();
				}
			} else {
				return Response.status(500).entity(sr.getMessagesAsStrings())
						.build();
			}

		}

	}

	@DELETE
	@Path("/{MD5}")
	@ApiOperation(value = "Remove a file from cache")
	@ApiErrors(value = {
			@ApiError(code = 400, reason = "No MD5 supplied"),
			@ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response removeFromCache(
			@ApiParam(value = "The MD5 of this cached file", required = true) @PathParam("MD5") String md5) {

		ServiceResponse<CachedFile> sr = cacheService.remove(md5);

		if (sr.isRequestOk()) {
			return Response.ok().build();
		} else {
			return Response.status(500).entity(sr.getMessagesAsStrings())
					.build();
		}

	}

	@GET
	@Path("/find")
	@ApiOperation(value = "Find the MD5 for a cached file")
	@ApiErrors(value = {
			@ApiError(code = 400, reason = "No key supplied"),
			@ApiError(code = 404, reason = "No file cached for given key"),
			@ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response find(
			@ApiParam(value = "One of the cached file's keys (e.g., the encoded url of the file)", required = true) @QueryParam("key") String key) {

		if (Strings.isNullOrEmpty(key)) {
			return Response.status(400).build();
		}

		ServiceResponse<CachedFile> sr = null;
		try {
			sr = cacheService.find(URLDecoder.decode(key, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			return Response.status(500).entity(e.getMessage()).build();
		}
		if (sr.isRequestOk()) {

			CachedFile cachedFile = sr.getPayload();
			if (cachedFile != null) {

				return Response.ok(cachedFile.getMd5()).build();

			} else {
				return Response.status(404).build();
			}

		} else {

			return Response.status(500).entity(sr.getMessagesAsStrings())
					.build();

		}

	}

	@PUT
	@Path("/{MD5}/map")
	@ApiOperation(value = "Map a new key to this cached file")
	@ApiErrors(value = {
			@ApiError(code = 400, reason = "No key or md5 supplied"),
			@ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response map(
			@ApiParam(value = "The MD5 of the cached file", required = true) @PathParam("MD5") String md5,
			@ApiParam(value = "A new key to map (e.g., the url of the file)", required = true) @FormParam("key") String key) {

		if (Strings.isNullOrEmpty(md5)) {
			return Response.status(400).build();
		}

		if (Strings.isNullOrEmpty(key)) {
			return Response.status(400).build();
		}

		ServiceResponse<CachedFile> sr = cacheService.updateKeys(md5, key);
		if (sr.isRequestOk()) {

			return Response.ok().build();

		} else {

			return Response.status(500).entity(sr.getMessagesAsStrings())
					.build();

		}

	}

}
