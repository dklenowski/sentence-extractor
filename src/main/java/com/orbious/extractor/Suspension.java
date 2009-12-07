package com.orbious.extractor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import org.apache.log4j.Logger;

/**
 * $Id: Suspension.java 12 2009-12-05 11:40:44Z app $
 * <p>
 * Suspension is a static class used to determine if words are suspension.
 * A suspension is a word that is distinct from traditional words in that:
 * <ul>
 * <li>It can be capitalized and not be a sentence start.
 * <li>It can be terminated with a fullstop.
 * </ul>
 * 
 * This class is usefull to determine valid sentence start's and end's.
 * 
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class Suspension {

	/**
	 * In memory list of suspension extracted from 
	 * {@link Config#SUSPENSION_FILENAME}.
	 */
	private static HashSet<String> suspensions;
	
	/**
	 * Private Constructor.
	 */
	private Suspension() { }

	/**
	 * Checks to see whether a word is a suspension 
	 * e.g. 'Mr.', 'Amer.', 'Am.' are suspensions.
	 * 
	 * @param wd	A word.
	 * @return		Returns <code>true</code> if the word is a suspension, 
	 * 				<code>false</code> otherwise.
	 */
	public static boolean isSuspension(String wd) {
		if ( suspensions == null ) {
			init();
		}
		
		if ( suspensions.contains(wd) ) {
			return(true);
		}
		
		return(false);
	}
	
	/**
	 * Parses the {@link Config#SUSPENSION_FILENAME} into memory.
	 */
	private static void init() {
		Logger logger;
		BufferedReader br = null;
		
		logger = Logger.getLogger(Config.LOGGER_REALM);
		try {
			br = new BufferedReader(
					new FileReader(Config.SUSPENSION_FILENAME));
		} catch ( FileNotFoundException fnfe ) {
			logger.fatal("Failed to open suspension file " + 
					Config.SUSPENSION_FILENAME, fnfe);
		}
		
		suspensions = new HashSet<String>();

		try {
			String wd;
			while ( (wd = br.readLine()) != null ) {
				suspensions.add(wd);
			}
		} catch ( IOException ioe ) {
			logger.fatal("Failed to read suspension file " + 
					Config.SUSPENSION_FILENAME, ioe);
		}
		
		logger.info("Initialized " + suspensions.size() + " suspensions.");
	}
}
