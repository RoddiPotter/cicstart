package ca.ualberta.physics.cicstart.cml.command;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class CForEach extends ForEach {

	private final boolean waitForIterationCompletion;
	private final CountDownLatch countdownLatch;

	public CForEach(String iterationVariable,
			Collection<?> collectionToIterate,
			List<CommandDefinition> cmdsToRun,
			boolean waitForIterationCompletion) {
		super(iterationVariable, collectionToIterate, cmdsToRun);
		this.waitForIterationCompletion = waitForIterationCompletion;
		this.countdownLatch = new CountDownLatch(collectionToIterate.size());
	}

	@Override
	public void execute(CMLRuntime runtime) {
		super.execute(runtime);
		try {
			if (waitForIterationCompletion) {
				countdownLatch.await();
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	@Override
	protected void each(final CMLRuntime runtime, final Object o) {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				CForEach.super.each(runtime, o);
				countdownLatch.countDown();
			}

		});
		t.start();
	}

	@Override
	public Object getResult() {
		return null;
	}

}
