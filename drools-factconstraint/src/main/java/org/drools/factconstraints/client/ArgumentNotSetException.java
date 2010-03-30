package org.drools.factconstraints.client;

/**
 *
 * @author esteban.aliverti@gmail.com
 */
public class ArgumentNotSetException extends Exception {

	private static final long serialVersionUID = 501L;

	public ArgumentNotSetException() {
		super();
	}

	public ArgumentNotSetException(String message, Throwable cause) {
		super(message, cause);
	}

	public ArgumentNotSetException(String message) {
		super(message);
	}

	public ArgumentNotSetException(Throwable cause) {
		super(cause);
	}

}
