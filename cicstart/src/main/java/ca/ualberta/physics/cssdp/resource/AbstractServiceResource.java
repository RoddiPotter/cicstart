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

import ca.ualberta.physics.cssdp.configuration.Common;
import ca.ualberta.physics.cssdp.domain.ServiceInfo;
import ca.ualberta.physics.cssdp.domain.ServiceStats;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

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
	@ApiOperation(value = "Basic identification and provenance information about the service.", notes = "CANARIE's monitoring service will poll this URI periodically.", response = ca.ualberta.physics.cssdp.domain.ServiceInfo.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Unable to complete request, see response body for error details") })
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
	@ApiOperation(value = "CANARIE's monitoring service will poll this URI periodically. Return information about the usage of this RPI.", notes = "If this URI fails or times out, this RPI is unavailable.", response = ca.ualberta.physics.cssdp.domain.ServiceStats.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Unable to complete request, see response body for error details") })
	public Response getStats(@Context HttpHeaders headers) {
		for (MediaType type : headers.getAcceptableMediaTypes()) {
			ServiceStats jsonStats = buildStats();
			if (type.equals(MediaType.APPLICATION_JSON_TYPE)) {
				if (jsonStats == null) {
					logger.error("no stats to return");
				}
				return Response.ok(jsonStats)/*
											 * .header("Content-Type",
											 * "application/json")
											 */.build();
			} else if (type.equals(MediaType.TEXT_HTML_TYPE)) {
				String htmlStats = toHtmlString(jsonStats);
				return Response.ok(htmlStats)/*
											 * .header("Content-Type",
											 * "text/html")
											 */.build();
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
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Unable to complete request, see response body for error details") })
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
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Unable to complete request, see response body for error details") })
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
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Unable to complete request, see response body for error details") })
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
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Unable to complete request, see response body for error details") })
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
	@ApiOperation(value = "Returns an HTTP redirect to the live generated API docs that include some 'try-me' actions")
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Unable to complete request, see response body for error details") })
	public Response getTryMe() {
		try {
			String docsUrl = Common.properties().getString("doc.url");
			String apiUrl = Common.properties().getString("api.url");
			String renderedApiDocs = docsUrl + "?input_baseUrl=" + apiUrl;
			return Response.seeOther(new URI(renderedApiDocs)).build();
		} catch (URISyntaxException e) {
			return Response.serverError()
					.entity(Throwables.getStackTraceAsString(e)).build();
		}
	}

	@Path("/licence")
	@GET
	@ApiOperation(value = "Returns an HTTP redirect to the licence file on Github")
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Unable to complete request, see response body for error details") })
	public Response getLicence() {
		try {
			return Response
					.seeOther(
							new URI(
									"https://github.com/RoddiPotter/cicstart/blob/master/LICENCE"))
					.build();
		} catch (URISyntaxException e) {
			return Response.serverError()
					.entity(Throwables.getStackTraceAsString(e)).build();
		}
	}

	@Path("/provenance")
	@GET
	@ApiOperation(value = "Returns an HTTP redirect to the provenance file on Github")
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Unable to complete request, see response body for error details") })
	public Response getProvenance() {
		try {
			return Response
					.seeOther(
							new URI(
									"https://github.com/RoddiPotter/cicstart/wiki/Provenance"))
					.build();
		} catch (URISyntaxException e) {
			return Response.serverError()
					.entity(Throwables.getStackTraceAsString(e)).build();
		}
	}

	private String toHtmlString(ServiceStats stats) {
		if (stats == null) {
			throw new IllegalArgumentException(
					"ServiceStats object can not be null");
		}
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
			data.put("tags", Joiner.on(",").join(info.getTags()));
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
