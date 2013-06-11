package ca.ualberta.physics.cicstart.cml.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

public class CommandDefinition {

	private final String signature;
	private final String name;
	private final Set<String> parameterNames = Sets.newLinkedHashSet();
	private String assignment;

	private Map<String, List<String>> struct = new HashMap<String, List<String>>();

	public CommandDefinition(String signature, String name) {
		this.signature = signature;
		this.name = name;
	}

	public CommandDefinition addParameterName(String parameterName) {
		parameterNames.add(parameterName.replaceAll("^\"", "")
				.replaceAll("\"$", "").replaceAll("\\\\", ""));
		return this;
	}

	private String _pk() {
		return signature;
	}

	@Override
	public boolean equals(Object obj) {
		return _pk().equals(((CommandDefinition) obj)._pk());
	}

	@Override
	public int hashCode() {
		return _pk().hashCode();
	}

	@Override
	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public String getSignature() {
		return signature;
	}

	public List<String> getParameterNames() {
		return ImmutableList.copyOf(parameterNames);
	}

	public String getAssignment() {
		return assignment;
	}

	public void setAssignment(String assignment) {
		this.assignment = assignment;
	}

	public void addStructParameter(String name, String value) {
		List<String> values = struct.get(name);
		if (values == null) {
			values = new ArrayList<String>();
		}
		values.add(value.replaceAll("^\"", "").replaceAll("\"$", ""));
		struct.put(name, values);
	}

	public List<String> getStructParameters(String key) {
		List<String> params = struct.get(key);
		if (params == null) {
			params = new ArrayList<String>();
		}
		return params;
	}

	
}
