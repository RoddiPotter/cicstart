package ca.ualberta.physics.cssdp.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cssdp.dao.Persistent;
import ca.ualberta.physics.cssdp.service.StatsService;
import ca.ualberta.physics.cssdp.util.JSONDateTimeNoMillisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * a domain object used to hold service statistics
 * 
 * @author rpotter
 * 
 */
@Entity
@Table(name = "service_stats")
@JsonAutoDetect(getterVisibility = Visibility.PUBLIC_ONLY)
public class ServiceStats extends Persistent implements Serializable {

	private static final Logger logger = LoggerFactory.getLogger(ServiceStats.class);
	
	private static final long serialVersionUID = 1L;

	@Column(name = "service_name", length = 10, nullable = false)
	@Enumerated(EnumType.STRING)
	private StatsService.ServiceName serviceName;

	@Column(name = "invocations", nullable = false)
	private int invocations;

	@Column(name = "reset_date", nullable = false)
	@Type(type = "ca.ualberta.physics.cssdp.dao.type.PersistentDateTime")
	private DateTime lastReset;

	// overridden to set jsonignore
	@JsonIgnore
	@Override
	public Long getId() {
		return super.getId();
	}

	// overridden to set jsonignore
	@JsonIgnore
	@Override
	public int getVersion() {
		return super.getVersion();
	}

	@Override
	public String _pk() {
		logger.debug("service name in domain object is " + serviceName);
		return serviceName == null ? getId() + "" : serviceName.name();
	}

	@JsonIgnore
	public StatsService.ServiceName getServiceName() {
		return serviceName;
	}

	public void setServiceName(StatsService.ServiceName serviceName) {
		this.serviceName = serviceName;
	}

	public int getInvocations() {
		return invocations;
	}

	public void setInvocations(int invocations) {
		this.invocations = invocations;
	}

	@JsonSerialize(using = JSONDateTimeNoMillisSerializer.class)
	public DateTime getLastReset() {
		return lastReset;
	}

	public void setLastReset(DateTime lastReset) {
		this.lastReset = lastReset;
	}

	public void incrementInvocations() {
		setInvocations(getInvocations() + 1);
	}

	public String toHtmlString() {
		return null;
	}

}
