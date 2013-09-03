package ca.ualberta.physics.cssdp.resource;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cssdp.domain.ServiceInfo;
import ca.ualberta.physics.cssdp.domain.ServiceStats;

import com.google.common.base.Throwables;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;
import com.wordnik.swagger.annotations.ApiOperation;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Consumes(MediaType.WILDCARD)
@Produces(MediaType.WILDCARD)
public abstract class AbstractServiceResource {

	private static final Logger logger = LoggerFactory
			.getLogger(AbstractServiceResource.class);

	protected abstract ServiceInfo buildInfo();

	protected abstract ServiceStats buildStats();

	protected abstract URI getDocURI() throws URISyntaxException;

	@Path("/info")
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
	@ApiOperation(value = "Basic identification and provenance information about the service.", notes = "CANARIE's monitoring service will poll this URI periodically.", responseClass = "ca.ualberta.physics.cssdp.domain.ServiceInfo")
	@ApiErrors(value = { @ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response getInfo(@Context HttpHeaders headers) {
		for (MediaType type : headers.getAcceptableMediaTypes()) {
			if (type.equals(MediaType.APPLICATION_JSON_TYPE)) {
				return Response.ok(buildInfo()).build();
			} else if (type.equals(MediaType.TEXT_HTML_TYPE)) {
				return Response.ok(toHtmlString(buildInfo())).build();
			} else {
				break;
			}
		}
		return Response.status(406).entity("Accept header is not json or html")
				.build();
	}

	@Path("/stats")
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_HTML })
	@ApiOperation(value = "CANARIE's monitoring service will poll this URI periodically. Return information about the usage of this RPI.", notes = "If this URI fails or times out, this RPI is unavailable.", responseClass = "ca.ualberta.physics.cssdp.domain.ServiceStats")
	@ApiErrors(value = { @ApiError(code = 500, reason = "Unable to complete request, see response body for error details") })
	public Response getStats(@Context HttpHeaders headers) {
		for (MediaType type : headers.getAcceptableMediaTypes()) {
			if (type.equals(MediaType.APPLICATION_JSON_TYPE)) {
				return Response.ok(buildStats()).build();
			} else if (type.equals(MediaType.TEXT_HTML_TYPE)) {
				return Response.ok(toHtmlString(buildStats())).build();
			} else {
				break;
			}
		}
		return Response.status(406).entity("Accept header is not json or html")
				.build();
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

	private String toHtmlString(ServiceStats stats) {
		String html = null;
		Configuration cfg = new Configuration();
		cfg.setClassForTemplateLoading(AbstractServiceResource.class, "");
		try {
			Template template = cfg.getTemplate("ServiceStats.ftl");
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("stats", stats);
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

	private String toHtmlString(ServiceInfo info) {
		String html = null;
		Configuration cfg = new Configuration();
		cfg.setClassForTemplateLoading(AbstractServiceResource.class, "");
		try {
			Template template = cfg.getTemplate("ServiceInfo.ftl");
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("info", info);
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
