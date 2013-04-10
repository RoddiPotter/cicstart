package ca.ualberta.physics.cssdp.auth.resource;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.wordnik.swagger.annotations.Api;

@Path("/session.json")
@Api(value = "/session", description = "Operations about sessions")
@Produces({ MediaType.APPLICATION_JSON })
public class SessionResourceJSON extends SessionResource {

}
