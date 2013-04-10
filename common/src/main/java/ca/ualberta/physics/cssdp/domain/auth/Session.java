package ca.ualberta.physics.cssdp.domain.auth;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import ca.ualberta.physics.cssdp.dao.Persistent;

@Entity
@Table(name = "auth_session")
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Session extends Persistent {

	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "token", length = 22, nullable = false)
	private String token;

	public Session() {
		
	}
	
	public Session(User user, String token) {
		this.user = user;
		this.token = token;
	}

	@Override
	public String _pk() {
		return token;
	}

	@XmlElement
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@XmlElement
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
