package ca.ualberta.physics.cicstart.cml.command;

import org.junit.Assert;
import org.junit.Test;

public class TestRun {

	@Test
	public void testRun() {

		// spaces in command
		Run run = new Run("ls -l");
		run.execute(new CMLRuntime("session"));

		// lots of spaces in command
		run = new Run("gradle build -x test");
		run.execute(new CMLRuntime("session"));

		run = new Run("cat build.gradle");
		run.execute(new CMLRuntime("session"));

		run = new Run("grep plugin build.gradle");
		run.execute(new CMLRuntime("session"));
		System.out.println("exit value " + run.getResult());

		// this is a complex command with embedded quotes
		run = new Run("grep \"plugin: 'war'\" build.gradle");
		run.execute(new CMLRuntime("session"));
		Assert.assertEquals(0, run.getResult());
		run = new Run("cat build.gradle");
		run.execute(new CMLRuntime("session"));
		Assert.assertEquals(0, run.getResult());

		// timeout explicitly set
		run = new Run("wc -l build.gradle", 2);
		run.execute(new CMLRuntime("session"));

	}

}
