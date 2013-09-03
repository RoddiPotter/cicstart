package ca.ualberta.physics.cicstart.cml.command;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForEach implements Command {

	private static final Logger jobLogger = LoggerFactory
			.getLogger("JOBLOGGER");

	private final String iterationVariable;
	private final Collection<?> collectionToIterate;
	private final List<CommandDefinition> cmdsToRun;

	public ForEach(String iterationVariable, Collection<?> collectionToIterate,
			List<CommandDefinition> cmdsToRun) {
		this.iterationVariable = iterationVariable;
		this.collectionToIterate = collectionToIterate;
		this.cmdsToRun = cmdsToRun;
	}

	@Override
	public void execute(CMLRuntime runtime) {
		jobLogger.info("ForEach: Iterating over " + collectionToIterate.size()
				+ " items");
		for (Object o : collectionToIterate) {
			each(runtime, o);
		}
	}

	protected void each(CMLRuntime runtime, Object o) {
		if (o instanceof File) {
			o = ((File) o).getName();
		}
		runtime.setVariableData(iterationVariable, o);
		jobLogger.info("ForEach: Iteration variable now set to " + o
				+ ", running commands in loop");
		runtime.run(getCmdsToRun());
	}

	@Override
	public Object getResult() {
		return null;
	}

	public List<CommandDefinition> getCmdsToRun() {
		return cmdsToRun;
	}

}
