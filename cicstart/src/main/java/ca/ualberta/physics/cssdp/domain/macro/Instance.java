package ca.ualberta.physics.cssdp.domain.macro;

import com.google.common.base.Objects;

public class Instance {

	public String cloudName;
	public String ipAddress;
	public String href;
	public String id;

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("ipAddress", ipAddress)
				.add("href", href).add("id", id).toString();
	}

}