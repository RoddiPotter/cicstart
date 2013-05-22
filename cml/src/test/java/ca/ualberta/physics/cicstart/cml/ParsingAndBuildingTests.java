package ca.ualberta.physics.cicstart.cml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import scala.actors.threadpool.Arrays;
import ca.ualberta.physics.cicstart.cml.command.CMLRuntime;
import ca.ualberta.physics.cicstart.cml.command.Command;
import ca.ualberta.physics.cicstart.cml.command.CommandDefinition;
import ca.ualberta.physics.cicstart.cml.command.ForEach;
import ca.ualberta.physics.cicstart.cml.command.ForEachCommandDefinition;
import ca.ualberta.physics.cicstart.cml.command.GetCataloguedFiles;
import ca.ualberta.physics.cicstart.cml.command.GetVFS;
import ca.ualberta.physics.cicstart.cml.command.Macro;
import ca.ualberta.physics.cicstart.cml.command.On;
import ca.ualberta.physics.cicstart.cml.command.OnCommandDefinition;
import ca.ualberta.physics.cicstart.cml.command.PutVFS;
import ca.ualberta.physics.cicstart.cml.command.Run;

import com.google.common.base.Joiner;

public class ParsingAndBuildingTests {

	private Macro macro;
	private CMLRuntime runtime;

	@Before
	public void setup() throws IOException {
		ANTLRInputStream input = new ANTLRInputStream(
				ParsingAndBuildingTests.class.getResourceAsStream("/test.cml"));

		CMLLexer lexer = new CMLLexer(input);

		CommonTokenStream tokens = new CommonTokenStream(lexer);

		CMLParser parser = new CMLParser(tokens);

		ParseTreeWalker walker = new ParseTreeWalker();

		macro = new Macro();

		ParseTree tree = parser.macro();

		walker.walk(macro, tree);

		runtime = new CMLRuntime("testJob", "testSession");
	}

	@Test
	public void testCommandOrder() {

		Assert.assertEquals(
				"on[getVFS, getCataloguedFiles, foreach[run, run, putVFS]]",
				Joiner.on(", ").join(macro.getCommands()));

	}

	@Test
	public void testCommandDefinitions() {

		CommandDefinition cmdDef = macro.getCommands().get(0);
		Assert.assertEquals("on", cmdDef.getName());
		Assert.assertEquals("$localhost",
				((OnCommandDefinition) cmdDef).getServerVar());

		List<CommandDefinition> cmds = ((OnCommandDefinition) cmdDef)
				.getChildren();

		cmdDef = cmds.get(0);
		Assert.assertEquals("getVFS", cmdDef.getName());
		Assert.assertEquals("gnuPlotScript", cmdDef.getAssignment());
		Assert.assertEquals("$CICSTART.session", cmdDef.getParameterNames()
				.get(0));
		Assert.assertEquals("/maccs.gp", cmdDef.getParameterNames().get(1));

		cmdDef = cmds.get(1);
		Assert.assertEquals("getCataloguedFiles", cmdDef.getName());
		Assert.assertEquals("maccsData", cmdDef.getAssignment());
		Assert.assertEquals(Arrays.asList(new String[0]),
				cmdDef.getParameterNames());
		Assert.assertEquals("MACCS",
				cmdDef.getStructParameters("project").get(0));
		Assert.assertEquals(Arrays.asList(new String[] { "PG", "RNK" }),
				cmdDef.getStructParameters("observatories"));
		Assert.assertEquals(
				Arrays.asList(new String[] { "2010-01-01", "2011-01-01" }),
				cmdDef.getStructParameters("dateRange"));

		ForEachCommandDefinition forEachCmdDef = (ForEachCommandDefinition) cmds
				.get(2);
		Assert.assertEquals("foreach", forEachCmdDef.getName());
		Assert.assertEquals("file", forEachCmdDef.getIteratorVar());
		Assert.assertEquals("$maccsData", forEachCmdDef.getCollectionVar());
		Assert.assertEquals(3, forEachCmdDef.getChildren().size());

	}

