package ca.ualberta.physics.cicstart.cml.command;

import com.google.common.base.Joiner;

public class OnCommandDefinition extends NestedCommandDefinition {

	private final String macroScript;
	private final String serverVar;

	public OnCommandDefinition(String signature, String name, String serverVar, String macroScript) {
		super(signature, name);
		this.serverVar = serverVar;
		this.macroScript = macroScript;
	}

	public String getServerVar() {
		return serverVar;
	}

	@Override
	public String toString() {
		return getName() + "[" + Joiner.on(", ").join(getChildren()) + "]";
	}

	public String getMacroScript() {
		return macroScript;
	}

}
