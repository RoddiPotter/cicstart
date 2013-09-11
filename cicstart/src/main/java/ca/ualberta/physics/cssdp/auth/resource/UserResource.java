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

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cssdp.auth.service.UserService;
import ca.ualberta.physics.cssdp.configuration.InjectorHolder;
import ca.ualberta.physics.cssdp.domain.auth.Session;
import ca.ualberta.physics.cssdp.domain.auth.User;
import ca.ualberta.physics.cssdp.service.ServiceResponse;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Path("/auth/user")
@Api(value = "/auth/user", description = "Operations about Users")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class UserResource {

	private static final Logger logger = LoggerFactory
			.getLogger(UserResource.class);

	@Inject
	private UserService userService;

	public UserResource() {
		InjectorHolder.inject(this);
	}

	@POST
	@ApiOperation(value = "Add a User", notes = "User.email must be unique. "
			+ "HTTP status 201 returned if create was successful, use location header for object url. "
			+ "Note that an empty User object will result in odd jackson mapper error. ")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "No User object supplied"),
			@ApiResponse(code = 500, message = "Unable to complete request, see response body for error details") })
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
			+ "Passwords are all masked in responses.", response = ca.ualberta.physics.cssdp.domain.auth.User.class)
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "No email supplied"),
			@ApiResponse(code = 500, message = "Unable to complete request, see response body for error details") })
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
				user.setMasked(true);

				return Response.ok(user).build();
			}

		} else {

			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(sr.getMessagesAsStrings()).build();
		}

	}

	@Path("/{email}")
	@DELETE
	@ApiOperation(value = "Delete a User", notes = "Not implemented yet", response = ca.ualberta.physics.cssdp.domain.auth.User.class)
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "No email supplied"),
			@ApiResponse(code = 500, message = "Unable to complete request, see response body for error details") })
	public Response delete(
			@ApiParam(value = "Email used to lookup User object", required = true) @PathParam("email") String email) {
		return Response.status(500).entity("Not implemented yet").build();
	}

	@PUT
	@ApiOperation(value = "Modify a User", notes = "User.email must be unique.  Use get to retrieve the user id before calling this."
			+ "HTTP status 200 returned with user object if update was successful, use location header for object url. "
			+ "Note that an empty User object will result in odd jackson mapper error. ")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "No User object supplied."),
			@ApiResponse(code = 400, message = "user.id is null"),
			@ApiResponse(code = 400, message = "CICSTART.session is not for this user"),
			@ApiResponse(code = 404, message = "CICSTART.session is null"),
			@ApiResponse(code = 500, message = "Unable to complete request, see response body for error details") })
	public Response updateUser(
			@ApiParam(value = "User object to update", required = true) User user,
			@ApiParam(value = "The authenticated session token", required = true) @HeaderParam("CICSTART.session") String sessionToken,
			@Context UriInfo uriInfo) {

		if (Strings.isNullOrEmpty(sessionToken)) {
			return Response.status(404).build();
		}

		if (user == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}

		if (user.getId() == null) {
			return Response.status(Status.BAD_REQUEST).build();
		}

		ServiceResponse<Session> sessionSr = userService.locate(sessionToken);
		if (sessionSr.isRequestOk()) {

			Session session = sessionSr.getPayload();
			if (session.getUser().getId().equals(user.getId())) {
				ServiceResponse<User> sr = userService.update(user);

				if (sr.isRequestOk()) {

					User updatedUser = sr.getPayload();
					updatedUser.setMasked(true);
					return Response
							.ok(updatedUser)
							.header("location",
									UriBuilder.fromUri(uriInfo.getBaseUri())
											.path(getClass())
											.path(user.getEmail()).build())
							.build();

				} else {

					return Response.status(Status.INTERNAL_SERVER_ERROR)
							.entity(sr.getMessagesAsStrings()).build();

				}
			} else {
				return Response.status(Status.BAD_REQUEST).build();
			}
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(sessionSr.getMessagesAsStrings()).build();
		}
	}

	@Path("/requestpasswordreset")
	@POST
	@ApiOperation(value = "Requests a password reset email to be sent")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "No email supplied"),
			@ApiResponse(code = 500, message = "Unable to complete request, see response body for error details") })
	public Response requestResetPassword(
			@ApiParam(value = "Email used to lookup User object", required = true) @QueryParam("email") String email) {
		if (Strings.isNullOrEmpty(email)) {
			return Response.status(400).build();
		} else {
			ServiceResponse<String> sr = userService
					.requestPasswordReset(email);
			logger.debug("Reset token is : " + sr.getPayload());
			if (sr.isRequestOk()) {
				return Response.ok().entity("Check your email").build();
			} else {
				return Response.status(500).entity(sr.getMessagesAsStrings())
						.build();
			}
		}
	}

	@Path("/reset/{token}")
	@Produces({ MediaType.TEXT_HTML })
	@GET
	@ApiOperation(value = "Resets the password given.  If no password is given, an html form is returned for password entry.")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "No token supplied"),
			@ApiResponse(code = 400, message = "No email supplied"),
			@ApiResponse(code = 404, message = "Token expired"),
			@ApiResponse(code = 400, message = "Token and email don't match"),
			@ApiResponse(code = 400, message = "newpassword1 <> newpassword2"),
			@ApiResponse(code = 500, message = "Unable to complete request, see response body for error details") })
	public Response buildResetPasswordForm(
			@ApiParam(value = "Email used to lookup User object", required = true) @QueryParam("email") String email,
			@ApiParam(value = "Email used to lookup User object", required = true) @PathParam("token") String token,
			@Context UriInfo uriInfo) {

		if (Strings.isNullOrEmpty(email)) {
			return Response.status(400).build();
		} else if (Strings.isNullOrEmpty(token)) {
			return Response.status(400).build();
		} else {
			String formActionUri = uriInfo.getBaseUriBuilder()
					.path(getClass()).path("dopasswordreset").build()
					.toASCIIString();
			return Response.ok().type(MediaType.TEXT_HTML)
					.entity(buildResetForm(email, token, formActionUri))
					.build();

		}

	}

	@Path("/dopasswordreset")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@POST
	@ApiOperation(value = "Resets the password given.  If no password is given, an html form is returned for password entry.")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "No token supplied"),
			@ApiResponse(code = 400, message = "No email supplied"),
			@ApiResponse(code = 404, message = "Token expired"),
			@ApiResponse(code = 400, message = "Token and email don't match"),
			@ApiResponse(code = 400, message = "newpassword1 <> newpassword2"),
			@ApiResponse(code = 500, message = "Unable to complete request, see response body for error details") })
	public Response resetPassword(
			@ApiParam(value = "Email used to lookup User object", required = true) @FormParam("email") String email,
			@ApiParam(value = "Email used to lookup User object", required = true) @FormParam("token") String token,
			@ApiParam(value = "Email used to lookup User object", required = true) @FormParam("newpassword1") String password1,
			@ApiParam(value = "Email used to lookup User object", required = true) @FormParam("newpassword2") String password2,
			@Context UriInfo uriInfo) {

		if (Strings.isNullOrEmpty(email)) {
			return Response.status(400).build();
		} else if (Strings.isNullOrEmpty(token)) {
			return Response.status(400).build();
		} else if (Strings.isNullOrEmpty(password1)
				|| Strings.isNullOrEmpty(password2)) {
			return Response.status(400).build();
		} else {
			// process reset

			if (password1.equals(password2)) {

				ServiceResponse<Void> sr = userService.resetPassword(email,
						password1, token);

				if (sr.isRequestOk()) {
					return Response.ok().entity("Password reset").build();
				} else {
					return Response.status(500)
							.entity(sr.getMessagesAsStrings()).build();
				}

			} else {
				return Response.status(400).build();
			}

		}

	}

	private String buildResetForm(String email, String token,
			String formActionUri) {
		String html = null;
		Configuration cfg = new Configuration();
		cfg.setClassForTemplateLoading(getClass(), "");
		try {
			Template template = cfg.getTemplate("ResetPasswordForm.ftl");
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("action", formActionUri);
			data.put("email", email);
			data.put("token", token);
			Writer out = new StringWriter();
			template.process(data, out);
			html = out.toString();
		} catch (IOException e) {
			logger.error("Could not load template", e);
			Throwables.propagate(e);
		} catch (TemplateException e) {
			logger.error("Could populate template", e);
			Throwables.propagate(e);
		}
		return html;
	}

}
