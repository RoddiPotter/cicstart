package ca.ualberta.physics.cssdp.auth.resource;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.wordnik.swagger.annotations.Api;

@Path("/user.xml")
@Api(value = "/user", description = "Operations about Users")
@Produces({ "application/xml" })
public class UserResourceXML extends UserResource {
}
