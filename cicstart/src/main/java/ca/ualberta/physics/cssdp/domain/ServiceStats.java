package ca.ualberta.physics.cssdp.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import ca.ualberta.physics.cssdp.dao.Persistent;
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

	public enum ServiceName {
		AUTH, FILE, CATALOGUE, MACRO, VFS, STATS
	}

	private static final long serialVersionUID = 1L;

	@Column(name = "service_name", length = 10, nullable = false)
	@Enumerated(EnumType.STRING)
	private ServiceName serviceName;

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
		return serviceName.name();
	}

	@JsonIgnore
	public ServiceName getServiceName() {
		return serviceName;
	}

	public void setServiceName(ServiceName serviceName) {
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