	@Test
	public void testBuild() {

		CommandDefinition cmdDef = macro.getCommands().get(0);
		Command onCommand = runtime.buildCommand(cmdDef);
		Assert.assertTrue(onCommand instanceof On);

		List<CommandDefinition> cmds = ((OnCommandDefinition) cmdDef)
				.getChildren();
		cmdDef = cmds.get(0);
		Command command = runtime.buildCommand(cmdDef);
		Assert.assertTrue(command instanceof GetVFS);

		// simulates running GetVFS
		File gnuPlotScript = new File("gnuPlotScript.gp");
		runtime.setVariableData(cmdDef.getAssignment(), gnuPlotScript);

		cmdDef = cmds.get(1);
		command = runtime.buildCommand(cmdDef);
		Assert.assertTrue(command instanceof GetCataloguedFiles);

		// simulates running GetCataloguedFiles
		List<File> maccsData = new ArrayList<File>();
		File file1 = new File("file1") {
			private static final long serialVersionUID = 1L;

			@Override
			public String toString() {
				return super.getAbsolutePath();
			}
		};
		maccsData.add(file1);
		File file2 = new File("file2") {
			private static final long serialVersionUID = 1L;

			@Override
			public String toString() {
				return super.getAbsolutePath();
			}
		};
		maccsData.add(file2);
		runtime.setVariableData(cmdDef.getAssignment(), maccsData);

		cmdDef = cmds.get(2);
		command = runtime.buildCommand(cmdDef);
		Assert.assertTrue(command instanceof ForEach);

		// simulate first iteration
		runtime.setVariableData("file", file1.getAbsolutePath());

		List<CommandDefinition> cmdsToRun = ((ForEach) command).getCmdsToRun();

		cmdDef = cmdsToRun.get(0);
		command = runtime.buildCommand(cmdDef);
		Assert.assertTrue(command instanceof Run);
		Assert.assertEquals("gnuplot -e \"filename='" + file1.getAbsolutePath()
				+ "'\" gnuPlotScript.gp", ((Run) command).getCommandLine());

		cmdDef = cmdsToRun.get(1);
		command = runtime.buildCommand(cmdDef);
		Assert.assertTrue(command instanceof Run);
		Assert.assertEquals(
				"convert -density 300 -alpha off \"" + file1.getAbsolutePath()
						+ ".eps\" \"" + file1.getAbsolutePath() + ".png",
				((Run) command).getCommandLine());

		cmdDef = cmdsToRun.get(2);
		command = runtime.buildCommand(cmdDef);
		Assert.assertTrue(command instanceof PutVFS);
		Assert.assertEquals("/testJob", ((PutVFS) command).getDir());
		Assert.assertEquals(file1.getAbsolutePath() + ".png",
				((PutVFS) command).getFile());

		// simulate second iteration
		runtime.setVariableData("file", file2.getAbsolutePath());

		cmdDef = cmdsToRun.get(0);
		command = runtime.buildCommand(cmdDef);
		Assert.assertTrue(command instanceof Run);
		Assert.assertEquals("gnuplot -e \"filename='" + file2.getAbsolutePath()
				+ "'\" gnuPlotScript.gp", ((Run) command).getCommandLine());

		cmdDef = cmdsToRun.get(1);
		command = runtime.buildCommand(cmdDef);
		Assert.assertTrue(command instanceof Run);
		Assert.assertEquals(
				"convert -density 300 -alpha off \"" + file2.getAbsolutePath()
						+ ".eps\" \"" + file2.getAbsolutePath() + ".png",
				((Run) command).getCommandLine());

		cmdDef = cmdsToRun.get(2);
		command = runtime.buildCommand(cmdDef);
		Assert.assertTrue(command instanceof PutVFS);
		Assert.assertEquals("/testJob", ((PutVFS) command).getDir());
		Assert.assertEquals(file2.getAbsolutePath() + ".png",
				((PutVFS) command).getFile());

	}

}
