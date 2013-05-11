package ca.ualberta.physics.cicstart.cml.command;



public interface Command {

	public void execute(CMLRuntime runtime);
	public Object getResult();
	
}
