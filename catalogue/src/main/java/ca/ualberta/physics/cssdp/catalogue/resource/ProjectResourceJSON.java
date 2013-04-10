package ca.ualberta.physics.cssdp.catalogue.resource;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.wordnik.swagger.annotations.Api;

@Path("/project.json")
@Api(value = "/project", description = "Operations about Projects")
@Produces({ MediaType.APPLICATION_JSON })
public class ProjectResourceJSON extends ProjectResource {

}
