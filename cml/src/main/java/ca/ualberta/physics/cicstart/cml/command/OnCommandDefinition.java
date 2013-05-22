package ca.ualberta.physics.cicstart.cml.command;

import com.google.common.base.Joiner;

public class OnCommandDefinition extends NestedCommandDefinition {

	private String serverVar;

	public OnCommandDefinition(String signature, String name, String serverVar) {
		super(signature, name);
		this.serverVar = serverVar;
	}

	public String getServerVar() {
		return serverVar;
	}

	@Override
	public String toString() {
		return getName() + "[" + Joiner.on(", ").join(getChildren()) + "]";
	}

}
