package com.orbious.extractor;

/**
 * $Id: AcronymException.java 11 2009-12-04 14:07:11Z app $
 * <p>
 * An <code>AcroynmException</code> exception occurs when:
 * <ul>
 * <li>The <code>index</code> where the acronym matching begins is not 
 * punctuation.
 * 
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class AcronymException extends Exception {
	/**
	 * Version Identifier
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * 
	 * @param msg 	A message that identifies the <code>AcronymException</code>.
	 */
	public AcronymException(String msg) {
		super(msg);
	}
}
