package ca.ualberta.physics.cssdp.file.resource;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.wordnik.swagger.annotations.Api;

@Path("/host.xml")
@Api(value = "/host", description = "Operations about hosts")
@Produces({ MediaType.APPLICATION_XML })
public class HostResourceXML extends HostResource {

}
