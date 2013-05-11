package ca.ualberta.physics.cicstart.cml.command;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import scala.actors.threadpool.Arrays;

public class TestCForEach {

	@Test
	@SuppressWarnings("unchecked")
	public void testCForEach() {

		final AtomicInteger ai = new AtomicInteger();

		CForEach forEach = new CForEach("file", Arrays.asList(new String[] {
				"1", "2", "3" }), Arrays.asList(new CommandDefinition[] {
				new CommandDefinition("debug(\"1\")", "debug")
						.addParameterName("debug 1"),
				new CommandDefinition("debug(\"2\")", "debug")
						.addParameterName("debug 2") }), true) {

			@Override
			protected void each(CMLRuntime runtime, Object o) {
				ai.incrementAndGet();
				super.each(runtime, o);
			}
		};

		forEach.execute(new CMLRuntime("session"));

		Assert.assertEquals(3, ai.get());

	}

	@Test
	@SuppressWarnings("unchecked")
	public void testForEach() {

		final AtomicInteger ai = new AtomicInteger();

		ForEach forEach = new ForEach("file", Arrays.asList(new Integer[] { 1,
				2, 3 }), Arrays.asList(new CommandDefinition[] {
				new CommandDefinition("debug(\"1\")", "debug")
						.addParameterName("debug 1"),
				new CommandDefinition("debug(\"2\")", "debug")
						.addParameterName("debug 2") })) {

			@Override
			protected void each(CMLRuntime runtime, Object o) {
				ai.incrementAndGet();
				super.each(runtime, o);
			}
		};

		forEach.execute(new CMLRuntime("session"));

		Assert.assertEquals(3, ai.get());

	}
}
