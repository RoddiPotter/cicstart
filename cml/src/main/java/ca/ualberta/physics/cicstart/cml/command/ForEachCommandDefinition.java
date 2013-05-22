package ca.ualberta.physics.cicstart.cml.command;

import com.google.common.base.Joiner;

public class ForEachCommandDefinition extends NestedCommandDefinition {

	private String iteratorVar;
	private String collectionVar;
	private final boolean waitFlag;

	public ForEachCommandDefinition(String signature, String name, boolean waitFlag) {
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

	@Override
	public String toString() {
		return getName() + "[" + Joiner.on(", ").join(getChildren()) + "]";
	}

	public boolean getWaitFlag() {
		return isWaitFlag();
	}

	public boolean isWaitFlag() {
		return waitFlag;
	}

}
