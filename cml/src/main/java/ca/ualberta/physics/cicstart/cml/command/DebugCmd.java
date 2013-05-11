package ca.ualberta.physics.cicstart.cml.command;


public class DebugCmd implements Command {

	private final String msg;
	
	
	public DebugCmd(String msg) {
		this.msg = msg;
	}
	
	@Override
	public void execute(CMLRuntime runtime) {
		System.out.println("DEBUG: " + msg);
	}

	@Override
	public Object getResult() {
		return null;
	}

}
