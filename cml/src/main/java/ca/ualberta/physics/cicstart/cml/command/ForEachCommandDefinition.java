package ca.ualberta.physics.cicstart.cml.command;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

public class ForEachCommandDefinition extends CommandDefinition {

	private String iteratorVar;
	private String collectionVar;
	private final boolean waitFlag;

	private final List<CommandDefinition> children = new ArrayList<CommandDefinition>();

	public ForEachCommandDefinition(String signature, String name,
			boolean waitFlag) {
		super(signature, name);
		this.waitFlag = waitFlag;
	}

	public String getIteratorVar() {
		return iteratorVar;
	}

	public void setIteratorVar(String iteratorVar) {
		this.iteratorVar = iteratorVar;
	}

	public String getCollectionVar() {
		return collectionVar;
	}

	public void setCollectionVar(String collectionVar) {
		this.collectionVar = collectionVar;
	}

	public void addChild(CommandDefinition child) {
		children.add(child);
	}

	@Override
	public String toString() {
		return getName() + "[" + Joiner.on(", ").join(children) + "]";
	}

	public List<CommandDefinition> getChildren() {
		return ImmutableList.copyOf(children);
	}

	public boolean getWaitFlag() {
		return waitFlag;
	}
}
