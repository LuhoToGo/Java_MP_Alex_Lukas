package de.uk.java.feader.utils;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * A custom logger for the Feader application
 */
public class FeaderLogger {
	
	private static final String LOGFILE_NAME = "logs/feader.log";
	private static Logger logger;
	
	private FeaderLogger() {}
	
	/**
	 * Returns the Logger instance for the Feader application
	 * @return
	 */
	public static Logger getLogger() {
		if (logger != null) return logger; // logger already initialized?
		logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME); // get global logger
		LogManager.getLogManager().reset(); // reset global log manager
		
		// configure log formatter
		System.setProperty("java.util.logging.SimpleFormatter.format",  "%1$tF %1$tT [%4$s] %2$s(): %5$s%6$s%n");
		SimpleFormatter formatter = new SimpleFormatter(); //init formatter
		
		//setup logging handler
		Handler loggingHandler = new Handler() {
			@Override
			public void publish(LogRecord record) {
				if (record.getLevel().intValue() <= Level.INFO.intValue()){
					System.out.println(getFormatter().format(record).trim());
				} else {
					System.err.println(getFormatter().format(record).trim());
				}
			}
			
			@Override
			public void flush() {}
			
			@Override
			public void close() throws SecurityException {}
		};
		
		loggingHandler.setFormatter(formatter); //set formatter
		loggingHandler.setLevel(Level.ALL); // log "ALL" log messages to console!
		logger.addHandler(loggingHandler); // add console handler to logger
		
		//setup file logging handler
		try {
			Handler logFileHandler = new FileHandler(
				LOGFILE_NAME, // log file name
				1048576, // maximum log file size (bytes)
				1, // maximum number of log files
				true); // should the logger append to existing log files?
			logFileHandler.setLevel(Level.WARNING); // file log only for messages with at least "INFO" level
			logFileHandler.setFormatter(formatter); // set formatter
			logger.addHandler(logFileHandler); // add file handler to logger
		} catch (SecurityException | IOException e) {
			System.err.println("Error setting up file logger");
		}
		
		// set global logging level
		logger.setLevel(Level.ALL); 
		return logger;
	}

}
