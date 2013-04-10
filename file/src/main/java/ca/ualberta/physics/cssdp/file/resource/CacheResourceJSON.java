package ca.ualberta.physics.cssdp.file.resource;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.wordnik.swagger.annotations.Api;

@Path("/cache.json")
@Api(value = "/cache", description = "Operations about the file cache")
@Produces({ MediaType.APPLICATION_JSON })
public class CacheResourceJSON extends CacheResource {

}
