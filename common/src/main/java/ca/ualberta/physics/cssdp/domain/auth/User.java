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
import com.wordnik.swagger.annotations.ApiProperty;

@Entity
@Table(name = "auth_user")
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class User extends Persistent implements Serializable {

	private static final long serialVersionUID = 1L;

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

	@ApiProperty(required = true, notes = "max length 100")
	@XmlElement
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@ApiProperty(required = true, notes = "max length 100")
	@XmlElement
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ApiProperty(required = true)
	@XmlElement
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@ApiProperty(required = false, notes = "max length 100")
	@XmlElement
	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	@ApiProperty(required = false, notes = "max length 100")
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
		return passwordDigest;
	}

	public void setPasswordDigest(String passwordDigest) {
		this.passwordDigest = passwordDigest;
	}

	@JsonIgnore
	@XmlTransient
	public String getPasswordSalt() {
		return passwordSalt;
	}

	public void setPasswordSalt(String passwordSalt) {
		this.passwordSalt = passwordSalt;
	}

	@XmlElement
	@ApiProperty(allowableValues = "ROOT, DATA_MANAGER, DATA_USER", required = false, notes = "default value is DATA_USER")
	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public User maskPassword() {
		setPassword("******");
		return this;
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

	@ApiProperty(required = false, notes = "The system only logically deletes entries.  This value is ignored on creates and updates.")
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
		setPassword(user.getPassword());
		setPasswordDigest(user.getPasswordDigest());
		setPasswordSalt(user.getPasswordSalt());
		setRole(user.getRole());
	}
}
