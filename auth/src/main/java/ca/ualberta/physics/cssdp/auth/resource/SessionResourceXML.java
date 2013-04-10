package ca.ualberta.physics.cssdp.auth.resource;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.wordnik.swagger.annotations.Api;

@Path("/session.xml")
@Api(value = "/session", description = "Operations about sessions")
@Produces({ MediaType.APPLICATION_XML })
public class SessionResourceXML extends SessionResource {

}
