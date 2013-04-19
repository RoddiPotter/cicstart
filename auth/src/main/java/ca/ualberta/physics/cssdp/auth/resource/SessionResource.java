/* ============================================================
 * SessionResource.java
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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cssdp.auth.InjectorHolder;
import ca.ualberta.physics.cssdp.auth.service.UserService;
import ca.ualberta.physics.cssdp.domain.auth.Session;
import ca.ualberta.physics.cssdp.service.ServiceResponse;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.sun.jersey.core.util.Base64;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

public class SessionResource {

	private static final Logger logger = LoggerFactory
			.getLogger(SessionResource.class);

	@Inject
	private UserService userService;

	public SessionResource() {
		InjectorHolder.inject(this);
	}

	/**
	 * This method allows for the user to use a http BASIC authentication
	 * mechanism, or a simple form submission, depending on their capabilities
	 * (or their libraries capabilities)
	 * 
	 * @param username
	 * @param password
	 * @param headers
	 * @param request
	 * @return
	 */
	@POST
	@ApiOperation(value = "Authenticate", notes = "Creates a new session and whitelists your ip address for 48 hours.  "
			+ "You can use HTTP BASIC or simple form based authentication; both are available at this end point.  "
			+ "Note the server will not request BASIC so you must always send the authentication header.  "
			+ "Returns a session token to use for future requests.  "
			+ "curl --user datamanager@nowhere.com:password -X POST http://localhost:8081/auth/session.json", responseClass = "java.lang.String")
	@ApiErrors(value = {
			@ApiError(code = 400, reason = "No email value supplied"),
			@ApiError(code = 400, reason = "No password value supplied"),
			@ApiError(code = 404, reason = "Invalid credentials - see http://stackoverflow.com/questions/9220432/http-401-unauthorized-or-403-forbidden-for-a-disabled-user to avoid information leakage."),
			@ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response authenticate(
			@ApiParam(value = "The username (email address) to authenticate with.  "
					+ "Required if using form authentication.") @FormParam("username") String username,
			@ApiParam(value = "The password to authenticate with.  Required if using form authentication.") @FormParam("password") String password,
			@Context HttpHeaders headers, @Context HttpServletRequest request) {

		// parse the username and password from the authorization string
		List<String> authHeaders = headers.getRequestHeader("authorization");
		if (authHeaders != null) {
			String authorizationHeader = authHeaders.get(0);
			if (!Strings.isNullOrEmpty(authorizationHeader)) {
				authorizationHeader = authorizationHeader.substring("Basic "
						.length());
				String[] creds = new String(
						Base64.base64Decode(authorizationHeader)).split(":");
				username = creds[0];
				password = creds[1];
			}
		}

		if (Strings.isNullOrEmpty(username) || Strings.isNullOrEmpty(password)) {
			return Response.status(400).build();
		}

		// capture the ip address of the client
		String requestIp = null;
		String forwardLine = request.getHeader("X-Forwarded-For");
		if (!Strings.isNullOrEmpty(forwardLine)) {
			requestIp = Splitter.on(",").split(forwardLine).iterator().next();
		}
		requestIp = Strings.isNullOrEmpty(requestIp) ? request.getRemoteAddr()
				: requestIp;

		logger.info(username + " is attempting to authenticate from "
				+ requestIp);
		logger.trace(username + "/" + password);

		if (username == null) {
			return Response.status(400).build();
		}

		// authenticate against the user table
		ServiceResponse<Session> sr = userService.authenticate(username,
				password, requestIp);

		if (sr.isRequestOk()) {

			return Response.ok(sr.getPayload().getToken()).build();

		} else {
			return Response.status(404).build();
		}

	}

	@Path("/{token}/whois")
	@GET
	@ApiOperation(value = "Get session token owner", notes = "Used by internal services to locate the user "
			+ "registered to this session token", responseClass = "ca.ualberta.physics.cssdp.domain.auth.User")
	@ApiErrors(value = {
			@ApiError(code = 400, reason = "No token given"),
			@ApiError(code = 404, reason = "No owner found for given token"),
			@ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response whoIs(
			@ApiParam(value = "The token to lookup the email address for", required = true) @PathParam("token") String sessionToken) {

		if (Strings.isNullOrEmpty(sessionToken)) {
			return Response.status(400).build();
		}

		ServiceResponse<Session> sr = userService.locate(sessionToken);
		if (sr.isRequestOk()) {
			Session session = sr.getPayload();
			if (session != null) {
				return Response.ok(session.getUser().maskPassword()).build();
			} else {
				return Response.status(404).build();
			}
		} else {
			return Response.status(500).entity(sr.getMessagesAsStrings())
					.build();
		}

	}

	// @Path("/{token}")
	// @GET
	// public Response getSessionActivity(@QueryParam("email") String email,
	// @QueryParam("ip") String userIpAddress,
	// @Context HttpServletRequest request) {
	//
	// if (email == null) {
	// return Response
	// .status(Status.BAD_REQUEST)
	// .header("cssdp-auth-errors", "An email address is required")
	// .build();
	// }
	//
	// String requestIp = request.getRemoteAddr();
	//
	// if (privilegedIps.contains(requestIp)) {
	// String ipAddress = ipWhiteList.getIfPresent(email);
	// if (ipAddress != null) {
	// if (ipAddress.equals(userIpAddress)) {
	// return Response.ok().build();
	// }
	// }
	// }
	//


}
