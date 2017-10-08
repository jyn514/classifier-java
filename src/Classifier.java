package src;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * @author Joshua Nelson
 * @email jyn514@gmail.com
 * @version 0.1 (2017-10-11)
 *
 * Classifier; a simple program to organize a directory
 *
 * Copyright (C) 2017 Joshua Nelson
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

class Classifier {

  public static final String VERSION = "0.1";

  static Parameters parsedArgs = ReadOnly.parameters; // not actually parsed until ReadOnly.instance.parse(args) is
                                                     // executed

  private static final Path MASTER = ReadOnly.MASTERCONF; // got tired of typing

  private static final String INVALID_PATH = "Invalid path or cannot write to file. Type -h for help.";

  private static Path LOCAL; // shouldn't really be static

  public static void main(String[] args) {
    ReadOnly.instance.parse(args);

    if (ReadOnly.Validator.pathIsValid(parsedArgs.directory)) {
      parsedArgs.path = ReadOnly.Validator.path;
    } else {
      System.out.println(INVALID_PATH);
      return;
    }

    getOptions(parsedArgs);

    if (parsedArgs.exitImmediately) {
      return;
    }

    ReadOnly.classifyByExtension(parsedArgs.parsedConfig, parsedArgs.path);

  }

  static void getOptions(Parameters parameters) {

    if ((parameters.resetLocal || parameters.editLocal) && parameters.directory.isEmpty()) {
      System.out.println("Please specify a directory.");
      parameters.exitImmediately = true;

    } else if (parameters.version || parameters.showDefault || parameters.config || parameters.editLocal
        || parameters.edit || parameters.reset || parameters.resetLocal || parameters.help || parameters.copyright) {

      parameters.exitImmediately = true;
      LOCAL = getLocal(parameters);
      iterateArgs(parameters, new Scanner(System.in));

    } else if (parameters.directory.isEmpty()) {
      // checks for null string, not if dir is empty
      System.out.println(ReadOnly.HELP);
      parameters.exitImmediately = true;
    } else {
      try {
        parameters.parsedConfig = ReadOnly.parseConfigList(ReadOnly.getConf(parameters.path));
      } catch (IOException e) {
        System.out.println("Could not read config file");
        ReadOnly.checkStackTraceOnError(e);
      }

    }

  }

  private static void iterateArgs(Parameters parameters, Scanner scanner) {
    if (parameters.version) {
      System.out.println(Classifier.VERSION);
    } else if (parameters.help) {
      ReadOnly.instance.usage();
    } else if (parameters.showDefault) {
      System.out.print(ReadOnly.listToString(ReadOnly.DEFAULT));
    } else if (parameters.copyright) {
      System.out.println(ReadOnly.COPYRIGHT);
    } else if (parameters.reset) {
      reset(MASTER, scanner);
    } else if (parameters.resetLocal) {
      if (parameters.directory.isEmpty()) {
        System.out.println("Please specify a directory.");
        return;
      }
      reset(LOCAL, scanner);
    } else if (parameters.config) {
      try {
        System.out.print(ReadOnly.listToString(ReadOnly.getConf(parameters.path)));
      } catch (IOException e) {
        System.out.println("Could not access one or more files.");
        ReadOnly.checkStackTraceOnError(e);
      }
    } else if (parameters.edit) {
      try {
        ReadOnly.edit(MASTER);
      } catch (IOException e) {
        System.out.println("Could not access " + MASTER);
        ReadOnly.checkStackTraceOnError(e);
      }
    } else if (parameters.editLocal) {
      try {
        ReadOnly.edit(LOCAL);
      } catch (IOException e) {
        System.out.println("Could not access " + LOCAL);
        ReadOnly.checkStackTraceOnError(e);
      }
    }
  }

  private static Path getLocal(Parameters parameters) throws IllegalArgumentException {
    return parameters.path.resolve(".classifier.conf").toAbsolutePath().normalize();
  }

  private static void reset(Path p, Scanner scanner) {
    if (!Files.exists(p)) {
      return;
    }
    System.out.print("Are you sure you want to delete " + p + " ? [y/n] ");
    String stdin = scanner.next().toLowerCase();
    if (stdin.equals("yes") || stdin.equals("y")) {
      try {
        Files.delete(p);
      } catch (IOException e) {
        System.out.print("Could not delete " + p + ", make sure you have the proper permissions.");
        ReadOnly.checkStackTraceOnError(e);
      }
    } else {
      System.out.println("Aborting.");
    }
  }
}