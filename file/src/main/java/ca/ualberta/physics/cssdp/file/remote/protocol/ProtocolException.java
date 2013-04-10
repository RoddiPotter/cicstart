package ca.ualberta.physics.cssdp.file.remote.protocol;



public final class ProtocolException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private final boolean retryable;
	
	public ProtocolException(String message, boolean retryable) {
		super(message);
		this.retryable = retryable;
	}
	
	public ProtocolException(String message, boolean retryable, Exception cause) {
		super(message, cause);
		this.retryable = retryable;
	}

	public boolean canRetry() {
		return retryable;
	}
}
