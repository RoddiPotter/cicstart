package ca.ualberta.physics.cssdp.resource;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ca.ualberta.physics.cssdp.domain.ServiceInfo;
import ca.ualberta.physics.cssdp.domain.ServiceStats;
import ca.ualberta.physics.cssdp.domain.ServiceStats.ServiceName;
import ca.ualberta.physics.cssdp.service.StatsService;

import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;

@Consumes(MediaType.WILDCARD)
public abstract class AbstractServiceResource {

	protected abstract ServiceInfo buildInfo();

	protected abstract ServiceStats buildStats();

	protected abstract URI getDocURI() throws URISyntaxException;

	@Inject
	private StatsService statsService;

	@Path("/info")
	@GET
	@ApiOperation(value = "Basic identification and provenance information about the service.", notes = "CANARIE's monitoring service will poll this URI periodically.", responseClass = "ca.ualberta.physics.cssdp.domain.ServiceInfo")
	@ApiErrors(value = { @ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response getInfo() {
		return Response.ok(buildInfo()).build();
	}

	@Path("/stats")
	@GET
	@ApiOperation(value = "CANARIE's monitoring service will poll this URI periodically. Return information about the usage of this RPI.", notes = "If this URI fails or times out, this RPI is unavailable.", responseClass = "ca.ualberta.physics.cssdp.domain.ServiceStats")
	@ApiErrors(value = { @ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response getStats() {
		// keep track of how many times CANARIE is polling the service
		statsService.incrementInvocationCount(ServiceName.STATS);
		return Response.ok(buildStats()).build();
	}

	@Path("/doc")
	@GET
	@ApiOperation(value = "Returns an HTTP redirect to the service documention on github")
	@ApiErrors(value = { @ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response getDoc() {
		try {
			return Response.seeOther(getDocURI()).build();
		} catch (URISyntaxException e) {
			return Response.serverError()
					.entity(Throwables.getStackTraceAsString(e)).build();
		}

	}

	@Path("/releasenotes")
	@GET
	@ApiOperation(value = "Returns an HTTP redirect to the release notes documention on github")
	@ApiErrors(value = { @ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response getReleaseNotes() {
		try {
			return Response
					.seeOther(
							new URI(
									"https://github.com/RoddiPotter/cicstart/wiki/releasenotes"))
					.build();
		} catch (URISyntaxException e) {
			return Response.serverError()
					.entity(Throwables.getStackTraceAsString(e)).build();
		}
	}

	@Path("/support")
	@GET
	@ApiOperation(value = "Returns an HTTP redirect to the support documention on github")
	@ApiErrors(value = { @ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response getSupport() {
		try {
			return Response
					.seeOther(
							new URI(
									"https://github.com/RoddiPotter/cicstart/wiki/support"))
					.build();
		} catch (URISyntaxException e) {
			return Response.serverError()
					.entity(Throwables.getStackTraceAsString(e)).build();
		}
	}

	@Path("/source")
	@GET
	@ApiOperation(value = "Returns an HTTP redirect to the source on github")
	@ApiErrors(value = { @ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response getSource() {
		try {
			return Response.seeOther(
					new URI("https://github.com/RoddiPotter/cicstart")).build();
		} catch (URISyntaxException e) {
			return Response.serverError()
					.entity(Throwables.getStackTraceAsString(e)).build();
		}
	}

	@Path("/tryme")
	@GET
	@ApiOperation(value = "Not implemented")
	@ApiErrors(value = { @ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response getTryMe() {
		return Response.noContent().build();
	}
}
