/* ============================================================
 * UserResource.java
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
package ca.ualberta.physics.cssdp.auth.resource;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import ca.ualberta.physics.cssdp.auth.InjectorHolder;
import ca.ualberta.physics.cssdp.auth.service.UserService;
import ca.ualberta.physics.cssdp.domain.ServiceStats.ServiceName;
import ca.ualberta.physics.cssdp.domain.auth.User;
import ca.ualberta.physics.cssdp.service.ServiceResponse;
import ca.ualberta.physics.cssdp.service.StatsService;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

public class UserResource {

	@Inject
	private UserService userService;

	@Inject
	private StatsService statsService;
	
	public UserResource() {
		InjectorHolder.inject(this);
		statsService.incrementInvocationCount(ServiceName.AUTH);
	}

	@POST
	@ApiOperation(value = "Add a User", notes = "User.email must be unique. "
			+ "HTTP status 201 returned if create was successful, use location header for object url. "
			+ "Note that an empty User object will result in odd jackson mapper error. ")
	@ApiErrors(value = {
			@ApiError(code = 400, reason = "No User object supplied"),
			@ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response addUser(
			@ApiParam(value = "User object to add", required = true) User user,
			@Context UriInfo uriInfo) {
		
		if (user == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}
		user.setDeleted(false);
		ServiceResponse<Void> sr = userService.create(user);

		if (sr.isRequestOk()) {

			return Response.created(
					UriBuilder.fromUri(uriInfo.getBaseUri()).path(getClass())
							.path(user.getEmail()).build()).build();

		} else {

			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(sr.getMessagesAsStrings()).build();

		}
	}

	@Path("/{email}")
	@GET
	@ApiOperation(value = "Get a User", notes = "If the user is not found or deleted, an empty response will be returned.  "
			+ "Passwords are all masked in responses.", responseClass = "ca.ualberta.physics.cssdp.domain.auth.User")
	@ApiErrors(value = {
			@ApiError(code = 400, reason = "No email supplied"),
			@ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response get(
			@ApiParam(value = "Email used to lookup User object", required = true) @PathParam("email") String email) {

		if (Strings.isNullOrEmpty(email)) {
			return Response.status(Status.BAD_REQUEST).build();
		}

		ServiceResponse<User> sr = userService.find(email);

		if (sr.isRequestOk()) {
			User user = sr.getPayload();
			if (user == null || user.isDeleted()) {

				return Response.ok().build();

			} else {

				// mask the password on the response.
				user.maskPassword();

				return Response.ok(user).build();
			}

		} else {

			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(sr.getMessagesAsStrings()).build();
		}

	}

	@Path("/{email}")
	@DELETE
	@ApiOperation(value = "Delete a User", notes = "Not implemented yet", responseClass = "ca.ualberta.physics.cssdp.domain.auth.User")
	@ApiErrors(value = {
			@ApiError(code = 400, reason = "No email supplied"),
			@ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response delete(
			@ApiParam(value = "Email used to lookup User object", required = true) @PathParam("email") String email) {
		return Response.status(500).entity("Not implemented yet").build();
	}
}
