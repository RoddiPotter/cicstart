package ca.ualberta.physics.cssdp.domain;

import org.joda.time.LocalDateTime;

import ca.ualberta.physics.cssdp.domain.ServiceStats.ServiceName;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

/**
 * A struct to hold service info for CANARIE service registry
 * 
 * @author rpotter
 * 
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class ServiceInfo {

	public ServiceName name;
	public String synopsis;
	public String version = "1.0";
	public String institution = "University of Alberta, Department of Physics, Space Physics";
	public LocalDateTime releaseTime = new LocalDateTime(2013, 06, 30, 01, 01);
	
}
