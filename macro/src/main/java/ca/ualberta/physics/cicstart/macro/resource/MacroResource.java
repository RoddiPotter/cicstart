/* ============================================================
 * MacroResource.java
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
package ca.ualberta.physics.cicstart.macro.resource;

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.compress.utils.IOUtils;

import ca.ualberta.physics.cicstart.macro.InjectorHolder;
import ca.ualberta.physics.cicstart.macro.service.CloudService;
import ca.ualberta.physics.cicstart.macro.service.Image;
import ca.ualberta.physics.cicstart.macro.service.MacroService;
import ca.ualberta.physics.cicstart.macro.service.MacroService.JobStatus;
import ca.ualberta.physics.cicstart.macro.service.OpenStackCloud.Flavor;
import ca.ualberta.physics.cssdp.configuration.Common;
import ca.ualberta.physics.cssdp.domain.ServiceStats.ServiceName;
import ca.ualberta.physics.cssdp.domain.auth.User;
import ca.ualberta.physics.cssdp.domain.macro.Instance;
import ca.ualberta.physics.cssdp.domain.macro.InstanceSpec;
import ca.ualberta.physics.cssdp.service.ServiceResponse;
import ca.ualberta.physics.cssdp.service.StatsService;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

public class MacroResource {

	@Inject
	private MacroService macroService;

	@Inject
	private CloudService cloudService;
	
	@Inject
	private StatsService statsService;

	public MacroResource() {
		InjectorHolder.inject(this);
		statsService.incrementInvocationCount(ServiceName.AUTH);
	}

	@Path("/run")
	@POST
	@ApiOperation(value = "Run a macro", notes = "Run the given macro.  HTTP 201 is returned along with a "
			+ "location header which can be used to access status and logs")
	@ApiErrors(value = {
			@ApiError(code = 400, reason = "No CML script supplied"),
			@ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response runScript(
			@ApiParam(value = "Script to run", required = true) String cmlScript,
			@ApiParam(value = "The authenticated session token", required = true) @HeaderParam("CICSTART.session") String sessionToken,
			@Context UriInfo uriInfo) {

		if (Strings.isNullOrEmpty(cmlScript)) {
			return Response.status(400).build();
		}

		ServiceResponse<String> sr = macroService.run(cmlScript, sessionToken);
		if (sr.isRequestOk()) {
			String jobId = sr.getPayload();
			return Response
					.status(201)
					.location(
							UriBuilder.fromUri(uriInfo.getBaseUri())
									.path(getClass()).path(jobId)
									.path("status").build()).build();
		} else {
			return Response.status(500).entity(sr.getMessagesAsStrings())
					.build();
		}
	}

	@Path("/{requestId}/status")
	@GET
	@ApiOperation(value = "Get the status of this request", notes = "The status is either queued, running, or stopped.", responseClass = "java.lang.String")
	@ApiErrors(value = {
			@ApiError(code = 400, reason = "No request id supplied"),
			@ApiError(code = 404, reason = "Request not found"),
			@ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response getStatus(
			@ApiParam(value = "The request id", required = true) @PathParam("requestId") String requestId,
			@Context UriInfo uriInfo) {

		ServiceResponse<JobStatus> sr = macroService.getStatus(requestId);

		if (sr.isRequestOk()) {
			JobStatus status = sr.getPayload();
			if (status.equals(JobStatus.RUNNING)) {
				return Response
						.status(Status.SEE_OTHER)
						.entity(status.name())
						.location(
								UriBuilder.fromUri(uriInfo.getBaseUri())
										.path(getClass()).path(requestId)
										.path("log/tail").build()).build();
			} else if (status.equals(JobStatus.PENDING)) {
				return Response
						.status(Status.SEE_OTHER)
						.entity(status.name())
						.location(
								UriBuilder.fromUri(uriInfo.getBaseUri())
										.path(getClass()).path(requestId)
										.path("status").build()).build();

			} else {
				return Response
						.status(Status.SEE_OTHER)
						.entity(status.name())
						.location(
								UriBuilder.fromUri(uriInfo.getBaseUri())
										.path(getClass()).path(requestId)
										.path("log").build()).build();

			}
		} else {
			return Response.status(500).entity(sr.getMessagesAsStrings())
					.build();
		}

	}

	@Path("/{requestId}/log/tail")
	@GET
	@Produces({ "text/plain" })
	@ApiOperation(value = "Tail the log file", notes = "Just like the command version using the -f option 'tail -f'", responseClass = "java.lang.String")
	@ApiErrors(value = {
			@ApiError(code = 400, reason = "No request id supplied"),
			@ApiError(code = 404, reason = "Request not found"),
			@ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public StreamingOutput tailLog(
			@ApiParam(value = "The request id", required = true) @PathParam("requestId") final String requestId) {

		return new StreamingOutput() {
			public void write(OutputStream output) throws IOException,
					WebApplicationException {
				try {
					macroService.connectToLogStream(requestId, output);
				} catch (Exception e) {
					throw new WebApplicationException(e);
				}
			}
		};

	}

	@Path("/{requestId}/log")
	@GET
	@ApiOperation(value = "Download the entire log file. Makes a request to your VFS to get the log file.")
	@ApiErrors(value = {
			@ApiError(code = 400, reason = "No request id supplied"),
			@ApiError(code = 404, reason = "Request not found"),
			@ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public StreamingOutput getLog(
			@ApiParam(value = "The request id", required = true) @PathParam("requestId") final String requestId,
			/*
			 * @ApiParam(value = "compress", required = false, allowableValues =
			 * "gz, zip, none") @QueryParam("compress") String compressionType,
			 */
			@ApiParam(value = "The authenticated session token", required = true) @HeaderParam("CICSTART.session") final String sessionToken) {

		String authResource = Common.properties().getString("auth.api.url");
		String whoisUrl = authResource + "/session.json/{session}/whois";
		final User user = get(whoisUrl, sessionToken).as(User.class);
		String vfsResource = Common.properties().getString("vfs.api.url");

		final String readUrl = vfsResource + "/filesystem.json/{owner}/read";

		// FIXME this does nothing.
		return new StreamingOutput() {
			public void write(OutputStream output) throws IOException,
					WebApplicationException {
				try {
					given().header("CICSTART.session", sessionToken)
							.get(readUrl, user.getId())
							.path(requestId + "/macro.log");
				} catch (Exception e) {
					throw new WebApplicationException(e);
				}
			}
		};

	}

	@Path("/{requestId}/log")
	@PUT
	@ApiOperation(value = "Append to the log file", notes = "This is normally called by the binary running the macro.")
	@ApiErrors(value = {
			@ApiError(code = 400, reason = "No request id supplied"),
			@ApiError(code = 404, reason = "Request not found"),
			@ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response appendLog(
			@ApiParam(value = "The request id", required = true) @PathParam("requestId") String requestId,
			@ApiParam(value = "value", required = true) @QueryParam("value") String value) {

		ServiceResponse<Void> sr = macroService.writeToLogBuffer(requestId,
				value);
		if (sr.isRequestOk()) {
			return Response.status(200).build();
		} else {
			return Response.status(404).build();
		}

	}

	@Path("/bin")
	@POST
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@ApiOperation(value = "Download the macro binary that would run on the server", notes = "optionally contains an embedded JRE if "
			+ "none are available on the resource running the macro")
	@ApiErrors(value = {
			@ApiError(code = 400, reason = "No request id supplied"),
			@ApiError(code = 404, reason = "Request not found"),
			@ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response getMacroBinaryClient(
			@ApiParam(value = "Script to run", required = true) byte[] cmlScript,
			@ApiParam(value = "The authenticated session token", required = true) @HeaderParam("CICSTART.session") String sessionToken,
			@ApiParam(value = "Include embedded JRE?", required = false, defaultValue = "false") @QueryParam("include_jre") boolean includeJre,
			@ApiParam(value = "Client running same network as CICSTART server?", required = false, defaultValue = "false") @QueryParam("use_internal_network") boolean useInternalNetwork,
			@ApiParam(value = "An existing job id if known", required = false) @QueryParam("job_id") String jobId,
			@Context final HttpServletResponse response) {

		ServiceResponse<File> sr = macroService.assembleClient(new String(
				cmlScript, Charset.forName("UTF-8")), sessionToken, includeJre,
				useInternalNetwork, jobId);
		if (sr.isRequestOk()) {

			File clientBinary = sr.getPayload();
			response.setHeader("Content-Disposition", "attachment; "
					+ "filename=\"" + clientBinary.getName() + "\"");
			response.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			try {

				IOUtils.copy(new FileInputStream(clientBinary),
						response.getOutputStream());

			} catch (IOException e) {
				return Response.status(500)
						.entity(Throwables.getStackTraceAsString(e)).build();

			} finally {
				clientBinary.delete();

			}
			return Response.ok().build();
		} else {
			return Response.status(500).entity(sr.getMessagesAsStrings())
					.build();
		}
	}

	@Path("/vm")
	@POST
	@ApiOperation(value = "starts a remote VM defined by a macro script", notes = "")
	@ApiErrors(value = {
			@ApiError(code = 400, reason = "No request id supplied"),
			@ApiError(code = 404, reason = "Request not found"),
			@ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response startVM(
			@ApiParam(value = "The instance specification to use when starting the VM", required = true) InstanceSpec instanceSpec,
			@ApiParam(value = "The authenticated session token", required = true) @HeaderParam("CICSTART.session") String sessionToken) {

		String cloudName = instanceSpec.getCloud();
		String jobId = instanceSpec.getRequestId();
		String imageName = instanceSpec.getImage();
		String flavorName = instanceSpec.getFlavor();

		Image theImage = null;
		for (Image image : cloudService.getImages(cloudName, sessionToken)
				.getPayload()) {

			if (image.name.equals(imageName)) {
				theImage = image;
				break;
			}

		}

		if (theImage == null) {
			return Response.status(400).entity("No image named " + imageName)
					.build();
		}

		Flavor flavor = null;
		try {
			flavor = Flavor.valueOf(flavorName.replaceAll("\\.", "_"));
		} catch (IllegalArgumentException e) {
			return Response.status(400).entity("No flavor named " + flavorName)
					.build();

		}

		ServiceResponse<Instance> sr = cloudService.startInstance(cloudName,
				theImage, flavor, sessionToken, jobId);

		if (sr.isRequestOk()) {
			return Response.ok(sr.getPayload()).build();
		} else {
			return Response.status(500).entity(sr.getMessagesAsStrings())
					.build();
		}

	}

	@Path("/vm")
	@DELETE
	@ApiOperation(value = "stops a remote VM", notes = "")
	@ApiErrors(value = {
			@ApiError(code = 400, reason = "No request id supplied"),
			@ApiError(code = 404, reason = "Request not found"),
			@ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response stopVM(
			@ApiParam(value = "The instance to stop", required = true) Instance instance,
			@ApiParam(value = "The authenticated session token", required = true) @HeaderParam("CICSTART.session") String sessionToken) {

		ServiceResponse<Void> sr = cloudService.stopInstance(instance,
				sessionToken);

		if (sr.isRequestOk()) {
			return Response.ok(sr.getPayload()).build();
		} else {
			return Response.status(500).entity(sr.getMessagesAsStrings())
					.build();
		}

	}

}
