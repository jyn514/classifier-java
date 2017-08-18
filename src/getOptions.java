package src;

/**
 * @author Joshua Nelson
 * @version 0.1 (2017-9-17)
 * Classifier; a simple program to organize a directory
 * 
 * Copyright (C) 2017  Joshua Nelson
 */

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Scanner;

public class getOptions {

	private static Parameters parameters;
	private Scanner scanner = new Scanner(System.in);
	private static final Path MASTER = ReadOnly.MASTERCONF; 
	// got tired of typing

	getOptions(Parameters givenParameters) {

		parameters = givenParameters;

		if (parameters.version || parameters.showDefault || parameters.config
				|| parameters.editLocal || parameters.edit || parameters.reset
				|| parameters.help || parameters.copyright) {
			// probably could look neater
			parameters.exitImmediately = true;
			iterateArgs();

		} else if (parameters.directory.isEmpty()) { // checks for null string, not if dir is empty
			System.out.println(ReadOnly.HELP);
			parameters.exitImmediately = true;
		} else {

			if (ReadOnly.Validator.pathIsValid(parameters.directory)) {
				parameters.path = ReadOnly.Validator.path;
				try {
					parameters.parsedConfig = ReadOnly.parseConfigList(ReadOnly
							.getConf(parameters.path));
				} catch (IOException e) {
					System.out.println("Could not read config file");
					checkStackTraceOnError(e);
				}
			} else {
				System.out
						.println("Invalid directory argument. Type -h for help.");
				// TODO - make this message more informative
				parameters.exitImmediately = true;
			}
		}

	}

	private void iterateArgs() {
		if (parameters.version) {
			System.out.println(Classifier.VERSION);
		} else if (parameters.help) {
			ReadOnly.instance.usage();
		} else if (parameters.showDefault) {
			System.out.print(ReadOnly.listToString((ReadOnly.DEFAULT)));
		} else if (parameters.copyright) {
			System.out.println(ReadOnly.COPYRIGHT);
		} else if (parameters.reset && Files.exists(MASTER)) {
			System.out.print("Are you sure you want to delete " + MASTER
					+ " ? [y/n] ");
			String stdin = scanner.next().toLowerCase();
			if (stdin.equals("yes") || stdin.equals("y")) {
				try {
					Files.delete(MASTER);
				} catch (IOException e) {
					System.out.print("Could not delete " + MASTER
							+ ", make sure you have the proper permissions.");
					checkStackTraceOnError(e);
				}
			} else {
				System.out.println("Aborting.");
			}
		} else if (parameters.config) {
			try {
				System.out.print(ReadOnly.listToString(ReadOnly
						.getConf(parameters.path)));
			} catch (IOException e) {
				System.out.println("Could not access one or more files.");
				checkStackTraceOnError(e);
			}
		} else if (parameters.edit) {
			try {
				ReadOnly.edit(MASTER);
			} catch (IOException e) {
				System.out.println("Could not access " + MASTER);
				checkStackTraceOnError(e);
			}
		} else if (parameters.editLocal) {
			if (ReadOnly.Validator.pathIsValid(parameters.directory)) {
				parameters.path = ReadOnly.Validator.path;
				Path LOCAL = parameters.path.resolve(".classifier.conf");
				try {
					ReadOnly.edit(LOCAL);
				} catch (IOException e) {
					System.out.println("Could not access " + LOCAL);
					checkStackTraceOnError(e);
				}
			}
		}
	}

	private static void checkStackTraceOnError(IOException e) {
		if (parameters.verbose) {
			e.printStackTrace();
		} else {
			System.out.println("Run with -v for stack trace.");
		}
		parameters.exitImmediately = true;
	}
}
