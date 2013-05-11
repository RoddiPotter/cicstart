package ca.ualberta.physics.cicstart.cml.command;

import static ch.qos.logback.classic.ClassicConstants.FINALIZE_SESSION_MARKER;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.NotImplementedException;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import ca.ualberta.physics.cssdp.domain.catalogue.CatalogueSearchRequest;
import ca.ualberta.physics.cssdp.model.Mnemonic;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

public class CMLRuntime {
	private static final Logger cmlLogger = LoggerFactory
			.getLogger("JOBLOGGER");
	private static final Logger logger = LoggerFactory
			.getLogger(CMLRuntime.class);

	private static final String CICSTARTSESSION = "CICSTART.session";
	private static final String JOBID = "JOBID";

	private final Map<String, Object> variableData = new HashMap<String, Object>();

	public CMLRuntime(String cicstartSession) {
		// set globals
		variableData.put(JOBID, newJobId());
		variableData.put(CICSTARTSESSION, cicstartSession);

	}

	protected String newJobId() {
		String jobId = UUID.randomUUID().toString();
		MDC.put("jobId", jobId);
		return jobId;
	}

	public void run(Collection<CommandDefinition> cmdDefs) {
		cmlLogger.info("Running commands: " + Joiner.on(", ").join(cmdDefs));
		for (CommandDefinition cmdDef : cmdDefs) {
			run(cmdDef);
		}
	}

	public void run(CommandDefinition cmdDef) {
		logger.info("building " + cmdDef.toString());
		Command cmd = buildCommand(cmdDef);
		logger.info("running " + cmd.toString());
		cmd.execute(this);
		Object result = cmd.getResult();
		String variableName = cmdDef.getAssignment();

		if (result != null && !Strings.isNullOrEmpty(variableName)) {
			variableData.put(variableName, result);
		}
		logger.info(FINALIZE_SESSION_MARKER, "About to end the job");
	}

	private <T> T mutate(String value, Class<T> clazz) {

		T t = null;
		if (value.contains("$")) {

			if (value.contains("$" + JOBID)) {
				value = value.replaceAll("\\$" + JOBID, variableData.get(JOBID)
						.toString());
				t = (T) value;
			} else if (value.contains("$" + CICSTARTSESSION)) {
				value = value.replaceAll("\\$" + CICSTARTSESSION, variableData
						.get(CICSTARTSESSION).toString());
				t = (T) value;
			} else {
				for (String variableName : variableData.keySet()) {
					/*
					 * have to iterate over variables to translate things like
					 * $file.png where we want just the $file part translated,
					 * otherwise we won't find the variable in the variabledata
					 * map.
					 */
					if (value.contains("$" + variableName)) {
						Object varData = variableData.get(variableName);
						if (clazz.equals(String.class)) {
							value = value.replaceAll("\\$" + variableName,
									varData.toString());
							t = (T) value;
						} else {
							// do this for variables that start with $
							t = (T) varData;
						}
					}
				}
			}
		} else {
			t = (T) value;
		}

		return t;

	}

	public Command buildCommand(CommandDefinition commandDef) {
		String name = commandDef.getName();

		if (name.equals("debug")) {
			DebugCmd cmd = new DebugCmd(commandDef.getParameterNames().get(0));
			return cmd;
		}

		if (name.equals("getCataloguedFiles")) {

			CatalogueSearchRequest searchRequest = new CatalogueSearchRequest();

			List<String> projects = commandDef.getStructParameters("project");
			if (projects.size() > 0) {
				searchRequest.setProjectKey(Mnemonic.of(projects.get(0)));
			}

			searchRequest.setObservatoryKeys(Mnemonic.listOf(commandDef
					.getStructParameters("obseravtories")));

			searchRequest.setInstrumentTypeKeys(Mnemonic.listOf(commandDef
					.getStructParameters("instrumentTypes")));

			List<String> discriminators = commandDef
					.getStructParameters("discriminator");
			if (discriminators.size() > 0) {
				searchRequest.setDiscriminatorKey(Mnemonic.of(discriminators
						.get(0)));
			}

			List<String> dateRange = commandDef
					.getStructParameters("dateRange");
			if (dateRange.size() > 0) {
				String start = dateRange.get(0);
				if (!Strings.isNullOrEmpty(start)) {
					searchRequest.setStart(LocalDateTime.parse(start));
				}
				String end = dateRange.get(1);
				if (!Strings.isNullOrEmpty(end)) {
					searchRequest.setEnd(LocalDateTime.parse(end));
				}
			}

			return new GetCataloguedFiles(searchRequest);
		}

		if (name.equals("cforeach")) {
			throw new NotImplementedException(
					"Have to factor out shared mutable state before this can work");
			// ForEachCommandDefinition forEachCmdDef =
			// (ForEachCommandDefinition) commandDef;
			// CForEach forEach = new CForEach(forEachCmdDef.getIteratorVar(),
			// mutate(forEachCmdDef.getCollectionVar(), Collection.class),
			// forEachCmdDef.getChildren(), forEachCmdDef.getWaitFlag());
			//
			// return forEach;

		}

		if (name.equals("foreach")) {
			ForEachCommandDefinition forEachCmdDef = (ForEachCommandDefinition) commandDef;
			ForEach forEach = new ForEach(forEachCmdDef.getIteratorVar(),
					mutate(forEachCmdDef.getCollectionVar(), Collection.class),
					forEachCmdDef.getChildren());

			return forEach;

		}

		if (name.equals("getVFS")) {
			String sessionVarName = commandDef.getParameterNames().get(0);
			String path = commandDef.getParameterNames().get(1);
			return new GetVFS(mutate(sessionVarName, String.class), mutate(
					path, String.class));
		}

		if (name.equals("putVFS")) {
			String sessionVarName = commandDef.getParameterNames().get(0);
			String dir = commandDef.getParameterNames().get(1);
			String file = commandDef.getParameterNames().get(2);
			return new PutVFS(mutate(sessionVarName, String.class), mutate(dir,
					String.class), mutate(file, String.class));
		}

		if (name.equals("run")) {
			String commandLine = commandDef.getParameterNames().get(0);
			String timeout = null;
			if (commandDef.getParameterNames().size() == 2) {
				timeout = commandDef.getParameterNames().get(1);
			}
			if (timeout != null) {
				return new Run(mutate(commandLine, String.class),
						Integer.parseInt(mutate(timeout, String.class)));
			} else {
				return new Run(mutate(commandLine, String.class));
			}
		}

		throw new IllegalArgumentException(
				"Don't know how to build a command with name " + name);
	}

	public void setVariableData(String variableName, Object value) {
		this.variableData.put(variableName, value);
	}

	public String getJobId() {
		return (String) variableData.get(JOBID);
	}

}
