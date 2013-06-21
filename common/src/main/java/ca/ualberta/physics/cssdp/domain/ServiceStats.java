package ca.ualberta.physics.cssdp.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

import ca.ualberta.physics.cssdp.dao.Persistent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * a domain object used to hold service statistics
 * 
 * @author rpotter
 * 
 */
@Entity
@Table(name = "service_stats")
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class ServiceStats extends Persistent implements Serializable {

	public enum ServiceName {
		AUTH, FILE, CATALOGUE, MACRO, VFS, STATS
	}

	private static final long serialVersionUID = 1L;

	@JsonIgnore
	@Column(name = "service_name", length = 10, nullable = false)
	@Enumerated(EnumType.STRING)
	private ServiceName serviceName;

	@Column(name = "invocations", nullable = false)
	private int invocations;

	@Column(name = "reset_date", nullable = false)
	@Type(type = "ca.ualberta.physics.cssdp.dao.type.PersistentLocalDateTime")
	private LocalDateTime lastReset;

	@Override
	public String _pk() {
		return serviceName.name();
	}

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

	public LocalDateTime getLastReset() {
		return lastReset;
	}

	public void setLastReset(LocalDateTime lastReset) {
		this.lastReset = lastReset;
	}

	public void incrementInvocations() {
		this.invocations++;
	}

}
