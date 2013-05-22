package ca.ualberta.physics.cicstart.cml.command;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

public class NestedCommandDefinition extends CommandDefinition {

	private final List<CommandDefinition> children = new ArrayList<CommandDefinition>();
	
	public NestedCommandDefinition(String signature, String name) {
		super(signature, name);
	}

	public void addChild(CommandDefinition child) {
		children.add(child);
	}

	public List<CommandDefinition> getChildren() {
		return ImmutableList.copyOf(children);
	}
}
