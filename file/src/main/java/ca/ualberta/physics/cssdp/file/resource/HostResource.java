/* ============================================================
 * HostResource.java
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

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import ca.ualberta.physics.cssdp.client.AuthClient;
import ca.ualberta.physics.cssdp.domain.auth.User;
import ca.ualberta.physics.cssdp.domain.auth.User.Role;
import ca.ualberta.physics.cssdp.domain.file.DirectoryListing;
import ca.ualberta.physics.cssdp.domain.file.Host;
import ca.ualberta.physics.cssdp.domain.file.RemoteFile;
import ca.ualberta.physics.cssdp.file.InjectorHolder;
import ca.ualberta.physics.cssdp.file.remote.RemoteServers;
import ca.ualberta.physics.cssdp.file.remote.command.RecursiveLs;
import ca.ualberta.physics.cssdp.file.service.HostService;
import ca.ualberta.physics.cssdp.service.ServiceResponse;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

public class HostResource {

	@Inject
	private RemoteServers remoteServers;

	@Inject
	private HostService hostService;

	@Inject
	private AuthClient authClient;

	public HostResource() {
		InjectorHolder.inject(this);
	}

	@GET
	@ApiOperation(value = "Get a Host object", notes = "Get details about a host.  Username and password are masked in response.")
	@Path("/{hostname}")
	@ApiErrors(value = {
			@ApiError(code = 400, reason = "No hostname supplied"),
			@ApiError(code = 404, reason = "Hostname not found in registry"),
			@ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response getHostEntry(
			@ApiParam(value = "The hostname to lookup", required = true) @PathParam("hostname") String hostname,
			@Context UriInfo urlInfo) {

		if (Strings.isNullOrEmpty(hostname)) {
			return Response.status(400).build();
		}

		ServiceResponse<Host> sr = hostService.getHostEntry(hostname);
		if (sr.isRequestOk()) {
			Host host = sr.getPayload();
			if (host != null) {
				host.maskUser();
				host.maskPassword();
				return Response.ok(host).build();
			} else {
				return Response.status(404).build();
			}
		} else {
			return Response.status(500).entity(sr.getMessagesAsStrings())
					.build();
		}

	}

	@POST
	@ApiOperation(value = "Add Host", notes = "Adds a new host object.  This operation requires the user to be logged in.")
	@ApiErrors(value = {
			@ApiError(code = 400, reason = "No host object value supplied"),
			@ApiError(code = 404, reason = "No session found"),
			@ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response addHost(
			@ApiParam(value = "The Host object to add", required = true) Host host,
			@ApiParam(value = "The authenticated session token", required = true) @HeaderParam("CICSTART.session") String sessionToken,
			@Context UriInfo uriInfo) {

		if (host == null) {
			return Response.status(400).build();
		}

		if (Strings.isNullOrEmpty(sessionToken)) {
			return Response.status(404).build();
		}

		ServiceResponse<Void> sr = hostService.addHost(host);

		if (sr.isRequestOk()) {
			return Response.created(
					UriBuilder.fromUri(uriInfo.getBaseUri()).path(getClass())
							.path(host.getHostname()).build()).build();
		} else {
			return Response.status(500).entity(sr.getMessagesAsStrings())
					.build();
		}
	}

	@DELETE
	@Path("/{hostname}")
	@ApiOperation(value = "Delete Host", notes = "Deletes a host object.  This operation requires the user to be logged in.")
	@ApiErrors(value = {
			@ApiError(code = 400, reason = "No host object value supplied"),
			@ApiError(code = 404, reason = "No session found"),
			@ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response deleteHostEntry(
			@ApiParam(value = "The Host object to add", required = true) @PathParam("hostname") String hostname,
			@ApiParam(value = "The authenticated session token", required = true) @HeaderParam("CICSTART.session") String sessionToken) {

		if (Strings.isNullOrEmpty(hostname)) {
			return Response.status(400).build();
		}

		if (Strings.isNullOrEmpty(sessionToken)) {
			return Response.status(404).build();
		}

		// whois also validates
		User requester = authClient.whois(sessionToken);

		if (requester.hasRole(Role.DATA_MANAGER)) {

			ServiceResponse<Host> sr = hostService.deleteHostEntry(hostname);

			if (sr.isRequestOk()) {
				return Response.ok(sr.getPayload()).build();
			} else {
				return Response.status(500).entity(sr.getMessagesAsStrings())
						.build();
			}
		} else {
			return Response.status(401)
					.entity("Only DATA_MANAGER roles are allowed to add hosts")
					.build();
		}

	}

	@GET
	@Path("/{hostname}/ls")
	@ApiOperation(value = "Directory listings", notes = "Performs a recursive 'ls' on the remote host.", responseClass = "ca.ualberta.physics.cssdp.domain.file.DirectoryListing")
	@ApiErrors(value = {
			@ApiError(code = 400, reason = "No hostname value supplied"),
			@ApiError(code = 400, reason = "No path to do ls on supplied"),
			@ApiError(code = 404, reason = "Host not registered for given hostname"),
			@ApiError(code = 404, reason = "No session found"),
			@ApiError(code = 204, reason = "No contents listed (remote host timed out, could not log into remote host, etc)"),
			@ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response ls(
			@ApiParam(value = "The hostname to do the ls on", required = true) @PathParam("hostname") String hostname,
			@ApiParam(value = "The path on the host to do the ls in", required = true) @QueryParam("path") String path,
			@ApiParam(value = "The number of directory levels to recurse", required = true) @QueryParam("depth") int depth,
			@ApiParam(value = "The authenticated session token", required = true) @HeaderParam("CICSTART.session") String sessionToken) {

		if (Strings.isNullOrEmpty(hostname)) {
			return Response.status(400).entity("no hostname").build();
		}
		if (Strings.isNullOrEmpty(path)) {
			return Response.status(400).entity("no path to ls").build();
		}

		authClient.validate(sessionToken);

		Host hostEntry = hostService.getHostEntry(hostname).getPayload();
		if (hostEntry == null) {
			return Response.status(404).entity(hostname + " not in registry")
					.build();
		}

		if (!remoteServers.contains(hostEntry)) {
			remoteServers.add(hostEntry);
		}

		RecursiveLs ls = new RecursiveLs(hostname, path, depth);
		remoteServers.requestOperation(ls);

		// TODO use a monitor instead of polling
		do {

			try {
				Thread.sleep(1000);
			} catch (InterruptedException ignore) {
			}

		} while (!ls.isDone());

		if (ls.hasError()) {
			return Response.status(500).entity(ls.getError()).build();
		}

		List<RemoteFile> result = ls.getResult();

		if (result == null || result.isEmpty()) {
			return Response.noContent().build();
		}

		return Response.ok(new DirectoryListing(result)).build();
	}

}
