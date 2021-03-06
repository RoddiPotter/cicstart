/* ============================================================
 * User.java
 * ============================================================
 * Copyright 2013 University of Alberta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============================================================ 
 */
package ca.ualberta.physics.cssdp.domain.auth;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import ca.ualberta.physics.cssdp.dao.Persistent;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;
import com.wordnik.swagger.annotations.ApiModelProperty;

@Entity
@Table(name = "auth_user")
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class User extends Persistent implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String MASK = "****";

	/*
	 * The ordinal value of the role is used to determine which roles are more
	 * authoritative than others: oder is important in this list! This means
	 * ROOT has more authority than DATA_MANAGER which has more authority than
	 * DATA_USER and so on.
	 */
	public static enum Role {
		ROOT, DATA_MANAGER, DATA_USER
	}

	/*
	 * The users email address
	 */
	@Column(name = "email", length = 100, nullable = false)
	private String email;

	/*
	 * The users name
	 */
	@Column(name = "name", length = 100, nullable = false)
	private String name;

	/*
	 * A hash of the users password
	 */
	@Column(name = "password_digest", length = 32, nullable = false)
	private String passwordDigest;

	/*
	 * A salt used in hashing the password
	 */
	@Column(name = "password_salt", length = 32, nullable = false)
	private String passwordSalt;

	/*
	 * The users password, which is not persisted.
	 */
	@Transient
	private String password;

	/*
	 * The institution the user belongs to
	 */
	@Column(name = "institution", length = 100, nullable = true)
	private String institution;

	/*
	 * The role this user has
	 */
	@Column(name = "role")
	@Enumerated(value = EnumType.STRING)
	private Role role;

	@Column(name = "deleted", nullable = false)
	private boolean deleted = false;

	/*
	 * The country the user is in
	 */
	@Column(name = "country", length = 100, nullable = true)
	private String country;

	// TODO this should be a collection of cloud credentials (a map by cloud id)

	/*
	 * The users open stack username.
	 */
	@Column(name = "openstack_username", length = 100, nullable = true)
	private String openStackUsername;

	/*
	 * The users open stack password.
	 */
	@Column(name = "openstack_password", length = 100, nullable = true)
	private String openStackPassword;

	@Column(name = "openstack_keyname", length = 100, nullable = true)
	private String keyname;

	/*
	 * Set to true if the sensitive data should be masked.
	 */
	@Transient
	private transient boolean masked = false;

	public User() {
		// establish defaults
		deleted = false;
		role = Role.DATA_USER;
	}

	@Override
	public String _pk() {
		return email;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("email", email)
				.add("name", name).toString();
	}

	@ApiModelProperty(required = true, notes = "max length 100")
	@XmlElement
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@ApiModelProperty(required = true, notes = "max length 100")
	@XmlElement
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ApiModelProperty(required = true)
	@XmlElement
	public String getPassword() {
		if (!masked) {
			return password;
		} else {
			return MASK;
		}

	}

	public void setPassword(String password) {
		this.password = password;
	}

	@ApiModelProperty(required = false, notes = "max length 100")
	@XmlElement
	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	@ApiModelProperty(required = false, notes = "max length 100")
	@XmlElement
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@JsonIgnore
	@XmlTransient
	public String getPasswordDigest() {
		if (!masked) {
			return passwordDigest;
		} else {
			return MASK;
		}

	}

	public void setPasswordDigest(String passwordDigest) {
		this.passwordDigest = passwordDigest;
	}

	@JsonIgnore
	@XmlTransient
	public String getPasswordSalt() {
		if (!masked) {
			return passwordSalt;
		} else {
			return MASK;
		}

	}

	public void setPasswordSalt(String passwordSalt) {
		this.passwordSalt = passwordSalt;
	}

	@XmlElement
	@ApiModelProperty(allowableValues = "ROOT, DATA_MANAGER, DATA_USER", required = false, notes = "default value is DATA_USER")
	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	/**
	 * Does this user have the role given? This takes into account implied
	 * roles. If this user is a DATA_MANAGER they also have the implied roles of
	 * DATA_USER.
	 * 
	 * @param roleRequired
	 * @return false if they don't or true if they do
	 */
	public boolean hasRole(Role role) {

		// the javadoc indicates compareTo uses the ordinal value, which is what
		// we want.
		int compareResult = getRole().compareTo(role);

		// if the result is negative or zero, then we have the role we are
		// looking for.
		boolean hasRole = compareResult <= 0;

		return hasRole;
	}

	@ApiModelProperty(required = false, notes = "The system only logically deletes entries.  This value is ignored on creates and updates.")
	@XmlElement
	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean d) {
		this.deleted = d;
	}

	public void replaceWith(User user) {
		setCountry(user.getCountry());
		setDeleted(user.isDeleted());
		setEmail(user.getEmail());
		setInstitution(user.getInstitution());
		setName(user.getName());
		// these don't get set by us
		// setPassword(user.getPassword());
		// setPasswordDigest(user.getPasswordDigest());
		// setPasswordSalt(user.getPasswordSalt());
		setRole(user.getRole());
	}

	@ApiModelProperty(required = false, notes = "The username for creating open stack resources")
	@XmlElement
	public String getOpenStackUsername() {
		if (!masked) {
			return openStackUsername;
		} else {
			return MASK;
		}

	}

	public void setOpenStackUsername(String openStackUsername) {
		this.openStackUsername = openStackUsername;
	}

	@ApiModelProperty(required = false, notes = "The open stack username password")
	@XmlElement
	public String getOpenStackPassword() {
		if (!masked) {
			return openStackPassword;
		} else {
			return MASK;
		}
	}

	public void setOpenStackPassword(String openStackPassword) {
		this.openStackPassword = openStackPassword;
	}

	@ApiModelProperty(required = false, notes = "The name of the public/private keypair you want injected into the instance")
	@XmlElement
	public String getKeyname() {
		return keyname;
	}

	public void setKeyname(String keyname) {
		this.keyname = keyname;
	}

	@JsonIgnore
	@XmlTransient
	public boolean isMasked() {
		return masked;
	}

	public void setMasked(boolean mask) {
		this.masked = mask;
	}
}
