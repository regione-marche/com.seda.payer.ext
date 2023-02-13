/**
 * 
 */
package com.seda.payer.ext.util;

public class SedaExtException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8559375647370938983L;

	/**
	 * @param message
	 */
	public SedaExtException(String message) {
		super("EXT: " + message);
	}

	/**
	 * @param cause
	 */
	public SedaExtException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SedaExtException(String message, Throwable cause) {
		super("EXT: " + message, cause);
	}

}
