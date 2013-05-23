package ca.ualberta.physics.cicstart.cml;

import java.io.IOException;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ca.ualberta.physics.cicstart.cml.command.CMLRuntime;
import ca.ualberta.physics.cicstart.cml.command.Macro;

import com.google.common.base.Joiner;

public class TestPlotMaccs {

	private Macro macro;
	private CMLRuntime runtime;

	@Before
	public void setup() throws IOException {
		ANTLRInputStream input = new ANTLRInputStream(
				TestPlotMaccs.class.getResourceAsStream("/plot_maccs.cml"));

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

}
