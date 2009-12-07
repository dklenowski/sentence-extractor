package com.orbious.extractor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import org.apache.log4j.Logger;

/**
 * $Id: Name.java 12 2009-12-05 11:40:44Z app $
 * <p>
 * Provides static methods to perform lookups of common first names and surnames.
 * 
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class Name {
	
	/**
	 * 
	 */
	private static HashSet<String> names;

	/**
	 * Private  Constructor
	 */
	private Name() { }
	
	/**
	 * Checks to see whether a word is a common name.
	 * 
	 * @param wd	A word.
	 * @return		Returns <code>true</code> if the word is a common name,
	 * 				<code>false</code> otherwise.
	 */
	public static boolean isName(String wd) {
		if ( names == null ) {
			init();
		}
		
		if ( names.contains(wd) ) {
			return(true);
		}
		
		return(false);
	}
	
	/**
	 * Parses the {@link Config#NAMES_FILENAME} into memory.
	 */
	private static void init() {
		Logger logger;
		BufferedReader br = null;
		
		logger = Logger.getLogger(Config.LOGGER_REALM);
		try {
			br = new BufferedReader(
					new FileReader(Config.NAMES_FILENAME));
		} catch ( FileNotFoundException fnfe ) {
			logger.fatal("Failed to open names file " + 
					Config.NAMES_FILENAME, fnfe);
		}
		
		names = new HashSet<String>();

		try {
			String wd;
			while ( (wd = br.readLine()) != null ) {
				if ( !wd.matches("#.*") ) {
					names.add(wd);
				} 
			}
		} catch ( IOException ioe ) {
			logger.fatal("Failed to read names file " + 
					Config.NAMES_FILENAME, ioe);
		}
		
		logger.info("Initialized " + names.size() + " common names.");		
	}
	
	
}
