package src;

/**
 * @author Joshua Nelson
 * @version 0.1 (2017-9-17)
 * Classifier; a simple program to organize a directory
 * 
 * Copyright (C) 2017  Joshua Nelson
 */

import com.beust.jcommander.*;

import java.nio.file.Path;
import java.util.Map;
import java.util.List;

public class Parameters {
		
	@Parameter(description = "<Directory to organize>")
	String directory = "";
	
	@Parameter(names = {"--verbose", "-V" }, description="Increase output")
	boolean verbose = false;
	
	@Parameter(names = {"--help", "-h" }, description = "Show this help message")
	boolean help = false;
	
	@Parameter(names = {"--edit", "-e"}, description = "Edit the master config file")
	boolean edit = false;
	
	@Parameter(names = {"--edit-local", "-l"}, description = "Edit the local config file")
	boolean editLocal = false;
	
	@Parameter(names = {"--version", "-v"}, description = "Print the version and exit", order = 0)
	boolean version = false;
	
	@Parameter(names = {"--config", "-c"}, description = "View the current master config file")
	boolean config = false;
	
	@Parameter(names = {"--default", "--show-default", "-s"}, description = "Show the default configuration file")
	boolean showDefault = false;
	
	@Parameter(names = {"--reset", "-R"}, description = "Reset the master config file to its default value")
	boolean reset = false;
	
	@Parameter(names = {"--copyright", "--license", "-C"}, description = "Show copyright information")
	boolean copyright = false;

	boolean exitImmediately = false;
	
	Map<String, List<String>> parsedConfig;
	
	Path path;
	
}
