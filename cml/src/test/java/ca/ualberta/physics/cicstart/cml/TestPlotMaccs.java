package ca.ualberta.physics.cicstart.cml;

import java.io.File;
import java.nio.charset.Charset;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ca.ualberta.physics.cicstart.cml.command.Macro;

import com.google.common.base.Joiner;
import com.google.common.io.Files;

public class TestPlotMaccs {

	private Macro macro;

	@Before
	public void setup() throws Exception {

		String script = Files.toString(new File(ParsingAndBuildingTests.class
				.getResource("/plot_maccs.cml").toURI()), Charset
				.forName("UTF-8"));
		ANTLRInputStream input = new ANTLRInputStream(script);

		CMLLexer lexer = new CMLLexer(input);

		CommonTokenStream tokens = new CommonTokenStream(lexer);

		CMLParser parser = new CMLParser(tokens);

		ParseTreeWalker walker = new ParseTreeWalker();

		macro = new Macro(script);

		ParseTree tree = parser.macro();

		walker.walk(macro, tree);

	}

	@Test
	public void testCommandOrder() {

		Assert.assertEquals(
				"on[getVFS, getCataloguedFiles, foreach[run, run, putVFS]]",
				Joiner.on(", ").join(macro.getCommands()));

	}

}
