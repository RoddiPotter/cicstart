/* ============================================================
 * ProjectResource.java
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

package ca.ualberta.physics.cssdp.catalogue.resource;

import java.net.URI;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cssdp.auth.service.AuthClient;
import ca.ualberta.physics.cssdp.catalogue.service.CatalogueService;
import ca.ualberta.physics.cssdp.configuration.InjectorHolder;
import ca.ualberta.physics.cssdp.domain.catalogue.CatalogueSearchRequest;
import ca.ualberta.physics.cssdp.domain.catalogue.CatalogueSearchResponse;
import ca.ualberta.physics.cssdp.domain.catalogue.DataProduct;
import ca.ualberta.physics.cssdp.domain.catalogue.Project;
import ca.ualberta.physics.cssdp.jaxb.Link;
import ca.ualberta.physics.cssdp.service.ServiceResponse;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/catalogue/project")
@Api(value = "/catalogue/project", description = "Operations about Projects")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class ProjectResource {

	private final Logger logger = LoggerFactory
			.getLogger(ProjectResource.class);

	@Inject
	private CatalogueService catalogueService;

	@Inject
	private AuthClient authClient;

	public ProjectResource() {
		InjectorHolder.inject(this);
	}

	@POST
	@ApiOperation(value = "Create a new Project object", notes = "Successful operation will respond with 201 and a location header to get the contents of the Project object")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "No Project object supplied"),
			@ApiResponse(code = 500, message = "Unable to complete request, see response body for error details") })
	public Response createProject(
			@ApiParam(value = "The Project data", required = true) Project project,
			@Context UriInfo uriInfo) {

		for (DataProduct dp : project.getDataProducts()) {
			try {
				dp.afterJsonUmarshal(project);
			} catch (IllegalStateException e) {
				return Response.status(400).entity(e.getMessage()).build();
			}
		}
		logger.debug("Project is " + project);

		ServiceResponse<Void> sr = catalogueService.create(project);

		if (sr.isRequestOk()) {
			return Response
					.status(201)
					.location(
							UriBuilder.fromUri(uriInfo.getAbsolutePath())
									.path(project.getExternalKey().getValue())
									.build()).build();
		} else {
			return Response.status(500).entity(sr.getMessagesAsStrings())
					.build();
		}

	}

	@DELETE
	@Path("/{extKey}")
	@ApiOperation(value = "Delete a Project object", notes = "Successful operation will respond with 200 and return the Project object deleted", response = ca.ualberta.physics.cssdp.domain.catalogue.Project.class)
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "No Project key supplied"),
			@ApiResponse(code = 404, message = "No Project found for key supplied"),
			@ApiResponse(code = 500, message = "Unable to complete request, see response body for error details") })
	public Response delete(
			@ApiParam(value = "The Project key", required = true) @PathParam("extKey") String extKey) {

		if (Strings.isNullOrEmpty(extKey)) {
			return Response.status(400).build();
		}

		ServiceResponse<Project> projectSr = catalogueService.find(extKey);

		if (projectSr.isRequestOk()) {

			Project project = projectSr.getPayload();
			if (project != null) {

				ServiceResponse<Void> sr = catalogueService.delete(project);
				if (sr.isRequestOk()) {
					return Response.ok(project).build();
				} else {
					return Response.status(500)
							.entity(sr.getMessagesAsStrings()).build();
				}
			} else {
				return Response.status(404)
						.entity("Project for key " + extKey + " not found")
						.build();
			}
		} else {
			return Response.status(500)
					.entity(projectSr.getMessagesAsStrings()).build();
		}

	}

	@GET
	@ApiOperation(value = "List all projects", notes = "Returns an object with links to project details.", response = ca.ualberta.physics.cssdp.catalogue.resource.ProjectLinks.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Unable to complete request, see response body for error details") })
	public Response getProjects(@Context UriInfo uriInfo) {

		ServiceResponse<List<Project>> sr = catalogueService.findAll();

		ProjectLinks projects = new ProjectLinks();
		for (Project p : sr.getPayload()) {
			URI uri = uriInfo.getBaseUriBuilder().path(getClass())
					.path(p.getExternalKey().getValue()).build();
			projects.addLink(new Link(p.getName(), uri));
		}

		return Response.ok(projects).build();
	}

	@GET
	@Path("/{extKey}")
	@ApiOperation(value = "Get Project details", notes = "Get the project details", response = ca.ualberta.physics.cssdp.domain.catalogue.Project.class)
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "No Project key supplied"),
			@ApiResponse(code = 404, message = "No Project found for key supplied"),
			@ApiResponse(code = 500, message = "Unable to complete request, see response body for error details") })
	public Response getProject(
			@ApiParam(value = "The Project key", required = true) @PathParam("extKey") String extKey) {

		if (Strings.isNullOrEmpty(extKey)) {
			return Response.status(400).build();
		}

		ServiceResponse<Project> sr = catalogueService.find(extKey);

		if (sr.isRequestOk()) {
			Project project = sr.getPayload();
			if (project == null) {
				return Response.status(404).build();
			} else {
				return Response.ok(project).build();
			}
		} else {
			return Response.status(500).entity(sr.getMessagesAsStrings())
					.build();
		}

	}

	@PUT
	@Path("/{extKey}/scan")
	@ApiOperation(value = "Request a directory scan and mapping of the project host", notes = "This is an asynchronous request")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "No Project key supplied"),
			@ApiResponse(code = 404, message = "No Project found for key supplied"),
			@ApiResponse(code = 404, message = "No session found"),
			@ApiResponse(code = 500, message = "Unable to complete request, see response body for error details") })
	public Response scanForDataProductMapping(
			@ApiParam(value = "The Project key", required = true) @PathParam("extKey") String extKey,
			@ApiParam(value = "The authenticated session token", required = true) @HeaderParam("CICSTART.session") String sessionToken,
			@Context UriInfo uriInfo) {

		authClient.validate(sessionToken);

		ServiceResponse<Project> projectSr = catalogueService.find(extKey);

		if (projectSr.isRequestOk()) {
			Project project = projectSr.getPayload();
			if (project == null) {
				return Response.status(404).build();
			} else {
				ServiceResponse<Void> sr = catalogueService.scan(project,
						sessionToken);
				if (sr.isRequestOk()) {
					return Response.status(202).build();
				} else {
					return Response.status(500)
							.entity(sr.getMessagesAsStrings()).build();
				}
			}
		} else {
			return Response.status(500)
					.entity(projectSr.getMessagesAsStrings()).build();
		}

	}

	@POST
	@Path("/find")
	@ApiOperation(value = "Search the catalogue", notes = "")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "No CatalogueSearchRequest supplied"),
			@ApiResponse(code = 500, message = "Unable to complete request, see response body for error details") })
	public Response query(
			@ApiParam(value = "The search criteria", required = true) CatalogueSearchRequest searchRequest) {

		if (searchRequest == null) {
			return Response.status(400).build();
		}

		ServiceResponse<List<URI>> results = catalogueService.find(
				searchRequest.getProjectKey(),
				searchRequest.getObservatoryKeys(),
				searchRequest.getInstrumentTypeKeys(),
				searchRequest.getDiscriminatorKey(), searchRequest.getStart(),
				searchRequest.getEnd());

		if (results.isRequestOk()) {
			// TODO return a summary and request the actual results.

			CatalogueSearchResponse searchResponse = new CatalogueSearchResponse();
			List<URI> uris = results.getPayload();
			if (uris != null) {
				searchResponse.getUris().addAll(uris);
			}
			return Response.ok(searchResponse).build();

		} else {
			return Response.status(500).entity(results.getMessagesAsStrings())
					.build();
		}
	}

}
